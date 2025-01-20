package org.poo.main;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.fileio.CommandInput;
import org.poo.fileio.UserInput;

import static org.poo.main.Helpers.*;

public class WithdrawSavingsCommand implements Command {
    /**
     * @param output       este output-ul oferit comenzii
     * @param commandInput este input-ul oferit comenzii
     */
    @Override
    public void execute(final ArrayNode output, final CommandInput commandInput) {
        Account account = accountWithIban(commandInput.getAccount());
        if (account == null) {
            return;
        }

        if (!account.getAccountType().equals("savings")) {
            return;
        }

        UserInput user = account.getOwnerUser();
        if (!isLegalAge(user.getBirthDate())) {
            NotLegalAgeTransaction notLegalAgeTransaction
                    = new NotLegalAgeTransaction(commandInput);
            user.addTransaction(notLegalAgeTransaction);
            return;
        }

        Account destinationAccount = null;
        for (Account nextAccount : user.getAccounts()) {
            if (nextAccount.getCurrency().equals(commandInput.getCurrency())
                    && nextAccount.getAccountType().equals("classic")) {
                destinationAccount = nextAccount;
                break;
            }
        }

        if (destinationAccount == null) {
            NoClassicAccountTransaction noClassicAccountTransaction
                    = new NoClassicAccountTransaction(commandInput);
            user.addTransaction(noClassicAccountTransaction);
            return;
        }

        double amountToWithDraw
                = convertCurrency(commandInput.getAmount(),
                account.getCurrency(), commandInput.getCurrency());
        double comision = 0;
        if (account.getPlanType().equals("standard")) {
            comision = POINT_TWO_PERCENT;
        } else if (account.getPlanType().equals("silver")
                && amountToWithDraw
                >= convertCurrency(FIVE_HUNDRED,
                "RON", account.getCurrency())) {
            comision = POINT_ONE_PERCENT;
        }
        if (account.getBalance() < amountToWithDraw * (1 + comision)) {
            InsufficientFundsTransaction insufficientFundsTransaction
                    = new InsufficientFundsTransaction(commandInput);
            user.addTransaction(insufficientFundsTransaction);
            return;
        }

        account.setBalance(account.getBalance()
                - amountToWithDraw * (1 + comision));

        destinationAccount
                .setBalance(destinationAccount.getBalance()
                        + amountToWithDraw);
    }
}
