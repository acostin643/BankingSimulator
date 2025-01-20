package org.poo.main;

import org.poo.fileio.CommandInput;

final class CardDestroyedTransaction extends CardCreationTransaction {
    CardDestroyedTransaction(final CommandInput command) {
        super(command);
        this.setDescription("The card has been destroyed");
    }
}
