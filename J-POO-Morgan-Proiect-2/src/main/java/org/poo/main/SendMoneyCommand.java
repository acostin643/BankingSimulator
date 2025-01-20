package org.poo.main;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.fileio.CommandInput;

import static org.poo.main.Helpers.accountWithIban;
import static org.poo.main.Helpers.generateUserNotFoundError;
import static org.poo.main.Helpers.accountWithAlias;
import static org.poo.main.Helpers.convertCurrency;
import static org.poo.main.Helpers.POINT_ONE_PERCENT;
import static org.poo.main.Helpers.POINT_TWO_PERCENT;
import static org.poo.main.Helpers.FIVE;
import static org.poo.main.Helpers.THREE_HUNDRED;
import static org.poo.main.Helpers.FIVE_HUNDRED;

final class SendMoneyCommand implements Command {

    @Override
    public void execute(final ArrayNode output, final CommandInput commandInput) {
        Account senderAccount = accountWithIban(commandInput.getAccount());
        if (senderAccount == null) {
            generateUserNotFoundError(output, commandInput);
            return;
        }
        Account receiverAccount = accountWithIban(commandInput.getReceiver());
        if (receiverAccount == null) {
            // daca nu se gaseste contul dupa IBAN, se cauta dupa aceea dupa alias
            receiverAccount = accountWithAlias(commandInput.getAlias());
            if (receiverAccount == null) {
                generateUserNotFoundError(output, commandInput);
                return;
            }
        }

        //se converteste intai in valuta contului care primeste
        double amount = convertCurrency(commandInput.getAmount(),
                receiverAccount.getCurrency(), senderAccount.getCurrency());

        if (senderAccount.getPlanType().equals("silver")
                && convertCurrency(amount, "RON",
                receiverAccount.getCurrency()) >= THREE_HUNDRED) {
            senderAccount.getOwnerUser().increaseNrOfTransactionsOver300();
            if (senderAccount.getOwnerUser().getNrOfTransactionsOver300() >= FIVE) {
                for (Account account : senderAccount.getOwnerUser().getAccounts()) {
                    account.setPlanType("gold");
                    account.getOwnerUser().setPlanType("gold");
                }
            }
        }

        double comision = 0;
        if (senderAccount.getPlanType().equals("standard")) {
            comision = POINT_TWO_PERCENT;
        }
        if (senderAccount.getPlanType().equals("silver")
                && convertCurrency(amount,
                "RON", receiverAccount.getCurrency()) >= FIVE_HUNDRED) {
            comision = POINT_ONE_PERCENT;
        }


        if (senderAccount.getBalance() < commandInput.getAmount() * (1 + comision)) {
            // se verifica daca contul care trimite are fonduri suficiente
            InsufficientFundsTransaction insufficientFundsTransaction
                    = new InsufficientFundsTransaction(commandInput);
            insufficientFundsTransaction.setAssociatedAccount(senderAccount);
            senderAccount.addTransaction(insufficientFundsTransaction);
            senderAccount.getOwnerUser().addTransaction(insufficientFundsTransaction);
            return;
        }

        // se genereaza o tranzactie de tip "trimis" pentru contul care trimite
        double deduction = commandInput.getAmount() * (1 + comision);
        senderAccount.setBalance(senderAccount.getBalance() - deduction);
        receiverAccount.setBalance(receiverAccount.getBalance() + amount);
        MoneyTransferTransaction transactionOne
                = new MoneyTransferTransaction(commandInput);
        transactionOne.finalizeAmount(senderAccount.getCurrency());
        transactionOne.setTransferType("sent");
        transactionOne.setAssociatedAccount(senderAccount);
        senderAccount.addTransaction(transactionOne);
        senderAccount.getOwnerUser().addTransaction(transactionOne);

        // se genereaza o tranzactie de tip "primit" pentru contul care primeste
        MoneyTransferTransaction transactionTwo
                = new MoneyTransferTransaction(commandInput);
        transactionTwo.setAmount(String.valueOf(amount + " "));
        transactionTwo.finalizeAmount(receiverAccount.getCurrency());
        transactionTwo.setTransferType("received");
        transactionTwo.setAssociatedAccount(receiverAccount);
        receiverAccount.addTransaction(transactionTwo);
        receiverAccount.getOwnerUser().addTransaction(transactionTwo);
    }
}
