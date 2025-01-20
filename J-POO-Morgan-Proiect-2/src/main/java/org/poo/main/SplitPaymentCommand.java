package org.poo.main;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.fileio.CommandInput;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.poo.main.Helpers.accountWithIban;
import static org.poo.main.Helpers.convertCurrency;

final class SplitPaymentCommand implements Command {
    @Override
    public void execute(final ArrayNode output, final CommandInput commandInput) {
        List<String> ibans = commandInput.getAccounts();
        ArrayList<Account> accounts = new ArrayList<>();
        for (String iban : ibans) {
            Account account = accountWithIban(iban);
            if (account == null) {
                return;
            }
            accounts.add(account);
        }

        Collections.reverse(accounts);

        double individualSum = commandInput.getAmount() / accounts.size();
        for (Account account : accounts) {
            if (account.getBalance() < convertCurrency(individualSum,
                    account.getCurrency(), commandInput.getCurrency())) {
                SplitPaymentTransaction splitPaymentTransaction
                        = new SplitPaymentTransaction(commandInput);
                splitPaymentTransaction.prepareError(account.getIban());
                // daca un cont nu are suficiente fonduri, fiecare din
                // conturile implicate va primi o eroare
                for (Account accountForAdding : accounts) {
                    accountForAdding.getOwnerUser()
                            .addTransaction(splitPaymentTransaction);
                    accountForAdding.addTransaction(splitPaymentTransaction);
                }
                return;
            }
        }

        for (Account account : accounts) {
            double amount = convertCurrency(individualSum,
                    account.getCurrency(), commandInput.getCurrency());
            account.setBalance(account.getBalance() - amount);
            SplitPaymentTransaction splitPaymentTransaction
                    = new SplitPaymentTransaction(commandInput);
            splitPaymentTransaction.setAssociatedAccount(account);
            account.getOwnerUser().addTransaction(splitPaymentTransaction);
        }
    }
}
