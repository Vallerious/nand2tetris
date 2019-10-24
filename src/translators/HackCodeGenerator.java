package translators;

import errors.SyntaxError;

public class HackCodeGenerator {

    public final static String FALSE = "0";
    public final static String TRUE = "-1";
    public final static String ONE = "1";
    public final static String MINUS_ONE = TRUE;
    public final static String DATA_REGISTER = "D";
    public final static String ADDRESS_REGISTER = "A";
    public final static String MEMORY_REGISTER = "M";
    public final static String NOT = "!";
    public final static String NOT_DATA_REGISTER = NOT + DATA_REGISTER;
    public final static String NOT_ADDRESS_REGISTER = NOT + ADDRESS_REGISTER;
    public final static String NOT_MEMORY_REGISTER = NOT + MEMORY_REGISTER;
    public final static String NEGATE_DATA_REGISTER = "-" + DATA_REGISTER;
    public final static String NEGATE_ADDRESS_REGISTER = "-" + ADDRESS_REGISTER;
    public final static String NEGATE_MEMORY_REGISTER = "-" + MEMORY_REGISTER;
    public final static String MEMORY_ADD_ONE = MEMORY_REGISTER + "+" + ONE;
    public final static String MEMORY_SUB_ONE = MEMORY_REGISTER + "-" + ONE;
    public final static String DATA_PLUS_MEMORY_REGISTERS = DATA_REGISTER + "+" + MEMORY_REGISTER;
    public final static String ACCESSOR = "@";

    public String selectStackPointer() {
        return "@SP";
    }

    // Let's rewrite this beach
    public String setMemoryRegister(String rightHandSide) {
        return "M=" + rightHandSide;
    }

    public String setAddressRegister(String rightHandSide) {
        return "A=" + rightHandSide;
    }

    public String setDataRegister(String rightHandSide) {
        return "D=" + rightHandSide;
    }

    public String advanceAddressPointer(int offset) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < offset; i++) {
            sb.append("A=A+1").append(System.lineSeparator());
        }

        return sb.toString();
    }
}
