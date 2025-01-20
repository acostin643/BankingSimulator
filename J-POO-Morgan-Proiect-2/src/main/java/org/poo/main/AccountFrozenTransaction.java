package org.poo.main;

import org.poo.fileio.CommandInput;

final class AccountFrozenTransaction extends Transaction {

    AccountFrozenTransaction(final CommandInput command) {
        super(command);
        this.
                setDescription("You have reached the minimum "
                        + "amount of funds, the card will be frozen");
    }
}
