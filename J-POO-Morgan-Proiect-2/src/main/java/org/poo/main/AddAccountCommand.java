package org.poo.main;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.fileio.CommandInput;
import org.poo.fileio.UserInput;

import static org.poo.main.Flow.allAccounts;
import static org.poo.main.Flow.users;
import static org.poo.utils.Utils.generateIBAN;

final class AddAccountCommand implements Command {
    @Override
    public void execute(final ArrayNode output, final CommandInput commandInput) {
        UserInput chosenUser = null;

        for (UserInput user : users) {
            if (user.getEmail().equals(commandInput.getEmail())) {
                chosenUser = user;
            }
        }

        if (chosenUser == null) {
            return;
        }

        Account account = null;
        if (commandInput.getAccountType().equals("savings")) {
            account = new SavingsAccount();
            ((SavingsAccount) account).setInterestRate(commandInput.getInterestRate());
        } else if (commandInput.getAccountType().equals("business")) {
            account = new BusinessAccount();
            account.setOwnerEmail(commandInput.getEmail());
        } else {
            account = new Account();
        }

        //Se seteaza datele initiale ale contului
        account.setBalance(0);
        account.setCurrency(commandInput.getCurrency());
        account.setAccountType(commandInput.getAccountType());
        account.setIban(generateIBAN());
        account.setOwnerUser(chosenUser);
        account.setAlias(null);
        if (chosenUser.getOccupation().equals("student")) {
            account.setPlanType("student");
        }

        //Se genereaza o tranzactie noua asociata creeri noului card
        chosenUser.addAccount(account);
        AccountCreationTransaction accountCreationTransaction
                = new AccountCreationTransaction(commandInput);
        accountCreationTransaction.setAssociatedAccount(account);
        account.addTransaction(accountCreationTransaction);
        chosenUser.addTransaction(accountCreationTransaction);


        //Aici se seteaza statusul contului in cazul in care celelalte
        //conturi ale utilizatorului au primit deja upgrade
        if (chosenUser.getAccounts() != null && chosenUser.getAccounts().getFirst() != null) {
            account.setPlanType(chosenUser.getAccounts().getFirst().getPlanType());
        } else if (chosenUser.getPlanType() != null) {
            account.setPlanType(chosenUser.getPlanType());
        }

        allAccounts.add(account);
    }
}
