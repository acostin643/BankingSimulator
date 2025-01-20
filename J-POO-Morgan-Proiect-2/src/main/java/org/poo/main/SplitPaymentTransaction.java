package org.poo.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.CommandInput;

import java.util.List;

@Getter
@Setter
final class SplitPaymentTransaction extends Transaction {
    private double totalSum;
    private double amount;
    private String currency;
    private String error = null;
    private List<String> involvedAccounts;

    SplitPaymentTransaction(final CommandInput command) {
        super(command);
        this.involvedAccounts = command.getAccounts();
        this.currency = command.getCurrency();
        this.totalSum = command.getAmount();
        this.amount = totalSum / involvedAccounts.size();
    }

    @Override
    public void prepareTransactionNode(final ObjectNode transactionNode) {
        super.prepareTransactionNode(transactionNode);
        transactionNode.put("amount", this.amount);
        transactionNode.put("currency", this.currency);
        transactionNode.put("splitPaymentType", "equal");
        transactionNode.put("description",
                "Split payment of "
                        + String.format("%.2f", this.totalSum)
                        + " " + this.currency);
        ObjectMapper objectMapper = new ObjectMapper();
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

    public void prepareError(final String culprit) {
        this.setError("Account " + culprit
                + " has insufficient funds for a split payment.");
    }
}
