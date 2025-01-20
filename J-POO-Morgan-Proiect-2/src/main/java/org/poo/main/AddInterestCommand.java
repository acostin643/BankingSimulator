package org.poo.main;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.fileio.CommandInput;

import static org.poo.main.Helpers.accountWithIban;
import static org.poo.main.Helpers.generateNotSavingsAccountError;

final class AddInterestCommand implements Command {
    @Override
    public void execute(final ArrayNode output, final CommandInput commandInput) {
        Account account = accountWithIban(commandInput.getAccount());
        if (account == null) {
            return;
        }
        if (account.getAccountType().equals("classic")) {
            // Daca nu este cont de economii, se genereaza o eroare
            generateNotSavingsAccountError(output, commandInput, 1);
        } else {
            // Altfel, se adauga suma necesara
            SavingsAccount savingsAccount = (SavingsAccount) account;
            savingsAccount.addInterestWithTransaction(commandInput);
        }
    }
}
