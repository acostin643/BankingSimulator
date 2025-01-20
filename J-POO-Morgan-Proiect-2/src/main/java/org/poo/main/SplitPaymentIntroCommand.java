package org.poo.main;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.fileio.CommandInput;

import java.util.ArrayList;
import java.util.List;

import static org.poo.main.Helpers.accountWithIban;

public class SplitPaymentIntroCommand implements Command {
    @Override
    public void execute(final ArrayNode output, final CommandInput commandInput) {
        ArrayList<Account> involvedAccounts = new ArrayList<>();
        List<String> ibans = commandInput.getAccounts();

        for (String iban : ibans) {
            Account account = accountWithIban(iban);
            if (account == null) {
                return;
            }
            if (account.getAccountType().equals("business")) {
                System.out.println("janau (splitpaymentintrocommand)");
            }
            involvedAccounts.add(account);
        }

        ArrayList<Double> amounts = null;
        if (commandInput.getSplitPaymentType().equals("custom")) {
            amounts = (ArrayList<Double>) commandInput.getAmountForUsers();
        } else {
            double amount = commandInput.getAmount();
            amounts = new ArrayList<>();
            for (Account involvedAccount : involvedAccounts) {
                amounts.add(amount / involvedAccount.getCards().size());
            }
        }
        if (amounts == null) {
            return;
        }

        CustomSplitPaymentClass customSplitPayment = new CustomSplitPaymentClass();
        for (Account account : involvedAccounts) {
            customSplitPayment.addAccount(account);
        }
        for (Double amount : amounts) {
            customSplitPayment.addAmount(amount);
        }

        customSplitPayment.setAssociatedCommand(commandInput);

        for (Account account : involvedAccounts) {
            if (commandInput.getSplitPaymentType().equals("custom")) {
                account.getOwnerUser().addPendingCustomPayment(customSplitPayment);
            } else {
                account.getOwnerUser().addPendingEqualPayment(customSplitPayment);
            }
        }
    }
}
