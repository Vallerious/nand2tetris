package contracts;

import constants.CommandType;
import constants.MemorySegment;

/**
 * - Handles parsing of a single .vm file
 * - Reads a VM command, parses the command into its lexical components, and provides convenient access to these components.
 * - Ignores all white space and comments.
 */
public interface IParser {

    /**
     * Checks if there are more commands in the input
     * @return if there are more commands to process
     */
    boolean hasMoreCommands();

    /**
     * Reads the next command from the input and makes it the 'current' command.
     * Should be called only if 'hasMoreCommands' is true.
     * Initially there is no current command.
     */
    void advance();

    /**
     * Parses the line and sets the respective class fields.
     * @param line
     */
    void parse(String line);


    /**
     * Gets the Command Type from the string of the input.
     * @return CommandType
     */
    CommandType getCommandType();

    /**
     * Returns the first argument of the current command. In the case of ARITHMETIC it returns the command itself.
     * @return String memory segment, sub, add
     */
    MemorySegment getArg1();

    /**
     * Returns the second argument of the command. Should be called only if current command is: PUSH, POP, FUNCTION, CALL
     * @return
     */
    String getArg2();

    /**
     * Closes the input.
     */
    void close();
}
