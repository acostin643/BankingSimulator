package org.poo.main;

import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.*;

import java.util.ArrayList;

import static org.poo.main.Helpers.*;
import static org.poo.utils.Utils.resetRandom;

@Getter
@Setter
final class Flow {
    static UserInput[] users;
    static CommerciantInput[] commerciantInputs;
    static ExchangeInput[] exchangeRates;
    private static CommandInput[] commands;
    static ArrayList<Account> allAccounts = new ArrayList<>();
    static CurrencyGraph graph = new CurrencyGraph();

    private Flow() {

    }

    /**
     * @param inputData
     * @param output
     */
    public static void processInput(final ObjectInput inputData, final ArrayNode output) {
        resetRandom();
        resetVariables();

        users = inputData.getUsers();
        commerciantInputs = inputData.getCommerciants();
        exchangeRates = inputData.getExchangeRates();
        commands = inputData.getCommands();

        for (ExchangeInput exchange : exchangeRates) {
            graph.addRate(exchange.getTo(), exchange.getFrom(), exchange.getRate());
        }

        Handler handler = new Handler();

        for (CommandInput newCommand : commands) {
            handler.handleCommand(output, newCommand);
        }
    }
}
