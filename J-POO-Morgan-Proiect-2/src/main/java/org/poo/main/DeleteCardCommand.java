package org.poo.main;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.fileio.CommandInput;

import static org.poo.main.Helpers.cardWithCardNumber;

final class DeleteCardCommand implements Command {
    @Override
    public void execute(final ArrayNode output, final CommandInput commandInput) {
        Card card = cardWithCardNumber(commandInput.getCardNumber());
        if (card == null) {
            return;
        }
        Account account = card.getOwnerAccount();
        if (account == null) {
            return;
        }
        if (!account.getOwnerUser().getEmail().equals(commandInput.getEmail())
                && !account.getAccountType().equals("business")) {
            return;
        } else if (account.getAccountType().equals("business")) {
            int rank = ((BusinessAccount) account).roleCheck(commandInput.getEmail());
            if (rank == 0) {
                // daca nu este asociat al contului, nu poate sterge cardul
                System.out.println("x (deletecardcommand)");
                return;
            } else if (rank == 1 && !card.getCreatorEmail().equals(commandInput.getEmail())) {
                // daca este angajat, insa cardul nu este creat de el,
                // nu se poate efectua comanda
                System.out.println("y (deletecardcommand)");
                return;
            }
        }

        CardDeletionTransaction cardDeletionTransaction
                = new CardDeletionTransaction(commandInput);
        cardDeletionTransaction.setAccount(account.getIban());
        cardDeletionTransaction.setAssociatedAccount(account);
        account.getOwnerUser().addTransaction(cardDeletionTransaction);
        account.getCards().remove(card);
    }
}
