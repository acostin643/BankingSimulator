package org.poo.main;

import org.poo.fileio.CommandInput;

final class CardNotFoundTransaction extends Transaction {
    CardNotFoundTransaction(final CommandInput command) {
        super(command);
        this.setDescription("Card not found");
    }
}
