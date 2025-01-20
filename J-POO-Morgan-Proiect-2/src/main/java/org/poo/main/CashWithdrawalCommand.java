package org.poo.main;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.fileio.CommandInput;

import static org.poo.main.Helpers.cardWithCardNumber;
import static org.poo.main.Helpers.generateCardNotFoundError;
import static org.poo.main.Helpers.convertCurrency;
import static org.poo.main.Helpers.POINT_ONE_PERCENT;
import static org.poo.main.Helpers.POINT_TWO_PERCENT;
import static org.poo.main.Helpers.FIVE_HUNDRED;

public class CashWithdrawalCommand implements Command {
    /**
     *
     * @param output       este output-ul oferit comenzii
     * @param commandInput este input-ul oferit comenzii
     */
    @Override
    public void execute(final ArrayNode output, final CommandInput commandInput) {
        Card card = cardWithCardNumber(commandInput.getCardNumber());
        if (card == null) {
            generateCardNotFoundError(output, commandInput);
            return;
        }

        //se converteste suma din input in valuta contului caruia
        //ii apartine cardul
        double amountToWithDraw
                = convertCurrency(commandInput.getAmount(),
                card.getOwnerAccount().getCurrency(), "RON");
        double comision = 0;

        //in functie de planul utilizatorului, se stabileste comisionul
        if (card.getOwnerAccount().getPlanType().equals("standard")) {
            comision = POINT_TWO_PERCENT;
        } else if (card.getOwnerAccount().getPlanType().equals("silver")
                && amountToWithDraw >= convertCurrency(FIVE_HUNDRED,
                card.getOwnerAccount().getCurrency(), "RON")) {
            comision = POINT_ONE_PERCENT;
        }

        if (card.getOwnerAccount().getBalance()
                < amountToWithDraw * (1 + comision)) {
            InsufficientFundsTransaction insufficientFundsTransaction
                    = new InsufficientFundsTransaction(commandInput);
            card.getOwnerAccount()
                    .getOwnerUser().addTransaction(insufficientFundsTransaction);
            return;
        }

        card.getOwnerAccount().setBalance(card.getOwnerAccount().getBalance()
                - amountToWithDraw * (1 + comision));
        CashWithdrawalTransaction cashWithdrawalTransaction
                = new CashWithdrawalTransaction(commandInput);
        card.getOwnerAccount().getOwnerUser()
                .addTransaction(cashWithdrawalTransaction);
    }
}
