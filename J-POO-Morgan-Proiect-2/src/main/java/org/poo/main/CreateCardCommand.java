package org.poo.main;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.fileio.CommandInput;

import static org.poo.main.Flow.allAccounts;

final class CreateCardCommand implements Command {
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

        if (!chosenAccount.getOwnerUser()
                .getEmail().equals(commandInput.getEmail())
                && !chosenAccount.getAccountType().equals("business")) {
            // Daca contul nu este detinut de utilizatorul din email,
            // iar contul nu este de business, nu se poate
            // efectua comanda
            return;
        } else if (chosenAccount.getAccountType()
                .equals("business")
                && ((BusinessAccount) chosenAccount)
                .roleCheck(commandInput.getEmail()) == 0) {
            // Daca contul este unul de business, insa
            // cel care incearca sa adauge un card
            // nu este asociat, nu se poate efectua
            // comanda
            System.out.println("haules (createcardcommand)");
            return;
        }

        Card card = new Card();
        card.setOwnerAccountIban(commandInput.getAccount());
        card.setOwnerAccount(chosenAccount);
        card.setActive(true);
        card.setCreatorEmail(commandInput.getEmail());
        chosenAccount.addCard(card);

        CardCreationTransaction cardCreationTransaction
                = new CardCreationTransaction(commandInput);
        cardCreationTransaction.setCardNumber(card.getCardNumber());
        cardCreationTransaction.setAssociatedAccount(chosenAccount);
        chosenAccount.getOwnerUser().addTransaction(cardCreationTransaction);
        chosenAccount.addTransaction(cardCreationTransaction);
    }
}
