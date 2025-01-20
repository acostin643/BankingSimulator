package org.poo.main;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.CommandInput;

@Getter
@Setter
final class OnlinePaymentTransaction extends Transaction {
    private Double amount;
    private String commerciant;

    OnlinePaymentTransaction(final CommandInput command) {
        super(command);
        this.amount = command.getAmount();
        this.commerciant = command.getCommerciant();
        this.setDescription("Card payment");
    }

    public void prepareTransactionNode(final ObjectNode transactionNode) {
        transactionNode.put("timestamp", this.getTimestamp());
        transactionNode.put("description", this.getDescription());
        transactionNode.put("amount", this.amount);
        transactionNode.put("commerciant", this.commerciant);
    }
}
