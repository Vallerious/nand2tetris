package translators;

public interface ICodeGenerator {

    String selectStackPointer();

    String decrementMemValue();

    String goToAddressByPointerValue();

    String storeMemoryRegisterToDataRegister();

    String clearMemoryRegister();

    String goToPreviousAddress();

    String increaseDataRegisterWithMemoryRegister();

    String setMemoryRegisterToDataRegisterValue();

    String setDataRegisterToMemoryRegisterMinusDataRegister();

    String setMemoryRegisterToDataRegMinusMemoryReg();

    String setDataRegisterToAddressVal();

    String goToPreviousPointerValue();

    String andDataAndMemoryRegs();

    String orDataAndMemoryRegs();

    String negateMemoryRegister();

    String setDataRegisterToDataRegisterMinusMemoryReg();

    String setMemoryToTrue();
}
