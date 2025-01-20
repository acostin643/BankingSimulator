package org.poo.main;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.fileio.CommandInput;

final class Handler {
    void handleCommand(final ArrayNode output, final CommandInput commandInput) {
        String commandName = commandInput.getCommand();
        Command command = CommandFactory.getCommand(commandName);
        if (command == null) {
            return;
        }
        command.execute(output, commandInput);
    }
}
