package org.poo.main;

import java.util.HashMap;
import java.util.Map;

final class CommandFactory {
    private static final Map<String, Class<? extends Command>> commands = new HashMap<>();

    private CommandFactory() {

    }

    static {
        commands.put("printUsers", PrintUsersCommand.class);
        commands.put("addAccount", AddAccountCommand.class);
        commands.put("addFunds", AddFundsCommand.class);
        commands.put("createCard", CreateCardCommand.class);
        commands.put("createOneTimeCard", CreateOneTimeCardCommand.class);
        commands.put("deleteAccount", DeleteAccountCommand.class);
        commands.put("deleteCard", DeleteCardCommand.class);
        commands.put("payOnline", PayOnlineCommand.class);
        commands.put("sendMoney", SendMoneyCommand.class);
        commands.put("printTransactions", PrintTransactionsCommand.class);
        commands.put("setAlias", SetAliasCommand.class);
        commands.put("setMinimumBalance", SetMinimumBalanceCommand.class);
        commands.put("checkCardStatus", CheckCardStatusCommand.class);
        commands.put("splitPayment", SplitPaymentIntroCommand.class);
        commands.put("report", ReportCommand.class);
        commands.put("spendingsReport", SpendingsReportCommand.class);
        commands.put("changeInterestRate", ChangeInterestRateCommand.class);
        commands.put("addInterest", AddInterestCommand.class);
        commands.put("withdrawSavings", WithdrawSavingsCommand.class);
        commands.put("upgradePlan", UpgradePlanCommand.class);
        commands.put("cashWithdrawal", CashWithdrawalCommand.class);
        commands.put("acceptSplitPayment", AcceptSplitPaymentCommand.class);
        commands.put("rejectSplitPayment", RejectSplitPaymentCommand.class);
    }

    public static Command getCommand(final String commandName) {
        Class<? extends Command> commandClass = commands.get(commandName);
        if (commandClass == null) {
            return null;
        }

        try {
            return commandClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            // Log the error if needed
            return null; // Return null in case of instantiation failure
        }
    }
}
