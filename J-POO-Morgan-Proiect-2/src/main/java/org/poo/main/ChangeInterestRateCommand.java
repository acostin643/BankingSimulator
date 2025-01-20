package org.poo.main;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.fileio.CommandInput;

import static org.poo.main.Helpers.accountWithIban;
import static org.poo.main.Helpers.generateNotSavingsAccountError;


final class ChangeInterestRateCommand implements Command {
    @Override
    public void execute(final ArrayNode output, final CommandInput commandInput) {
        Account account = accountWithIban(commandInput.getAccount());
        if (account == null) {
            return;
        }
        if (account.getAccountType().equals("classic")) {
            // Daca nu este cont de economii, se genereaza o eroare. Altfel,
            // se schimba dobanda
            generateNotSavingsAccountError(output, commandInput, 0);
        } else {
            account.setInterestRate(commandInput.getInterestRate());
            InterestChangeTransaction interestChangeTransaction
                    = new InterestChangeTransaction(commandInput);
            account.getOwnerUser().addTransaction(interestChangeTransaction);
        }
    }
}
