package org.poo.main;

import org.poo.fileio.CommandInput;

final class InterestChangeTransaction extends Transaction {
    InterestChangeTransaction(final CommandInput command) {
        super(command);
        this.setDescription("Interest rate of the account changed to "
                + command.getInterestRate());
    }
}
