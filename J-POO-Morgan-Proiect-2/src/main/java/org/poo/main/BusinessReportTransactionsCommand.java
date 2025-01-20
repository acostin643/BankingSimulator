package org.poo.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;

import static org.poo.main.Helpers.accountWithIban;
import static org.poo.main.Helpers.generateReportAccountNotFoundError;
import static org.poo.main.Helpers.formatEmailToName;

public class BusinessReportTransactionsCommand implements Command {
    @Override
    public void execute(ArrayNode output, CommandInput commandInput) {
        Account account = accountWithIban(commandInput.getAccount());
        if (account == null) {
            generateReportAccountNotFoundError(output, commandInput, 1);
            return;
        }

        if (account.getAccountType().equals("business")) {
            //generateSpendingsReportSavingsError(output, commandInput);
            System.out.println("nu e de business sefu");
            return;
        }

        double totalSpentInTimespan = 0;
        double totalDepositedInTimespan = 0;


        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", "businessReport");

        ObjectNode outputNode = objectNode.objectNode();
        outputNode.put("IBAN", account.getIban());
        outputNode.put("balance", account.getBalance());
        outputNode.put("currency", account.getCurrency());
        outputNode.put("spending limit", ((BusinessAccount) account).getSpendingLimit());
        outputNode.put("deposit limit", ((BusinessAccount) account).getDepositingLimit());
        outputNode.put("statistics type", "transaction");

        ArrayNode managersArray = objectMapper.createArrayNode();
        for (Associate manager : ((BusinessAccount) account).getManagerAssociates()) {
            ObjectNode managerNode = objectNode.objectNode();
            managerNode.put("username", formatEmailToName(manager.getEmail()));
            managerNode.put("spent", manager.getAmountSpent());
        }
    }
}
