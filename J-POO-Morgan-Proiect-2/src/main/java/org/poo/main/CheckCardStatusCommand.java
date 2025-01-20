package org.poo.main;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.fileio.CommandInput;

import static org.poo.main.Helpers.cardWithCardNumber;
import static org.poo.main.Helpers.generateCardNotFoundError;

final class CheckCardStatusCommand implements Command {
    @Override
    public void execute(final ArrayNode output, final CommandInput commandInput) {
        Card card = cardWithCardNumber(commandInput.getCardNumber());
        if (card == null) {
            generateCardNotFoundError(output, commandInput);
            return;
        }
        if (card.getOwnerAccount().getBalance()
                <= card.getOwnerAccount().getMinimumBalance()) {
            // Daca cardul are fonduri sub suma minima setata,
            // se ingheata
            card.setActive(false);
            AccountFrozenTransaction accountFrozenTransaction
                    = new AccountFrozenTransaction(commandInput);
            accountFrozenTransaction.
                    setAssociatedAccount((card.getOwnerAccount()));
            card.getOwnerAccount().getOwnerUser()
                    .addTransaction(accountFrozenTransaction);
        }
    }
}
