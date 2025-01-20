package org.poo.main;

import org.poo.fileio.CommandInput;

final class CardDeletionTransaction extends CardCreationTransaction {
    CardDeletionTransaction(final CommandInput command) {
        super(command);
        this.setDescription("The card has been destroyed");
    }
}
