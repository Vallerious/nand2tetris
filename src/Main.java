import com.sun.media.jfxmedia.logging.Logger;
import constants.CommandType;
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

                if (CommandType.isArithmeticLogicalOp(commandType)) {
                    hackWriter.writeArithmetic(commandType);
                } else if (CommandType.POP.equals(commandType)) {
                    hackWriter.writePop(hackParser.getArg1(), hackParser.getArg2());
                } else if (CommandType.PUSH.equals(commandType)) {
                    hackWriter.writePush(hackParser.getArg1(), hackParser.getArg2());
                }
            }

            hackWriter.close();
            hackParser.close();
        } catch (IOException e) {
            Logger.logMsg(Logger.ERROR, "There was a problem reading from or writing to file");
        }
    }
}
