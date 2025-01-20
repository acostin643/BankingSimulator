package org.poo.main;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.CommandInput;

@Getter
@Setter
final class InsufficientFundsTransaction extends Transaction {
    InsufficientFundsTransaction(final CommandInput command) {
        super(command);
        this.setDescription("Insufficient funds");
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
