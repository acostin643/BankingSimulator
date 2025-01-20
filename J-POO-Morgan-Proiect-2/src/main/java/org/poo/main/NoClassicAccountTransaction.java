package org.poo.main;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;

public class NoClassicAccountTransaction extends Transaction {
    public NoClassicAccountTransaction(final CommandInput command) {
        super(command);
        this.setDescription("You do not have a classic account.");
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
