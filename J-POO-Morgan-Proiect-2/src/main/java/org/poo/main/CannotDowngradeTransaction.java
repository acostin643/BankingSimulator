package org.poo.main;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;

public class CannotDowngradeTransaction extends Transaction {
    public CannotDowngradeTransaction(CommandInput command) {
        super(command);
        this.setDescription("You cannot downgrade your plan");
    }

    /**
     * @param transactionNode metoda pentru a crea un nod de tranzactie
     *                        pentru afisarea lui la comanda "PrintTransactions"
     */
    @Override
    public void prepareTransactionNode(final ObjectNode transactionNode) {
        transactionNode.put("timestamp", this.getTimestamp());
        transactionNode.put("description", this.getDescription());
    }
}
