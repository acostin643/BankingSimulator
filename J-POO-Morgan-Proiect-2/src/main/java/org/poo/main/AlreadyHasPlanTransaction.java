package org.poo.main;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;

public class AlreadyHasPlanTransaction extends Transaction {
    public AlreadyHasPlanTransaction(final CommandInput command) {
        super(command);
        this.setDescription("The user already has the " + command.getNewPlanType() + " plan");
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
