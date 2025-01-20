package org.poo.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.CommandInput;

import java.util.ArrayList;

@Getter
@Setter
public class CustomSplitPaymentTransaction extends Transaction {
    private double totalSum;
    private ArrayList<Double> amountForUsers = new ArrayList<>();
    private String currency;
    private ArrayList<String> involvedAccounts = new ArrayList<>();
    private final String splitPaymentType;
    private String error = null;

    public CustomSplitPaymentTransaction(final CommandInput command) {
        super(command);
        this.involvedAccounts = (ArrayList<String>) command.getAccounts();
        if (command.getSplitPaymentType().equals("custom")) {
            this.amountForUsers = (ArrayList<Double>) command.getAmountForUsers();
        } else {
            this.amountForUsers.add(0.0);
        }
        this.splitPaymentType = command.getSplitPaymentType();
        this.currency = command.getCurrency();
        this.totalSum = 0;
        for (double amount : amountForUsers) {
            this.totalSum += amount;
        }
    }

    /**
     * @param transactionNode este nodul de tranzactie care va fi
     *                        adaugat la printTransactions, modificat in mod unic de
     *                        fiecare tip de tranzactie
     */
    @Override
    public void prepareTransactionNode(final ObjectNode transactionNode) {
        super.prepareTransactionNode(transactionNode);
        ObjectMapper objectMapper = new ObjectMapper();

        ArrayNode amountForUsersNode = objectMapper.createArrayNode();
        if (this.getAmountForUsers() != null) {
            for (double amount : this.amountForUsers) {
                amountForUsersNode.add(amount);
            }
        }

        transactionNode.set("amountForUsers", amountForUsersNode);
        transactionNode.put("currency", this.currency);
        transactionNode.put("splitPaymentType", this.splitPaymentType);
        transactionNode.put("description",
                "Split payment of "
                        + String.format("%.2f", this.totalSum)
                        + " " + this.currency);
        ArrayNode accountsNode = objectMapper.createArrayNode();
        for (String involvedAccount : involvedAccounts) {
            accountsNode.add(involvedAccount);
        }
        transactionNode.set("involvedAccounts", accountsNode);
        transactionNode.put("timestamp", this.getTimestamp());
        if (this.error != null) {
            transactionNode.put("error", this.error);
        }
    }

    /**
     * @param culprit
     */
    public void prepareError(final String culprit) {
        this.setError("Account " + culprit
                + " has insufficient funds for a split payment.");
    }
}
