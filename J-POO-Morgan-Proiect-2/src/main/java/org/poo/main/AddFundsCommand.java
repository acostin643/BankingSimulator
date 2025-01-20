package org.poo.main;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.fileio.CommandInput;

import static org.poo.main.Flow.allAccounts;

final class AddFundsCommand implements Command {
    @Override
    public void execute(final ArrayNode output, final CommandInput commandInput) {
        for (Account account : allAccounts) {
            if (account.getIban().equals(commandInput.getAccount())) {
                if (account.getAccountType().equals("business")) {
                    int rank = ((BusinessAccount) account).roleCheck(commandInput.getEmail());
                    if (rank == 0) {
                        return;
                    } else if (rank == 1) {
                        Associate associate = null;
                        for (Associate a : ((BusinessAccount) account).getEmployeeAssociates()) {
                            if (a.getEmail().equals(commandInput.getEmail())) {
                                associate = a;
                            }
                        }
                        if (associate == null) {
                            return;
                        }
                        if (commandInput.getAmount()
                                > ((BusinessAccount) account)
                                .getDepositingLimit()) {
                            return;
                        }
                    }
                }
                account.setBalance(account.getBalance() + commandInput.getAmount());
            }
        }
    }
}
