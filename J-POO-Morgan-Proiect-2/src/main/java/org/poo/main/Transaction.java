package org.poo.main;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.CommandInput;

@Getter
@Setter
public abstract class Transaction {
    // Getters and Setters
    private int timestamp;
    private String description;
    private Account associatedAccount;

    public Transaction(final CommandInput command) {
        this.timestamp = command.getTimestamp();
    }

    /**
     * @param transactionNode este nodul de tranzactie care va fi
     *                        adaugat la printTransactions, modificat in mod unic de
     *                        fiecare tip de tranzactie
     */
    public void prepareTransactionNode(final ObjectNode transactionNode) {
        transactionNode.put("timestamp", this.timestamp);
        transactionNode.put("description", this.description);
    }
}
