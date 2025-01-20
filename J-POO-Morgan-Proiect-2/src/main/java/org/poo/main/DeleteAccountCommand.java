package org.poo.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.fileio.UserInput;

import static org.poo.main.Flow.allAccounts;
import static org.poo.main.Helpers.accountWithIban;
import static org.poo.main.Helpers.accountWithAlias;
import static org.poo.main.Helpers.userWithEmail;

final class DeleteAccountCommand implements Command {
    @Override
    public void execute(final ArrayNode output, final CommandInput commandInput) {
        Account account = accountWithIban(commandInput.getAccount());
        if (account == null) {
            return;
        }
        if (!account.getOwnerUser().getEmail().equals(commandInput.getEmail())) {
            return;
        }
        UserInput user = userWithEmail(commandInput.getEmail());
        if (user == null) {
            return;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", "deleteAccount");
        ObjectNode statusNode = objectMapper.createObjectNode();

        if (account.getBalance() != 0) {
            statusNode.put("error",
                    "Account couldn't be deleted "
                            + "- see org.poo.transactions for details");
            FundsRemainingTransaction fundsRemainingTransaction =
                    new FundsRemainingTransaction(commandInput);
            account.getOwnerUser().addTransaction(fundsRemainingTransaction);
        } else {
            statusNode.put("success", "Account deleted");
            user.getAccounts().remove(account);
            CardDestroyedTransaction cardDestroyedTransaction =
                    new CardDestroyedTransaction(commandInput);
            account.getOwnerUser().addTransaction(cardDestroyedTransaction);

            allAccounts.remove(account);
        }
        statusNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("output", statusNode);
        objectNode.put("timestamp", commandInput.getTimestamp());
        output.add(objectNode);
    }
}
