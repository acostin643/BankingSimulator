package org.poo.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;

import static org.poo.main.Helpers.accountWithIban;
import static org.poo.main.Helpers.generateReportAccountNotFoundError;
import static org.poo.main.Helpers.generateSpendingsReportSavingsError;

final class SpendingsReportCommand implements Command {
    @Override
    public void execute(final ArrayNode output, final CommandInput commandInput) {
        Account account = accountWithIban(commandInput.getAccount());
        if (account == null) {
            generateReportAccountNotFoundError(output, commandInput, 1);
            return;
        }

        if (account.getAccountType().equals("savings")) {
            generateSpendingsReportSavingsError(output, commandInput);
            return;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", "spendingsReport");

        ObjectNode outputNode = objectMapper.createObjectNode();
        outputNode.put("IBAN", account.getIban());
        outputNode.put("balance", account.getBalance());
        outputNode.put("currency", account.getCurrency());


        ArrayNode allTransactionsNode = objectMapper.createArrayNode();

        for (OnlinePaymentTransaction spendingTransaction
                : account.getSpendingTransactions()) {
            if (spendingTransaction.getTimestamp() >= commandInput.getStartTimestamp()
                    && spendingTransaction.getTimestamp()
                    <= commandInput.getEndTimestamp()) {
                ObjectNode transactionNode = objectMapper.createObjectNode();
                spendingTransaction.prepareTransactionNode(transactionNode);
                allTransactionsNode.add(transactionNode);
            }
        }

        outputNode.set("transactions", allTransactionsNode);

        ArrayNode allCommerciantsNode = objectMapper.createArrayNode();
        for (Commerciant commerciant : account.getCommerciants()) {
            ObjectNode commerciantNode = objectMapper.createObjectNode();
            commerciantNode.put("commerciant", commerciant.getName());
            commerciantNode.put("total", commerciant.getAmountSpent());
            allCommerciantsNode.add(commerciantNode);
        }

        outputNode.set("commerciants", allCommerciantsNode);


        objectNode.put("output", outputNode);
        objectNode.put("timestamp", commandInput.getTimestamp());
        output.add(objectNode);
    }
}
