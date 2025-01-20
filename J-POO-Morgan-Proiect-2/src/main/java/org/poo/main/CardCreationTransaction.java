package org.poo.main;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.CommandInput;

@Getter
@Setter
public class CardCreationTransaction extends Transaction {
    private String cardNumber;
    private String cardHolder;
    private String account;

    public CardCreationTransaction(final CommandInput command) {
        super(command);
        this.setDescription("New card created");
        this.cardNumber = command.getCardNumber();
        this.cardHolder = command.getEmail();
        this.account = command.getAccount();
    }

    /**
     * @param transactionNode
     */
    @Override
    public void prepareTransactionNode(final ObjectNode transactionNode) {
        transactionNode.put("timestamp", this.getTimestamp());
        transactionNode.put("description", this.getDescription());
        transactionNode.put("card", this.cardNumber);
        transactionNode.put("cardHolder", this.cardHolder);
        transactionNode.put("account", this.account);
    }
}
