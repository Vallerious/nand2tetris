package writers;

import com.sun.media.jfxmedia.logging.Logger;
import constants.CommandType;
import constants.MemorySegment;
import contracts.ICodeWriter;
import translators.ICodeGenerator;

import java.io.BufferedWriter;
import java.io.IOException;

public class HackWriter implements ICodeWriter {
    protected BufferedWriter assemblyOutput;
    protected ICodeGenerator codeGenerator;

    private long labelIndex = 0L;

    public HackWriter(BufferedWriter assemblyOutput, ICodeGenerator codeGenerator) {
        this.assemblyOutput = assemblyOutput;
        this.codeGenerator = codeGenerator;
    }

    @Override
    public void writeArithmetic(CommandType command) {
        if (this.assemblyOutput != null && this.codeGenerator != null) {
            switch (command) {
                case ADD:
                    this.add();
                    break;
                case SUB:
                    this.sub();
                    break;
                case NEG:
                    this.neg();
                    break;
                case AND:
                    this.and();
                    break;
                case OR:
                    this.or();
                    break;
                case NOT:
                    this.not();
                    break;
                case EQ:
                    this.eq();
                    break;
                case LT:
                    this.lt();
                    break;
                case GT:
                    this.gt();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void writePush(MemorySegment segment, String index) {
        this.writeInBufferedWriter("// push");

        if (MemorySegment.CONSTANT.equals(segment)) {
            this.constant(index);
        } else {
            // "@k\nA=M\nD=M\n@0\nM=M+1\nA=M-1\nM=D\n"
            this.writeInBufferedWriter("@" + segment.name());
            this.writeInBufferedWriter(this.codeGenerator.goToAddressByPointerValue());
            this.writeInBufferedWriter("D=M");
            this.writeInBufferedWriter(this.codeGenerator.selectStackPointer());
            this.writeInBufferedWriter("M=M+1");
            this.writeInBufferedWriter(this.codeGenerator.goToPreviousPointerValue());
            this.writeInBufferedWriter("M=D");
        }
    }

    // "@0\nM=M-1\nA=M\nD=M\n@k\nA=M\nM=D\n"
    @Override
    public void writePop(MemorySegment segment, String index) {
        this.writeInBufferedWriter("// pop");
        this.writeInBufferedWriter(this.codeGenerator.selectStackPointer());
        this.writeInBufferedWriter("M=M-1");
        this.writeInBufferedWriter("A=M");
        this.writeInBufferedWriter("D=M");

        if (MemorySegment.TEMP.equals(segment)) {
            segment = MemorySegment.R5;
        }

        this.writeInBufferedWriter("@" + segment.name());
        this.writeInBufferedWriter("A=M");

        for (int i = 0; i < Integer.parseInt(index); i++) {
            this.writeInBufferedWriter("A=A+1");
        }

        this.writeInBufferedWriter("M=D");
    }

    @Override
    public void close() {
        try {
            this.assemblyOutput.close();
        } catch (IOException e) {
            Logger.logMsg(Logger.ERROR, "Could not close writer resource");
        }
    }

    private void add() {
        this.writeInBufferedWriter("// add");
        this.storeTopStackValueAndPointToNext();
        this.writeInBufferedWriter(this.codeGenerator.increaseDataRegisterWithMemoryRegister());
        this.writeInBufferedWriter(this.codeGenerator.setMemoryRegisterToDataRegisterValue());
    }

    private void sub() {
        this.writeInBufferedWriter("// sub");
        this.storeTopStackValueAndPointToNext();
        this.writeInBufferedWriter(this.codeGenerator.setDataRegisterToMemoryRegisterMinusDataRegister());
        this.writeInBufferedWriter(this.codeGenerator.setMemoryRegisterToDataRegisterValue());
    }

    private void neg() {
        this.writeInBufferedWriter("// neg");
        this.writeInBufferedWriter(this.codeGenerator.selectStackPointer());
        this.writeInBufferedWriter(this.codeGenerator.setDataRegisterToAddressVal());
        this.writeInBufferedWriter(this.codeGenerator.goToPreviousPointerValue());
        this.writeInBufferedWriter(this.codeGenerator.setMemoryRegisterToDataRegMinusMemoryReg());
    }

    // @0\nM=M-1\nA=M\nD=M\nM=0\nA=A-1\nD=D&M\nM=D\n
    private void and() {
        this.writeInBufferedWriter("// and");
        this.storeTopStackValueAndPointToNext();
        this.writeInBufferedWriter(this.codeGenerator.andDataAndMemoryRegs());
        this.writeInBufferedWriter(this.codeGenerator.setMemoryRegisterToDataRegisterValue());
    }

    private void or() {
        this.writeInBufferedWriter("// or");
        this.storeTopStackValueAndPointToNext();
        this.writeInBufferedWriter(this.codeGenerator.orDataAndMemoryRegs());
        this.writeInBufferedWriter(this.codeGenerator.setMemoryRegisterToDataRegisterValue());
    }

    // "@0\nA=M-1\nM=!M\n"
    private void not() {
        this.writeInBufferedWriter("// not");
        this.writeInBufferedWriter(this.codeGenerator.selectStackPointer());
        this.writeInBufferedWriter(this.codeGenerator.goToPreviousPointerValue());
        this.writeInBufferedWriter(this.codeGenerator.negateMemoryRegister());
    }

    // @0\nM=M-1\nA=M\nD=M\nM=0\nA=A-1\nD=D-M\n@EQ.cmp.x\nD;JEQ\n@0\nA=M-1\nM=0\n@END.x\n0;JMP\n(EQ.cmp.x)\n@0\nA=M-1\nM=-1\n(END.x)\n
    private void eq() {
        this.writeInBufferedWriter("// eq");
        this.cmp("EQ", "JEQ");
    }

    private void gt() {
        this.writeInBufferedWriter("// gt");
        this.cmp("GT", "JLT");
    }

    private void lt() {
        this.writeInBufferedWriter("// lt");
        this.cmp("LT", "JGT");
    }

    private void constant(String val) {
        this.writeInBufferedWriter("// constant");
        this.writeInBufferedWriter("@" + val);
        this.writeInBufferedWriter(this.codeGenerator.setDataRegisterToAddressVal());
        this.writeInBufferedWriter(this.codeGenerator.selectStackPointer());
        this.writeInBufferedWriter("M=M+1");
        this.writeInBufferedWriter(this.codeGenerator.goToPreviousPointerValue());
        this.writeInBufferedWriter(this.codeGenerator.setMemoryRegisterToDataRegisterValue());
    }

    private void cmp(String op, String jumpAction) {
        this.storeTopStackValueAndPointToNext();
        this.writeInBufferedWriter(this.codeGenerator.setDataRegisterToDataRegisterMinusMemoryReg());
        this.writeInBufferedWriter("@" + op + ".cmp." + this.labelIndex);
        this.writeInBufferedWriter("D;" + jumpAction);
        this.writeInBufferedWriter(this.codeGenerator.selectStackPointer());
        this.writeInBufferedWriter(this.codeGenerator.goToPreviousPointerValue());
        this.writeInBufferedWriter(this.codeGenerator.clearMemoryRegister());
        this.writeInBufferedWriter("@END." + this.labelIndex);
        this.writeInBufferedWriter("0;JMP");
        this.writeInBufferedWriter("(" + op + ".cmp." + this.labelIndex + ")");
        this.writeInBufferedWriter(this.codeGenerator.selectStackPointer());
        this.writeInBufferedWriter(this.codeGenerator.goToPreviousPointerValue());
        this.writeInBufferedWriter(this.codeGenerator.setMemoryToTrue());
        this.writeInBufferedWriter("(END." + labelIndex + ")");
        labelIndex++;
    }

    private void storeTopStackValueAndPointToNext() {
        this.writeInBufferedWriter(this.codeGenerator.selectStackPointer());
        this.writeInBufferedWriter(this.codeGenerator.decrementMemValue());
        this.writeInBufferedWriter(this.codeGenerator.goToAddressByPointerValue());
        this.writeInBufferedWriter(this.codeGenerator.storeMemoryRegisterToDataRegister());
        this.writeInBufferedWriter(this.codeGenerator.clearMemoryRegister());
        this.writeInBufferedWriter(this.codeGenerator.goToPreviousAddress());
    }

    private void writeInBufferedWriter(String text) {
        try {
            this.assemblyOutput.write(text);
            this.assemblyOutput.newLine();
        } catch (IOException e) {
            this.close();
        }
    }
}
