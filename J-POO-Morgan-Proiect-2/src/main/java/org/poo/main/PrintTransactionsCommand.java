package org.poo.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.fileio.UserInput;

import static org.poo.main.Helpers.userWithEmail;

final class PrintTransactionsCommand implements Command {
    @Override
    public void execute(final ArrayNode output, final CommandInput commandInput) {
        UserInput user = userWithEmail(commandInput.getEmail());
        if (user == null) {
            return;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode arrayNode = objectMapper.createArrayNode();
        for (Transaction transaction : user.getTransactions()) {
            ObjectNode transactionNode = objectMapper.createObjectNode();
            transaction.prepareTransactionNode(transactionNode);
            arrayNode.add(transactionNode);
        }
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", "printTransactions");
        objectNode.set("output", arrayNode);
        objectNode.put("timestamp", commandInput.getTimestamp());
        output.add(objectNode);
    }
}
