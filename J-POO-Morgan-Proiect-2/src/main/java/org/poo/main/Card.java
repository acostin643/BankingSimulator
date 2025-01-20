package org.poo.main;

import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.CommandInput;
import org.poo.fileio.CommerciantInput;

import static org.poo.main.Helpers.commerciantWithName;
import static org.poo.main.Helpers.POINT_ONE_PERCENT;
import static org.poo.main.Helpers.POINT_TWO_PERCENT;
import static org.poo.main.Helpers.THREE;
import static org.poo.main.Helpers.FIVE;
import static org.poo.main.Helpers.TEN;
import static org.poo.main.Helpers.ONE_HUNDRED;
import static org.poo.main.Helpers.THREE_HUNDRED;
import static org.poo.main.Helpers.FIVE_HUNDRED;
import static org.poo.main.Helpers.convertCurrency;
import static org.poo.utils.Utils.generateCardNumber;

@Getter
@Setter
public class Card {
    private String cardNumber;
    private String ownerAccountIban;
    protected Account ownerAccount;
    private boolean isActive;
    private String creatorEmail;

    public Card() {
        this.isActive = true;
        this.cardNumber = generateCardNumber();
    }

    /**
     * @param amount  reprezinta suma care trebuie retrasa din cont
     * @param command contine diversii parametri necesari generarii de tranzactii
     */
    public void effectuatePayment(final double amount, final CommandInput command) {
        if (!this.isActive) {
            FrozenTransaction frozenTransaction = new FrozenTransaction(command);
            frozenTransaction.setAssociatedAccount(this.getOwnerAccount());
            this.getOwnerAccount().getOwnerUser().addTransaction(frozenTransaction);
            return;
        }

        if (ownerAccount.getBalance() < amount) {
            InsufficientFundsTransaction insufficientFundsTransaction
                    = new InsufficientFundsTransaction(command);
            insufficientFundsTransaction.setAssociatedAccount(this.getOwnerAccount());
            this.getOwnerAccount().addTransaction(insufficientFundsTransaction);
            this.getOwnerAccount().getOwnerUser().
                    addTransaction(insufficientFundsTransaction);
            return;
        }
        ownerAccount.setBalance(ownerAccount.getBalance() - amount);
        if (ownerAccount.getAccountType().equals("business")) {
            ((BusinessAccount) ownerAccount)
                    .setTotalSpent(((BusinessAccount) ownerAccount)
                            .getTotalSpent() + amount);
        }
        OnlinePaymentTransaction onlinePaymentTransaction
                = new OnlinePaymentTransaction(command);
        onlinePaymentTransaction.setAmount(amount);
        onlinePaymentTransaction.setAssociatedAccount(this.getOwnerAccount());
        if (amount != 0) {
            ownerAccount.addTransaction(onlinePaymentTransaction);
            ownerAccount.addSpendingTransaction(onlinePaymentTransaction);
            ownerAccount.getOwnerUser().addTransaction(onlinePaymentTransaction);
        }

        //aici vine logica pentru adaugarea de tranzactii
        //pentru comerciant in vederea logicii de cashback

        CommerciantInput commerciantInput = commerciantWithName(command.getCommerciant());
        if (commerciantInput == null) {
            return;
        }

        this.getOwnerAccount().addCommerciant(commerciantInput);
        Commerciant chosenCommerciant = null;
        for (Commerciant commerciant : this.getOwnerAccount().getCommerciants()) {
            if (commerciant.getName().equals(command.getCommerciant())) {
                chosenCommerciant = commerciant;
            }
        }
        if (chosenCommerciant == null) {
            return;
        }

        //aici vine logica de discount nrOfTransactions dupa efectuarea unei plati
        //la un comerciant care foloseste aceasta strategie
        chosenCommerciant.setTimesSpent(chosenCommerciant.getTimesSpent() + 1);
        if (commerciantInput.getCashbackStrategy().equals("nrOfTransactions")) {
            if (chosenCommerciant.getTimesSpent() == 2
                    && ownerAccount.getFoodCashBack() == 0) {
                ownerAccount.setFoodCashBack(1);
            }
            if (chosenCommerciant.getTimesSpent() == FIVE
                    && ownerAccount.getClothesCashBack() == 0) {
                ownerAccount.setClothesCashBack(1);
            }
            if (chosenCommerciant.getTimesSpent() == TEN
                    && ownerAccount.getTechCashBack() == 0) {
                ownerAccount.setTechCashBack(1);
            }
        }

        //aici vine logica de discount spendingThreshold dupa efectuarea unei plati
        //la un comerciant care foloseste aceasta strategie
        double og = chosenCommerciant.getAmountSpent() + amount;
        chosenCommerciant.setAmountSpent(og);

        if (commerciantInput.getCashbackStrategy().equals("spendingThreshold")) {
            if (convertCurrency(chosenCommerciant.getAmountSpent(),
                    "RON", ownerAccount.getCurrency()) >= FIVE_HUNDRED
                    && ownerAccount.getSpendingStatus() <= 2) {
                ownerAccount.setSpendingStatus(THREE);
                double multiplier = 0.0025;
                ownerAccount.setBalance(ownerAccount.getBalance()
                        + amount * (multiplier));
            }
            if (convertCurrency(chosenCommerciant.getAmountSpent(),
                    "RON", ownerAccount.getCurrency()) >= THREE_HUNDRED
                    && ownerAccount.getSpendingStatus() <= 1) {
                ownerAccount.setSpendingStatus(2);
                double multiplier = POINT_TWO_PERCENT;
                ownerAccount.setBalance(ownerAccount.getBalance()
                        + amount * (multiplier));
            }
            if (convertCurrency(chosenCommerciant.getAmountSpent(),
                    "RON", ownerAccount.getCurrency())
                    >= ONE_HUNDRED && ownerAccount.getSpendingStatus() == 0) {
                ownerAccount.setSpendingStatus(1);
                double multiplier = POINT_ONE_PERCENT;
                ownerAccount.setBalance(ownerAccount.getBalance()
                        + amount * (multiplier));
            }
        }
        chosenCommerciant.setAmountSpent(og);
    }

    /**
     * @return daca este contul sau nu activ
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * @param active reprezinta starea pe care vrem sa o asociem cardului (inghetat/activ)
     */
    public void setActive(final boolean active) {
        isActive = active;
    }
}
