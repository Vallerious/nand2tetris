package contracts;

import constants.CommandType;
import constants.MemorySegment;

/**
 * Generates the assembly code from the parsed VMCommand.
 * Writes directly to the output stream.
 */
public interface ICodeWriter {

    /**
     * Writes to the output stream the assembly code for the current arithmetic operation.
     * @param command
     */
    void writeArithmetic(CommandType command);

    /**
     * Writes the assembly code of the push operation to the output stream.
     * @param segment
     * @param index
     */
    void writePush(MemorySegment segment, String index);

    void writePop(MemorySegment segment, String index);

    void writeLabel(String labelName);

    void writeGoto(String labelName);

    void writeConditionalGoto(String labelName);

    /**
     * Closes the output stream.
     */
    void close();
}
