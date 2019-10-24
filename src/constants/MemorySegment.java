package constants;

import java.util.HashMap;
import java.util.Map;

public enum MemorySegment {
    LCL("local"),
    ARG("argument"),
    THIS("this"),
    THAT("that"),
    TEMP("temp"),
    STATIC("static"),
    POINTER("pointer"),
    SCREEN(""),
    KBD(""),
    R0(""),
    R1(""),
    R2(""),
    R3("R3"),
    R4(""),
    R5("R5"),
    R6(""),
    R7(""),
    R8(""),
    R9(""),
    R10(""),
    R11(""),
    R12(""),
    R13(""),
    R14(""),
    R15(""),
    R16("R16"),

    CONSTANT("constant"),

    NOP("");

    private final static Map<String, MemorySegment> BY_INPUT_VAL = new HashMap<>();

    private String inputVal;

    private MemorySegment(String inputVal) {
        this.inputVal = inputVal;
    }

    static {
        for (MemorySegment ms : values()) {
            BY_INPUT_VAL.put(ms.inputVal, ms);
        }
    }

    public static MemorySegment getMemorySegmentByInputArgValue(String inputArgVal) {
        if (BY_INPUT_VAL.containsKey(inputArgVal)) {
            return BY_INPUT_VAL.get(inputArgVal);
        }

        return MemorySegment.NOP;
    }
}
