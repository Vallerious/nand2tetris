package writers;

import com.sun.media.jfxmedia.logging.Logger;
import constants.CommandType;
import constants.MemorySegment;
import contracts.ICodeWriter;
import translators.HackCodeGenerator;

import java.io.BufferedWriter;
import java.io.IOException;

public class HackWriter implements ICodeWriter {
    protected BufferedWriter assemblyOutput;
    protected HackCodeGenerator codeGenerator;

    private long labelIndex = 0L;

    public HackWriter(BufferedWriter assemblyOutput, HackCodeGenerator codeGenerator) {
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
            this.writeInBufferedWriter(HackCodeGenerator.ACCESSOR + segment.name());

            if (!MemorySegment.R5.equals(segment) && !MemorySegment.R3.equals(segment) && !MemorySegment.STATIC.equals(segment)) { // non-pointer addresses do not need this.
                this.writeInBufferedWriter(this.codeGenerator.setAddressRegister(HackCodeGenerator.MEMORY_REGISTER));
            }

            this.writeInBufferedWriter(this.codeGenerator.advanceAddressPointer(Integer.parseInt(index)));

            this.writeInBufferedWriter(this.codeGenerator.setDataRegister(HackCodeGenerator.MEMORY_REGISTER));
            this.writeInBufferedWriter(this.codeGenerator.selectStackPointer());
            this.writeInBufferedWriter(this.codeGenerator.setMemoryRegister(HackCodeGenerator.MEMORY_ADD_ONE));
            this.writeInBufferedWriter(this.codeGenerator.setAddressRegister(HackCodeGenerator.MEMORY_SUB_ONE));
            this.writeInBufferedWriter(this.codeGenerator.setMemoryRegister(HackCodeGenerator.DATA_REGISTER));
        }
    }

    @Override
    public void writePop(MemorySegment segment, String index) {
        this.writeInBufferedWriter("// pop");
        this.writeInBufferedWriter(this.codeGenerator.selectStackPointer());
        this.writeInBufferedWriter(this.codeGenerator.setMemoryRegister(HackCodeGenerator.MEMORY_SUB_ONE));
        this.writeInBufferedWriter(this.codeGenerator.setAddressRegister(HackCodeGenerator.MEMORY_REGISTER));
        this.writeInBufferedWriter(this.codeGenerator.setDataRegister(HackCodeGenerator.MEMORY_REGISTER));

        this.writeInBufferedWriter(HackCodeGenerator.ACCESSOR + segment.name());

        if (!MemorySegment.R5.equals(segment) && !MemorySegment.R3.equals(segment) && !MemorySegment.STATIC.equals(segment)) { // non-pointer addresses do not need this.
            this.writeInBufferedWriter(this.codeGenerator.setAddressRegister(HackCodeGenerator.MEMORY_REGISTER));
        }

        this.writeInBufferedWriter(this.codeGenerator.advanceAddressPointer(Integer.parseInt(index)));

        this.writeInBufferedWriter(this.codeGenerator.setMemoryRegister(HackCodeGenerator.DATA_REGISTER));
    }

    public void writeLabel(String labelName) {
        this.writeInBufferedWriter("// label");
        this.writeInBufferedWriter("(" + labelName + ")");
    }

    public void writeGoto(String labelName) {
        this.writeInBufferedWriter("// goto");
        this.writeInBufferedWriter(HackCodeGenerator.ACCESSOR + labelName);
        this.writeInBufferedWriter("0;JMP");
    }

    public void writeConditionalGoto(String label) {
        this.writeInBufferedWriter("// if goto");
        this.writeInBufferedWriter(this.codeGenerator.selectStackPointer());
        this.writeInBufferedWriter(this.codeGenerator.setMemoryRegister(HackCodeGenerator.MEMORY_SUB_ONE));
        this.writeInBufferedWriter(this.codeGenerator.setAddressRegister(HackCodeGenerator.MEMORY_REGISTER));
        this.writeInBufferedWriter(this.codeGenerator.setDataRegister(HackCodeGenerator.MEMORY_REGISTER));
        this.writeInBufferedWriter(HackCodeGenerator.ACCESSOR + label);
        this.writeInBufferedWriter("D;JGT");
    }

    private void add() {
        this.writeInBufferedWriter("// add");
        this.storeTopStackValueAndPointToNext();
        this.writeInBufferedWriter(this.codeGenerator.setDataRegister(HackCodeGenerator.DATA_PLUS_MEMORY_REGISTERS));
        this.writeInBufferedWriter(this.codeGenerator.setMemoryRegister(HackCodeGenerator.DATA_REGISTER));
    }

    private void sub() {
        this.writeInBufferedWriter("// sub");
        this.storeTopStackValueAndPointToNext();
        this.writeInBufferedWriter(this.codeGenerator.setDataRegister(HackCodeGenerator.MEMORY_REGISTER + "-" + HackCodeGenerator.DATA_REGISTER));
        this.writeInBufferedWriter(this.codeGenerator.setMemoryRegister(HackCodeGenerator.DATA_REGISTER));
    }

    private void neg() {
        this.writeInBufferedWriter("// neg");
        this.writeInBufferedWriter(this.codeGenerator.selectStackPointer());
        this.writeInBufferedWriter(this.codeGenerator.setAddressRegister(HackCodeGenerator.MEMORY_SUB_ONE));
        this.writeInBufferedWriter(this.codeGenerator.setMemoryRegister(HackCodeGenerator.NEGATE_MEMORY_REGISTER));
    }

    private void and() {
        this.writeInBufferedWriter("// and");
        this.storeTopStackValueAndPointToNext();
        this.writeInBufferedWriter(this.codeGenerator.setDataRegister(HackCodeGenerator.MEMORY_REGISTER + "&" + HackCodeGenerator.DATA_REGISTER));
        this.writeInBufferedWriter(this.codeGenerator.setMemoryRegister(HackCodeGenerator.DATA_REGISTER));
    }

    private void or() {
        this.writeInBufferedWriter("// or");
        this.storeTopStackValueAndPointToNext();
        this.writeInBufferedWriter(this.codeGenerator.setDataRegister(HackCodeGenerator.MEMORY_REGISTER + "|" + HackCodeGenerator.DATA_REGISTER));
        this.writeInBufferedWriter(this.codeGenerator.setMemoryRegister(HackCodeGenerator.DATA_REGISTER));
    }

    // "@0\nA=M-1\nM=!M\n"
    private void not() {
        this.writeInBufferedWriter("// not");
        this.writeInBufferedWriter(this.codeGenerator.selectStackPointer());
        this.writeInBufferedWriter(this.codeGenerator.setAddressRegister(HackCodeGenerator.MEMORY_SUB_ONE));
        this.writeInBufferedWriter(this.codeGenerator.setMemoryRegister(HackCodeGenerator.NOT_MEMORY_REGISTER));
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
        this.writeInBufferedWriter(HackCodeGenerator.ACCESSOR + val);
        this.writeInBufferedWriter(this.codeGenerator.setDataRegister(HackCodeGenerator.ADDRESS_REGISTER));
        this.writeInBufferedWriter(this.codeGenerator.selectStackPointer());
        this.writeInBufferedWriter(this.codeGenerator.setMemoryRegister(HackCodeGenerator.MEMORY_ADD_ONE));
        this.writeInBufferedWriter(this.codeGenerator.setAddressRegister(HackCodeGenerator.MEMORY_SUB_ONE));
        this.writeInBufferedWriter(this.codeGenerator.setMemoryRegister(HackCodeGenerator.DATA_REGISTER));
    }

    private void cmp(String op, String jumpAction) {
        this.storeTopStackValueAndPointToNext();
        this.writeInBufferedWriter(this.codeGenerator.setDataRegister(HackCodeGenerator.DATA_REGISTER + "-" + HackCodeGenerator.MEMORY_REGISTER));
        this.writeInBufferedWriter(HackCodeGenerator.ACCESSOR + op + ".cmp." + this.labelIndex);
        this.writeInBufferedWriter(HackCodeGenerator.DATA_REGISTER + ";" + jumpAction);
        this.writeInBufferedWriter(this.codeGenerator.selectStackPointer());
        this.writeInBufferedWriter(this.codeGenerator.setAddressRegister(HackCodeGenerator.MEMORY_SUB_ONE));
        this.writeInBufferedWriter(HackCodeGenerator.ACCESSOR + "END." + this.labelIndex);
        this.writeInBufferedWriter("0;JMP");
        this.writeInBufferedWriter("(" + op + ".cmp." + this.labelIndex + ")");
        this.writeInBufferedWriter(this.codeGenerator.selectStackPointer());
        this.writeInBufferedWriter(this.codeGenerator.setAddressRegister(HackCodeGenerator.MEMORY_SUB_ONE));
        this.writeInBufferedWriter(this.codeGenerator.setMemoryRegister(HackCodeGenerator.TRUE));
        this.writeInBufferedWriter("(END." + labelIndex + ")");
        labelIndex++;
    }

    private void storeTopStackValueAndPointToNext() {
        this.writeInBufferedWriter(this.codeGenerator.selectStackPointer());
        this.writeInBufferedWriter(this.codeGenerator.setMemoryRegister(HackCodeGenerator.MEMORY_SUB_ONE));
        this.writeInBufferedWriter(this.codeGenerator.setAddressRegister(HackCodeGenerator.MEMORY_REGISTER));
        this.writeInBufferedWriter(this.codeGenerator.setDataRegister(HackCodeGenerator.MEMORY_REGISTER));
        this.writeInBufferedWriter(this.codeGenerator.setAddressRegister(HackCodeGenerator.ADDRESS_REGISTER + "-" + HackCodeGenerator.ONE));
    }

    private void writeInBufferedWriter(String text) {
        try {
            this.assemblyOutput.write(text);
            this.assemblyOutput.newLine();
        } catch (IOException e) {
            this.close();
        }
    }

    @Override
    public void close() {
        try {
            this.assemblyOutput.close();
        } catch (IOException e) {
            Logger.logMsg(Logger.ERROR, "Could not close writer resource");
        }
    }
}
