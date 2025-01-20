package org.poo.main;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.fileio.CommandInput;

public interface Command {
    /**
     * @param output       este output-ul oferit comenzii
     * @param commandInput este input-ul oferit comenzii
     */
    void execute(ArrayNode output, CommandInput commandInput);
}
