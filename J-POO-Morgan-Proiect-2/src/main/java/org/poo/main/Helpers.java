package org.poo.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.fileio.CommerciantInput;
import org.poo.fileio.UserInput;

import java.time.LocalDate;
import java.time.Period;

import static org.poo.main.Flow.users;
import static org.poo.main.Flow.exchangeRates;
import static org.poo.main.Flow.allAccounts;
import static org.poo.main.Flow.graph;
import static org.poo.main.Flow.commerciantInputs;

final class Helpers {
    private Helpers() {

    }

    public static final double POINT_ONE_PERCENT = 0.001;
    public static final double POINT_TWO_PERCENT = 0.002;
    public static final double POINT_FOUR_PERCENT = 0.004;
    public static final double POINT_FIVE_PERCENT = 0.005;
    public static final int THREE = 3;
    public static final int FIVE = 5;
    public static final int TEN = 10;
    public static final int ONE_HUNDRED = 100;
    public static final int TWO_HUNDRED_FIFTY = 250;
    public static final int THREE_HUNDRED = 300;
    public static final int THREE_HUNDRED_FIFTY = 350;
    public static final int FIVE_HUNDRED = 500;

    public static void resetVariables() {
        users = null;
        exchangeRates = null;
        allAccounts.clear();
        graph = new CurrencyGraph();
    }

    static void generateCardNotFoundError(final ArrayNode output,
                                          final CommandInput commandInput) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        ObjectNode statusNode = objectMapper.createObjectNode();
        statusNode.put("description", "Card not found");
        statusNode.put("timestamp", commandInput.getTimestamp());
        objectNode.set("output", statusNode);
        objectNode.put("timestamp", commandInput.getTimestamp());
        output.add(objectNode);
    }

    static UserInput userWithEmail(final String email) {
        for (UserInput user : users) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }
        return null;
    }

    static Account accountWithIban(final String iban) {
        for (Account account : allAccounts) {
            if (account.getIban().equals(iban)) {
                return account;
            }
        }
        return null;
    }

    static Account accountWithAlias(final String alias) {
        if (alias == null) {
            return null;
        }
        for (Account account : allAccounts) {
            if (account.getAlias().equals(alias)) {
                return account;
            }
        }
        return null;
    }

    public static Card cardWithCardNumber(final String cardNumber) {
        for (Account account : allAccounts) {
            for (Card card : account.getCards()) {
                if (card.getCardNumber().equals(cardNumber)) {
                    return card;
                }
            }
        }
        return null;
    }

    public static CommerciantInput commerciantWithName(final String name) {
        for (CommerciantInput commerciant : commerciantInputs) {
            if (commerciant.getCommerciant().equals(name)) {
                return commerciant;
            }
        }
        return null;
    }

    static void generatePayOnlineError(final ArrayNode output,
                                       final CommandInput commandInput) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", "payOnline");
        ObjectNode statusNode = objectMapper.createObjectNode();
        statusNode.put("description", "Card not found");
        statusNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("output", statusNode);
        objectNode.put("timestamp", commandInput.getTimestamp());
        output.add(objectNode);
    }

    static double convertCurrency(final double amount, final String from, final String to) {
        CurrencyConverter converter = new CurrencyConverter(graph);
        return converter.convertCurrency(amount, from, to);
    }

    static void generateReportAccountNotFoundError(final ArrayNode output,
final CommandInput commandInput, final int errorCode) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        if (errorCode == 0) {
            objectNode.put("command", "report");
        }
        if (errorCode == 1) {
            objectNode.put("command", "spendingsReport");
        }

        ObjectNode outputNode = objectMapper.createObjectNode();
        outputNode.put("description", "Account not found");
        outputNode.put("timestamp", commandInput.getTimestamp());

        objectNode.put("output", outputNode);
        objectNode.put("timestamp", commandInput.getTimestamp());

        output.add(objectNode);
    }

    static void generateUpgradeAccountNotFoundError(final ArrayNode output,
                                                    final CommandInput commandInput) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", "upgradePlan");

        ObjectNode outputNode = objectMapper.createObjectNode();
        outputNode.put("description", "Account not found");
        outputNode.put("timestamp", commandInput.getTimestamp());

        objectNode.put("output", outputNode);
        objectNode.put("timestamp", commandInput.getTimestamp());

        output.add(objectNode);
    }

    static void generateUserNotFoundError(final ArrayNode output,
                                          final CommandInput commandInput) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        ObjectNode statusNode = objectMapper.createObjectNode();
        statusNode.put("description", "User not found");
        statusNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("output", statusNode);
        objectNode.put("timestamp", commandInput.getTimestamp());

        output.add(objectNode);
    }

    public static void generateNotSavingsAccountError(final ArrayNode output,
final CommandInput commandInput, final int errorCode) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        if (errorCode == 0) {
            objectNode.put("command", "changeInterestRate");
        }
        if (errorCode == 1) {
            objectNode.put("command", "addInterest");
        }

        ObjectNode outputNode = objectMapper.createObjectNode();
        outputNode.put("description", "This is not a savings account");
        outputNode.put("timestamp", commandInput.getTimestamp());

        objectNode.put("output", outputNode);
        objectNode.put("timestamp", commandInput.getTimestamp());

        output.add(objectNode);
    }

    public static void generateSpendingsReportSavingsError(final ArrayNode output,
                                                           final CommandInput commandInput) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", "spendingsReport");
        ObjectNode outputNode = objectMapper.createObjectNode();
        outputNode.put("error",
                "This kind of report is not "
                        + "supported for a saving account");
        objectNode.put("output", outputNode);
        objectNode.put("timestamp", commandInput.getTimestamp());
        output.add(objectNode);
    }

    public static Boolean isLegalAge(final String birthDateString) {
        LocalDate birthDate = LocalDate.parse(birthDateString);
        LocalDate currentDate = LocalDate.now();

        int age = Period.between(birthDate, currentDate).getYears();

        if (age >= 21) {
            return true;
        }
        return false;
    }

    public static String formatEmailToName(final String email) {
        String namePart = email.split("@")[0];
        String formattedName = namePart.replace("_", " ");
        return formattedName;
    }
}
