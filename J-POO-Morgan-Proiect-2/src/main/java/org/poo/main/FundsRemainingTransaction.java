package org.poo.main;

import org.poo.fileio.CommandInput;

final class FundsRemainingTransaction extends Transaction {
    FundsRemainingTransaction(final CommandInput command) {
        super(command);
        this.
                setDescription("Account couldn't be deleted - "
                        + "there are funds remaining");
    }
}
