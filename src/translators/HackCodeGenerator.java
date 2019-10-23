package translators;

public class HackCodeGenerator implements ICodeGenerator {

    @Override
    public String selectStackPointer() {
        return "@SP";
    }

    @Override
    public String decrementMemValue() {
        return "M=M-1";
    }

    @Override
    public String goToAddressByPointerValue() {
        return "A=M";
    }

    @Override
    public String storeMemoryRegisterToDataRegister() {
        return "D=M";
    }

    @Override
    public String clearMemoryRegister() {
        return "M=0";
    }

    @Override
    public String goToPreviousAddress() {
        return "A=A-1";
    }

    @Override
    public String increaseDataRegisterWithMemoryRegister() {
        return "D=D+M";
    }

    @Override
    public String setMemoryRegisterToDataRegisterValue() {
        return "M=D";
    }

    @Override
    public String setDataRegisterToMemoryRegisterMinusDataRegister() {
        return "D=M-D";
    }

    @Override
    public String setMemoryRegisterToDataRegMinusMemoryReg() {
        return "M=D-M";
    }

    @Override
    public String setDataRegisterToAddressVal() {
        return "D=A";
    }

    @Override
    public String goToPreviousPointerValue() {
        return "A=M-1";
    }

    @Override
    public String andDataAndMemoryRegs() {
        return "D=D&M";
    }

    @Override
    public String orDataAndMemoryRegs() {
        return "D=D|M";
    }

    @Override
    public String negateMemoryRegister() {
        return "M=!M";
    }

    @Override
    public String setDataRegisterToDataRegisterMinusMemoryReg() {
        return "D=D-M";
    }

    @Override
    public String setMemoryToTrue() {
        return "M=-1";
    }


}
