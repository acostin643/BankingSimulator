package org.poo.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.fileio.UserInput;

import static org.poo.main.Flow.users;

final class PrintUsersCommand implements Command {

    @Override
    public void execute(final ArrayNode output, final CommandInput commandInput) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", "printUsers");
        ArrayNode rezNode = objectMapper.createArrayNode();
        for (UserInput user : users) {
            ObjectNode userNode = objectMapper.createObjectNode();
            userNode.put("firstName", user.getFirstName());
            userNode.put("lastName", user.getLastName());
            userNode.put("email", user.getEmail());

            ArrayNode arrayNode = objectMapper.createArrayNode();
            if (user.getAccounts() != null) {
                for (Account account : user.getAccounts()) {
                    ObjectNode accountNode = objectMapper.createObjectNode();
                    accountNode.put("IBAN", account.getIban());
                    accountNode.put("balance", account.getBalance());
                    accountNode.put("currency", account.getCurrency());
                    accountNode.put("type", account.getAccountType());

                    ArrayNode cardsNode = objectMapper.createArrayNode();
                    for (Card card : account.getCards()) {
                        ObjectNode cardNode
                                = objectMapper.createObjectNode();
                        cardNode.put("cardNumber", card.getCardNumber());
                        if (card.isActive()) {
                            cardNode.put("status", "active");
                        } else {
                            cardNode.put("status", "frozen");
                        }
                        cardsNode.add(cardNode);
                    }
                    accountNode.set("cards", cardsNode);
                    arrayNode.add(accountNode);
                }
            }

            userNode.set("accounts", arrayNode);
            rezNode.add(userNode);
        }

        objectNode.set("output", rezNode);
        objectNode.put("timestamp", commandInput.getTimestamp());
        output.add(objectNode);
    }
}
