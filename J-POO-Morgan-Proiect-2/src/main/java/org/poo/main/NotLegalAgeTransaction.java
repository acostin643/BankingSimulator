package org.poo.main;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;

public class NotLegalAgeTransaction extends Transaction {
    public NotLegalAgeTransaction(final CommandInput command) {
        super(command);
        this.setDescription("You don't have the minimum age required.");
    }

    /**
     * @param transactionNode
     */
    @Override
    public void prepareTransactionNode(final ObjectNode transactionNode) {
        transactionNode.put("timestamp", this.getTimestamp());
        transactionNode.put("description", this.getDescription());
    }
}
