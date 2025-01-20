package org.poo.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;

import static org.poo.main.Helpers.accountWithIban;
import static org.poo.main.Helpers.generateReportAccountNotFoundError;


final class ReportCommand implements Command {
    @Override
    public void execute(final ArrayNode output, final CommandInput commandInput) {
        Account account = accountWithIban(commandInput.getAccount());
        if (account == null) {
            generateReportAccountNotFoundError(output, commandInput, 0);
            return;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", "report");

        ObjectNode outputNode = objectMapper.createObjectNode();
        outputNode.put("IBAN", account.getIban());
        outputNode.put("balance", account.getBalance());
        outputNode.put("currency", account.getCurrency());
        ArrayNode allTransactionsNode = objectMapper.createArrayNode();
        for (Transaction transaction : account.getTransactions()) {
            // se parcurg tranzactiile asociate contului
            if (transaction.getTimestamp() >= commandInput.getStartTimestamp()
                    && transaction.getTimestamp()
                    <= commandInput.getEndTimestamp()) {
                ObjectNode transactionNode = objectMapper.createObjectNode();
                transaction.prepareTransactionNode(transactionNode);
                allTransactionsNode.add(transactionNode);
            }
        }
        outputNode.set("transactions", allTransactionsNode);
        objectNode.put("output", outputNode);
        objectNode.put("timestamp", commandInput.getTimestamp());
        output.add(objectNode);
    }
}
