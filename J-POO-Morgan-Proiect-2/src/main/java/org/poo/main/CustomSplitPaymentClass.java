package org.poo.main;

import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.CommandInput;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

import static org.poo.main.Helpers.convertCurrency;

@Getter
@Setter
public class CustomSplitPaymentClass {
    private ArrayList<Account> involvedAccounts = new ArrayList<>();
    private ArrayList<Double> amounts = new ArrayList<>();
    private int agreements;
    private CommandInput associatedCommand;

    /**
     *
     * @param account
     */
    public void addAccount(final Account account) {
        involvedAccounts.add(account);
    }

    /**
     *
     * @param amount
     */
    public void addAmount(final double amount) {
        amounts.add(amount);
    }

    /**
     * sorteaza conturile si sumele asociate fiecaruia
     * in ordine alfabetica
     */
    public void sortAccountsAndAmounts() {
        // Create a list of indices to track original positions
        ArrayList<Integer> indices = new ArrayList<>();
        for (int i = 0; i < involvedAccounts.size(); i++) {
            indices.add(i);
        }

        // Sort indices based on the account IBANs
        indices.sort(Comparator.comparing(i -> involvedAccounts.get(i).getIban()));

        // Reorder the accounts and amounts lists based on sorted indices
        for (int i = 0; i < indices.size(); i++) {
            while (indices.get(i) != i) { // Swap until sorted correctly
                int targetIndex = indices.get(i);

                // Swap accounts
                Account tempAccount = involvedAccounts.get(i);
                involvedAccounts.set(i, involvedAccounts.get(targetIndex));
                involvedAccounts.set(targetIndex, tempAccount);

                // Swap amounts
                double tempAmount = amounts.get(i);
                amounts.set(i, amounts.get(targetIndex));
                amounts.set(targetIndex, tempAmount);

                // Swap indices to reflect new positions
                indices.set(i, indices.get(targetIndex));
                indices.set(targetIndex, targetIndex);
            }
        }
    }

    /**
     * refuz pentru split payment
     * @param commandInput
     */
    public void denyVote(CommandInput commandInput) {
        for (Account involvedAccount : involvedAccounts) {
            if (commandInput.getSplitPaymentType().equals("custom")) {
                involvedAccount.getOwnerUser().getPendingCustomPayments().remove(this);
            } else {
                involvedAccount.getOwnerUser().getPendingEqualPayments().remove(this);
            }
        }
    }

    /**
     * aprobare pentru split payment
     * @param commandInput
     */
    public void acceptVote(final CommandInput commandInput) {
        this.setAgreements(this.getAgreements() + 1);
        if (this.getAgreements() == involvedAccounts.size()) {
            sortAccountsAndAmounts();

            Iterator<Account> accountIterator = involvedAccounts.iterator();
            Iterator<Double> amountIterator = amounts.iterator();

            while (accountIterator.hasNext() && amountIterator.hasNext()) {
                Account account = accountIterator.next();
                Double amount = amountIterator.next();

                if (account.getBalance() < convertCurrency(amount,
                        account.getCurrency(), associatedCommand.getCurrency())
                        && associatedCommand.getSplitPaymentType().equals("custom")) {
                    CustomSplitPaymentTransaction customSplitPaymentTransaction
                            = new CustomSplitPaymentTransaction(associatedCommand);
                    customSplitPaymentTransaction.prepareError(account.getIban());
                    // daca un cont nu are suficiente fonduri, fiecare din
                    // conturile implicate va primi o eroare
                    for (Account accountForAdding : involvedAccounts) {
                        accountForAdding.getOwnerUser()
                                .addTransaction(customSplitPaymentTransaction);
                        accountForAdding.addTransaction(customSplitPaymentTransaction);
                    }
                    return;
                } else if (account.getBalance() < convertCurrency(amount,
                        account.getCurrency(), associatedCommand.getCurrency())
                        && associatedCommand.getSplitPaymentType().equals("equal")) {
                    SplitPaymentTransaction splitPaymentTransaction
                            = new SplitPaymentTransaction(associatedCommand);
                    splitPaymentTransaction.prepareError(account.getIban());
                    // daca un cont nu are suficiente fonduri, fiecare din
                    // conturile implicate va primi o eroare
                    for (Account accountForAdding : involvedAccounts) {
                        accountForAdding.getOwnerUser()
                                .addTransaction(splitPaymentTransaction);
                        accountForAdding.addTransaction(splitPaymentTransaction);
                    }
                    return;
                }
            }

            accountIterator = involvedAccounts.iterator();
            amountIterator = amounts.iterator();

            while (accountIterator.hasNext() && amountIterator.hasNext()) {
                Account account = accountIterator.next();
                Double amount = amountIterator.next();

                account.setBalance(account.getBalance() - amount);
                CustomSplitPaymentTransaction customSplitPaymentTransaction
                        = new CustomSplitPaymentTransaction(associatedCommand);
                customSplitPaymentTransaction.setAssociatedAccount(account);
                customSplitPaymentTransaction.setAmountForUsers(
                        (ArrayList<Double>) associatedCommand.getAmountForUsers());
                account.getOwnerUser().addTransaction(customSplitPaymentTransaction);
                if (associatedCommand.getSplitPaymentType().equals("custom")) {
                    account.getOwnerUser().getPendingCustomPayments().remove(this);
                } else {
                    account.getOwnerUser().getPendingEqualPayments().remove(this);
                }
            }
        }
    }
}
