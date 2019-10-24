import com.sun.media.jfxmedia.logging.Logger;
import constants.CommandType;
import constants.MemorySegment;
import parsers.HackParser;
import translators.HackCodeGenerator;
import writers.HackWriter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

    // 1. End every program with infinite loop.
    // 2. use R0, R1, R2...for addressing, SCREEN, KBD
    // 3. use SP, LCL, ARG, THIS, THAT - these are built-in so you can use`em
    public static void main(String[] args) {
        try {
            BufferedReader reader = Files.newBufferedReader(Paths.get("src/test.txt"));
            HackParser hackParser = new HackParser(reader);
            File outputFile = new File(Paths.get("").toAbsolutePath().toString() + "/src/output.asm");

            if (!outputFile.exists()) {
                outputFile.createNewFile();
            }

            HackWriter hackWriter = new HackWriter(new BufferedWriter(new FileWriter(outputFile)), new HackCodeGenerator());

            while (hackParser.hasMoreCommands()) {
                hackParser.advance();

                CommandType commandType = hackParser.getCommandType();

                // Do some translations of the addresses beforehand
                MemorySegment segment = hackParser.getArg1();
                if (MemorySegment.TEMP.equals(segment)) {
                    segment = MemorySegment.R5;
                } else if (MemorySegment.POINTER.equals(segment)) {
                    segment = MemorySegment.R3;
                } else if (MemorySegment.STATIC.equals(segment)) {
                    segment = MemorySegment.R16;
                }


                if (CommandType.isArithmeticLogicalOp(commandType)) {
                    hackWriter.writeArithmetic(commandType);
                } else if (CommandType.POP.equals(commandType)) {
                    hackWriter.writePop(segment, hackParser.getArg2());
                } else if (CommandType.PUSH.equals(commandType)) {
                    hackWriter.writePush(segment, hackParser.getArg2());
                } else if (CommandType.LABEL.equals(commandType)) {
                    hackWriter.writeLabel(hackParser.arg1);
                } else if (CommandType.GOTO.equals(commandType)) {
                    hackWriter.writeGoto(hackParser.arg1);
                } else if (CommandType.IF.equals(commandType)) {
                    hackWriter.writeConditionalGoto(hackParser.arg1);
                }
            }

            hackWriter.close();
            hackParser.close();
        } catch (IOException e) {
            Logger.logMsg(Logger.ERROR, "There was a problem reading from or writing to file");
        }
    }
}
