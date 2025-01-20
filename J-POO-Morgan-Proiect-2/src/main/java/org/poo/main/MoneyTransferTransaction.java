package org.poo.main;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.CommandInput;

@Getter
@Setter
final class MoneyTransferTransaction extends Transaction {
    private String senderIban;
    private String receiverIban;
    private String amount;
    private String transferType;

    MoneyTransferTransaction(final CommandInput command) {
        super(command);
        this.setDescription(command.getDescription());
        this.senderIban = command.getAccount();
        this.receiverIban = command.getReceiver();
        this.amount = command.getAmount() + " ";
        this.transferType = "none";
    }

    public void finalizeAmount(final String currency) {
        this.amount = this.amount + currency;
    }

    @Override
    public void prepareTransactionNode(final ObjectNode transactionNode) {
        transactionNode.put("timestamp", this.getTimestamp());
        transactionNode.put("description", this.getDescription());
        transactionNode.put("senderIBAN", this.senderIban);
        transactionNode.put("receiverIBAN", this.receiverIban);
        transactionNode.put("amount", this.amount);
        transactionNode.put("transferType", this.transferType);
    }
}
