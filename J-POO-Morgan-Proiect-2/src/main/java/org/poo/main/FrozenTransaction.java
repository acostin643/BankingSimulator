package org.poo.main;

import org.poo.fileio.CommandInput;

final class FrozenTransaction extends Transaction {
    FrozenTransaction(final CommandInput command) {
        super(command);
        this.setDescription("The card is frozen");
    }
}
