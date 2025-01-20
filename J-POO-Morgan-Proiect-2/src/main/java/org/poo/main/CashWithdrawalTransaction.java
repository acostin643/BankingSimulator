package org.poo.main;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;

public class CashWithdrawalTransaction extends Transaction {
    private double amount;

    public CashWithdrawalTransaction(final CommandInput command) {
        super(command);
        this.amount = command.getAmount();
        this.setDescription("Cash withdrawal of " + command.getAmount());
    }

    /**
     * @param transactionNode
     */
    @Override
    public void prepareTransactionNode(final ObjectNode transactionNode) {
        transactionNode.put("timestamp", this.getTimestamp());
        transactionNode.put("description", this.getDescription());
        transactionNode.put("amount", this.amount);
    }
}
