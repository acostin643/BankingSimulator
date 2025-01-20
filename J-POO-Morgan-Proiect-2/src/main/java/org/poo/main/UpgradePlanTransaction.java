package org.poo.main;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.CommandInput;

@Getter
@Setter
public class UpgradePlanTransaction extends Transaction {
    private String accountIban;
    private String newPlanType;

    UpgradePlanTransaction(final CommandInput command) {
        super(command);
        this.setDescription("Upgrade plan");
        this.setNewPlanType(command.getNewPlanType());
        this.setAccountIban(command.getAccount());
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
        transactionNode.put("accountIBAN", this.accountIban);
        transactionNode.put("newPlanType", this.newPlanType);
    }
}
