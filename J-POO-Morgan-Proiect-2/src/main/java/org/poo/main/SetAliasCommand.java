package org.poo.main;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.fileio.CommandInput;

import static org.poo.main.Helpers.accountWithIban;

final class SetAliasCommand implements Command {
    @Override
    public void execute(final ArrayNode output, final CommandInput commandInput) {
        Account account = accountWithIban(commandInput.getAccount());
        if (account == null) {
            return;
        }
        if (!account.getOwnerUser().getEmail().equals(commandInput.getEmail())) {
            return;
        }
        account.setAlias(commandInput.getAlias());
    }
}
