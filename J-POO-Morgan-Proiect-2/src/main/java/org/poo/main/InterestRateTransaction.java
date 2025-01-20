package org.poo.main;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.CommandInput;

@Getter
@Setter
public class InterestRateTransaction extends Transaction {
    private double amount;
    private String currency;

    public InterestRateTransaction(final CommandInput command) {
        super(command);
        this.setDescription("Interest rate income");
    }

    /**
     *
     * @param transactionNode este nodul de tranzactie care va fi
     *                        adaugat la printTransactions, modificat in mod unic de
     *                        fiecare tip de tranzactie
     */
    @Override
    public void prepareTransactionNode(final ObjectNode transactionNode) {
        transactionNode.put("timestamp", this.getTimestamp());
        transactionNode.put("description", this.getDescription());
        transactionNode.put("amount", this.getAmount());
        transactionNode.put("currency", this.getCurrency());
    }
}
