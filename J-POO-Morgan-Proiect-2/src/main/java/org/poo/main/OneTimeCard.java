package org.poo.main;

import org.poo.fileio.CommandInput;
import org.poo.fileio.CommerciantInput;

import static org.poo.main.Helpers.commerciantWithName;
import static org.poo.main.Helpers.convertCurrency;
import static org.poo.main.Helpers.POINT_TWO_PERCENT;
import static org.poo.main.Helpers.POINT_FOUR_PERCENT;
import static org.poo.main.Helpers.POINT_FIVE_PERCENT;
import static org.poo.main.Helpers.THREE;
import static org.poo.main.Helpers.FIVE;
import static org.poo.main.Helpers.TEN;
import static org.poo.main.Helpers.ONE_HUNDRED;
import static org.poo.main.Helpers.THREE_HUNDRED;
import static org.poo.main.Helpers.FIVE_HUNDRED;


final class OneTimeCard extends Card {
    OneTimeCard() {
        super();
    }

    @Override
    public void effectuatePayment(final double amount, final CommandInput command) {
        if (ownerAccount.getBalance() < amount) {
            InsufficientFundsTransaction insufficientFundsTransaction
                    = new InsufficientFundsTransaction(command);
            insufficientFundsTransaction
                    .setAssociatedAccount(this.getOwnerAccount());
            this.getOwnerAccount()
                    .addTransaction(insufficientFundsTransaction);
            this.getOwnerAccount().getOwnerUser()
                    .addTransaction(insufficientFundsTransaction);
            return;
        }


        ownerAccount.setBalance(ownerAccount.getBalance() - amount);
        // intai se retrage suma

        // se genereaza tranzactia aferenta
        OnlinePaymentTransaction onlinePaymentTransaction
                = new OnlinePaymentTransaction(command);
        onlinePaymentTransaction.setAmount(amount);
        onlinePaymentTransaction.setAssociatedAccount(this.getOwnerAccount());
        this.getOwnerAccount().addTransaction(onlinePaymentTransaction);
        this.getOwnerAccount().addSpendingTransaction(onlinePaymentTransaction);
        this.getOwnerAccount().getOwnerUser()
                .addTransaction(onlinePaymentTransaction);


        //se genereaza tranzactia de distrugere de card
        CardDestroyedTransaction cardDestroyedTransaction
                = new CardDestroyedTransaction(command);
        cardDestroyedTransaction.setAssociatedAccount(this.getOwnerAccount());
        cardDestroyedTransaction.setAccount(this.getOwnerAccount().getIban());
        ownerAccount.getOwnerUser().addTransaction(cardDestroyedTransaction);


        // se creaza noul card care il va inlocui pe cel vechi
        OneTimeCard oneTimeCard = new OneTimeCard();
        oneTimeCard.setOwnerAccount(this.getOwnerAccount());
        oneTimeCard.setOwnerAccountIban(this.getOwnerAccountIban());
        ownerAccount.addCard(oneTimeCard);

        // se genereaza tranzactia de creare a noului card
        CardCreationTransaction cardCreationTransaction
                = new CardCreationTransaction(command);
        cardCreationTransaction
                .setAssociatedAccount(oneTimeCard.getOwnerAccount());
        cardCreationTransaction.setCardNumber(oneTimeCard.getCardNumber());
        cardCreationTransaction
                .setAccount(oneTimeCard.getOwnerAccount().getIban());
        ownerAccount.addTransaction(cardCreationTransaction);
        ownerAccount.getOwnerUser().addTransaction(cardCreationTransaction);

        //aici vine logica pentru adaugarea de tranzactii
        //pentru comerciant in vederea logicii de cashback

        CommerciantInput commerciantInput = commerciantWithName(command.getCommerciant());
        if (commerciantInput == null) {
            ownerAccount.getCards().remove(this);
            return;
        }

        this.getOwnerAccount().addCommerciant(commerciantInput);
        Commerciant chosenCommerciant = null;
        for (Commerciant commerciant : this.getOwnerAccount().getCommerciants()) {
            if (commerciant.getName().equals(command.getCommerciant())) {
                chosenCommerciant = commerciant;
            }
        }
        ownerAccount.getCards().remove(this);

        chosenCommerciant.addAmountSpent(amount);

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

        if (commerciantInput.getCashbackStrategy().equals("spendingThreshold")) {
            if (convertCurrency(chosenCommerciant.getAmountSpent(),
                    "RON", ownerAccount.getCurrency()) >= FIVE_HUNDRED
                    && ownerAccount.getSpendingStatus() <= 2) {
                ownerAccount.setSpendingStatus(THREE);
                double multiplier = POINT_FIVE_PERCENT;
                ownerAccount.setBalance(ownerAccount.getBalance()
                        + amount * (multiplier));
            }
            if (convertCurrency(chosenCommerciant.getAmountSpent(),
                    "RON", ownerAccount.getCurrency()) >= THREE_HUNDRED
                    && ownerAccount.getSpendingStatus() <= 1) {
                ownerAccount.setSpendingStatus(2);
                double multiplier = POINT_FOUR_PERCENT;
                ownerAccount.setBalance(ownerAccount.getBalance()
                        + amount * (multiplier));
            }
            if (convertCurrency(chosenCommerciant.getAmountSpent(),
                    "RON", ownerAccount.getCurrency()) >= ONE_HUNDRED
                    && ownerAccount.getSpendingStatus() == 0) {
                ownerAccount.setSpendingStatus(1);
                double multiplier = POINT_TWO_PERCENT;
                ownerAccount.setBalance(ownerAccount.getBalance()
                        + amount * (multiplier));
            }
        }

        // se scoate vechiul card
        ownerAccount.getCards().remove(this);
        this.setOwnerAccountIban(null);
    }
}
