package org.poo.main;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.fileio.CommandInput;

import static org.poo.main.Helpers.accountWithIban;
import static org.poo.main.Helpers.generateUpgradeAccountNotFoundError;
import static org.poo.main.Helpers.convertCurrency;
import static org.poo.main.Helpers.ONE_HUNDRED;
import static org.poo.main.Helpers.TWO_HUNDRED_FIFTY;
import static org.poo.main.Helpers.THREE_HUNDRED_FIFTY;

public class UpgradePlanCommand implements Command {

    /**
     *
     * @param output       este output-ul oferit comenzii
     * @param commandInput este input-ul oferit comenzii
     */
    @Override
    public void execute(final ArrayNode output, final CommandInput commandInput) {
        Account targetAccount = accountWithIban(commandInput.getAccount());
        if (targetAccount == null) {
            generateUpgradeAccountNotFoundError(output, commandInput);
            return;
        }

        if (targetAccount.getAccountType().equals("savings")) {
            return;
        }

        if (targetAccount.getPlanType().equals(commandInput.getNewPlanType())) {
            AccountCreationTransaction accountCreationTransaction
                    = new AccountCreationTransaction(commandInput);
            targetAccount.getOwnerUser().addTransaction(accountCreationTransaction);
            return;
        }
        if (commandInput.getNewPlanType().equals("student")
                || commandInput.getNewPlanType().equals("standard")) {
            return;
        }

        if (targetAccount.getPlanType() == "gold"
                && commandInput.getNewPlanType().equals("silver")) {
            CannotDowngradeTransaction cannotDowngradeTransaction
                    = new CannotDowngradeTransaction(commandInput);
            targetAccount.getOwnerUser()
                    .addTransaction(cannotDowngradeTransaction);
            return;
        }

        double price = 0;
        if (targetAccount.getPlanType().equals("student")
                || targetAccount.getPlanType().equals("standard")) {
            if (commandInput.getNewPlanType().equals("silver")) {
                price = ONE_HUNDRED;
            }
            if (commandInput.getNewPlanType().equals("gold")) {
                price = THREE_HUNDRED_FIFTY;
            }
        } else if (targetAccount.getPlanType().equals("silver")
                && commandInput.getNewPlanType().equals("gold")) {
            price = TWO_HUNDRED_FIFTY;
        }

        if (convertCurrency(targetAccount.getBalance(), "RON",
                targetAccount.getCurrency()) < price) {
            InsufficientFundsTransaction insufficientFundsTransaction
                    = new InsufficientFundsTransaction(commandInput);
            targetAccount.addTransaction(insufficientFundsTransaction);
            targetAccount.getOwnerUser().addTransaction(insufficientFundsTransaction);
            return;
        }

        targetAccount.setBalance(targetAccount.getBalance()
                - convertCurrency(price, targetAccount.getCurrency(), "RON"));
        targetAccount.setPlanType(commandInput.getNewPlanType());
        targetAccount.getOwnerUser().setPlanType(commandInput.getNewPlanType());
        for (Account account : targetAccount.getOwnerUser().getAccounts()) {
            account.setPlanType(commandInput.getNewPlanType());
        }
        UpgradePlanTransaction upgradePlanTransaction
                = new UpgradePlanTransaction(commandInput);
        targetAccount.getOwnerUser().addTransaction(upgradePlanTransaction);
    }
}
