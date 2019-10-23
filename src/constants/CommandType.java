package constants;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public enum CommandType {
    ADD("add"),
    SUB("sub"),
    AND("and"),
    OR("or"),
    NOT("not"),
    GT("gt"),
    LT("lt"),
    EQ("eq"),
    NEG("neg"),
    PUSH("push"),
    POP("pop"),
    LABEL("label"),
    GOTO("goto"),
    IF("goto-if"),
    FUNCTION("function"),
    RETURN("return"),
    CALL("call"),
    NOP("");

    private final static Map<String, CommandType> BY_INPUT_CMD = new HashMap<String, CommandType>();
    private final static Set<CommandType> ARITHMETIC_LOGICAL_CMDS = new HashSet<>();

    private CommandType(String inputCmd) {
        this.inputCmd = inputCmd;
    }

    static {
        for (CommandType ct : values()) {
            BY_INPUT_CMD.put(ct.inputCmd, ct);
        }
        ARITHMETIC_LOGICAL_CMDS.add(ADD);
        ARITHMETIC_LOGICAL_CMDS.add(SUB);
        ARITHMETIC_LOGICAL_CMDS.add(AND);
        ARITHMETIC_LOGICAL_CMDS.add(OR);
        ARITHMETIC_LOGICAL_CMDS.add(NOT);
        ARITHMETIC_LOGICAL_CMDS.add(GT);
        ARITHMETIC_LOGICAL_CMDS.add(LT);
        ARITHMETIC_LOGICAL_CMDS.add(EQ);
        ARITHMETIC_LOGICAL_CMDS.add(NEG);
    }

    public final String inputCmd;

    public static CommandType getCommandTypeByInputCmd(String inputCmd) {
        if (BY_INPUT_CMD.containsKey(inputCmd)) {
            return BY_INPUT_CMD.get(inputCmd);
        }

        return CommandType.NOP;
    }

    public static boolean isArithmeticLogicalOp(CommandType cmdType) {
        return ARITHMETIC_LOGICAL_CMDS.contains(cmdType);
    }
}
