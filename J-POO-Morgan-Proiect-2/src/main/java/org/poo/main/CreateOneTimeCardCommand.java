package org.poo.main;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.fileio.CommandInput;

import static org.poo.main.Flow.allAccounts;

final class CreateOneTimeCardCommand implements Command {
    @Override
    public void execute(final ArrayNode output, final CommandInput commandInput) {
        Account chosenAccount = null;
        for (Account account : allAccounts) {
            if (account.getIban().equals(commandInput.getAccount())) {
                chosenAccount = account;
            }
        }

        if (chosenAccount == null) {
            return;
        }

        if (!chosenAccount.getOwnerUser().getEmail().equals(commandInput.getEmail())) {
            return;
        }

        //generarea cardului si adaugarea acestuia la contul respectiv
        OneTimeCard oneTimeCard = new OneTimeCard();
        oneTimeCard.setOwnerAccountIban(commandInput.getAccount());
        oneTimeCard.setOwnerAccount(chosenAccount);
        oneTimeCard.setActive(true);
        chosenAccount.addCard(oneTimeCard);

        //generarea de tranzactie
        CardCreationTransaction cardCreationTransaction
                = new CardCreationTransaction(commandInput);
        cardCreationTransaction.setCardNumber(oneTimeCard.getCardNumber());
        cardCreationTransaction.setAssociatedAccount(chosenAccount);
        chosenAccount.addTransaction(cardCreationTransaction);
        chosenAccount.getOwnerUser().addTransaction(cardCreationTransaction);
    }
}
