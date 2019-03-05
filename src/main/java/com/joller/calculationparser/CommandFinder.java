package com.joller.calculationparser;

import static com.joller.calculationparser.Command.*;

class CommandFinder {

    private final String commandSetAsString = SET.toString().toLowerCase();
    private final String commandCommentAsString = "c";
    private final String commandCalcfAsString = CALCF.toString().toLowerCase();

    public CommandFinder() {
    }

    public Command getCommandType(String line) throws IllegalArgumentException {
        int indexColon = line.indexOf(':');
        if (indexColon == -1) throw new IllegalArgumentException("Missing colon, impossible to " +
                "get a command");
        String command = line.substring(0, indexColon);
        if (isCommandSet(indexColon, command)) return SET;
        if (isCommandComment(indexColon, command)) return COMMENT;
        if (isCommandCalcf(indexColon, command)) return CALCF;
        return UNKNOWN;
    }

    private boolean isCommandCalcf(int indexColon, String command) {
        if (command.contains(commandCalcfAsString)) {
            int leftParenthesis = command.indexOf('(');
            int rightParenthesis = command.indexOf(')');
            if (leftParenthesis < rightParenthesis && leftParenthesis != -1
            && (rightParenthesis + 1 == indexColon)) {
                return true;
            }
        }
        return false;
    }

    private boolean isCommandComment(int indexColon, String command) {
        if (command.equals(commandCommentAsString) && indexColon == commandCommentAsString.length()) {
            return true;
        }
        return false;
    }

    private boolean isCommandSet(int indexColon, String command) {
        if (command.equals(commandSetAsString) && indexColon == commandSetAsString.length()) {
            return true;
        }
        return false;
    }


}
