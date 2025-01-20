package org.poo.main;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.fileio.CommandInput;
import org.poo.fileio.CommerciantInput;

import static org.poo.main.Helpers.cardWithCardNumber;
import static org.poo.main.Helpers.generatePayOnlineError;
import static org.poo.main.Helpers.convertCurrency;
import static org.poo.main.Helpers.commerciantWithName;

final class PayOnlineCommand implements Command {
    @Override
    public void execute(final ArrayNode output, final CommandInput commandInput) {
        Card card = cardWithCardNumber(commandInput.getCardNumber());
        if (card == null) {
            generatePayOnlineError(output, commandInput);
            return;
        }

        double amount = commandInput.getAmount();
        amount = convertCurrency(amount,
                card.getOwnerAccount().getCurrency(),
                commandInput.getCurrency());

        CommerciantInput commerciantInput = commerciantWithName(commandInput.getCommerciant());
        if (commerciantInput == null) {
            return;
        }

        card.getOwnerAccount().addCommerciant(commerciantInput);

        //aici vine logica de discount pentru strategiile de tip nrOfTransactions
        if (commerciantInput.getCashbackStrategy().equals("nrOfTransactions")) {
            if (commerciantInput.getType().equals("Food")
                    && card.getOwnerAccount().getFoodCashBack() == 1) {
                amount = amount * 0.98;
                card.getOwnerAccount().setFoodCashBack(2);
            }
            if (commerciantInput.getType().equals("Clothes")
                    && card.getOwnerAccount().getClothesCashBack() == 1) {
                amount = amount * 0.95;
                card.getOwnerAccount().setClothesCashBack(2);
            }
            if (commerciantInput.getType().equals("Tech")
                    && card.getOwnerAccount().getTechCashBack() == 1) {
                amount = amount * 0.90;
                card.getOwnerAccount().setTechCashBack(2);
            }
        }

        double comision = 0;

        if (card.getOwnerAccount().getPlanType().equals("standard")) {
            comision = 0.002;
        } else if (card.getOwnerAccount().getPlanType().equals("silver")
                && amount >= convertCurrency(500, card.getOwnerAccount().getCurrency(), "RON")) {
            comision = 0.001;
        }

        if (card.getOwnerAccount().getBalance() < amount * (1 + comision)) {
            InsufficientFundsTransaction insufficientFundsTransaction
                    = new InsufficientFundsTransaction(commandInput);
            card.getOwnerAccount()
                    .addTransaction(insufficientFundsTransaction);
            card.getOwnerAccount()
                    .getOwnerUser()
                    .addTransaction(insufficientFundsTransaction);
            return;
        }

        if (card.getOwnerAccount().getAccountType().equals("business")) {
            int rank = ((BusinessAccount) card.getOwnerAccount())
                    .roleCheck(commandInput.getEmail());
            if (rank == 0) {
                return;
            } else if (rank <= 2
                    && amount > ((BusinessAccount) card.getOwnerAccount())
                    .getSpendingLimit()) {
                return;
            }
            ((BusinessAccount) card.getOwnerAccount())
                    .setTotalSpent(((BusinessAccount) card.getOwnerAccount())
                            .getTotalSpent() + amount);
        }


        card.effectuatePayment(amount, commandInput);
        card.getOwnerAccount().setBalance(card.getOwnerAccount().getBalance()
                - comision * amount);
    }
}
