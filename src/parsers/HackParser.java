package parsers;

import com.sun.media.jfxmedia.logging.Logger;
import constants.CommandType;
import constants.MemorySegment;
import contracts.IParser;

import java.io.BufferedReader;
import java.io.IOException;

public class HackParser implements IParser {
    protected final static String INNER_COMMAND_SEPARATOR = "\\s+";
    protected BufferedReader reader;

    private String commandType;
    private String arg1;
    private String arg2;

    private boolean hasMoreCommands = true;

    public HackParser(BufferedReader reader) {
        this.reader = reader;
    }

    @Override
    public boolean hasMoreCommands() {
        return this.hasMoreCommands;
    }

    @Override
    public void advance() {
        try {
            String currentCommand = reader.readLine();

            if (currentCommand == null) {
                throw new IOException();
            }

            if (currentCommand.isEmpty() || currentCommand.indexOf("//") == 0) {
                return;
            }

            this.parse(currentCommand);
        } catch (IOException e) {
            this.close();
        }
    }

    @Override
    public void parse(String line) {
        if (line != null && !line.isEmpty()) {
            String[] cmdArgs = line.split(INNER_COMMAND_SEPARATOR);

            if (cmdArgs.length >= 1) {
                this.commandType = cmdArgs[0];
            }

            if (cmdArgs.length >= 2) {
                this.arg1 = cmdArgs[1];
            }

            if (cmdArgs.length >= 3) {
                this.arg2 = cmdArgs[2];
            }

            // Let's clear the not needed class fields.
            if (cmdArgs.length == 1) {
                this.arg1 = null;
                this.arg2 = null;
            } else if (cmdArgs.length == 2) {
                this.arg2 = null;
            }
        }
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.getCommandTypeByInputCmd(this.commandType);
    }

    @Override
    public MemorySegment getArg1() {
        return MemorySegment.getMemorySegmentByInputArgValue(this.arg1);
    }

    @Override
    public String getArg2() {
        return this.arg2;
    }

    public void close() {
        this.hasMoreCommands = false;
        this.commandType = null;
        this.arg1 = null;
        this.arg2 = null;

        try {
            this.reader.close();
        } catch (IOException e) {
            Logger.logMsg(Logger.ERROR, "There was a problem with the input resource. Please contact your administrator.");
        }
    }
}
