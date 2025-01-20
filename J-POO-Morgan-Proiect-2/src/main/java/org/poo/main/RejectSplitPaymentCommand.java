package org.poo.main;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.fileio.CommandInput;
import org.poo.fileio.UserInput;

import static org.poo.main.Helpers.userWithEmail;

public class RejectSplitPaymentCommand implements Command {
    /**
     *
     * @param output       este output-ul oferit comenzii
     * @param commandInput este input-ul oferit comenzii
     */
    @Override
    public void execute(final ArrayNode output, final CommandInput commandInput) {
        UserInput chosenUser = userWithEmail(commandInput.getEmail());
        if (chosenUser == null) {
            return;
        }
        CustomSplitPaymentClass customSplitPayment = null;
        if (commandInput.getSplitPaymentType().equals("custom")) {
            if (chosenUser.getPendingCustomPayments().size() > 0) {
                customSplitPayment = chosenUser.getPendingCustomPayments().getFirst();
            }
        } else {
            if (chosenUser.getPendingEqualPayments().size() > 0) {
                customSplitPayment = chosenUser.getPendingEqualPayments().getFirst();
            }
        }
        if (customSplitPayment != null) {
            customSplitPayment.acceptVote(commandInput);
        }
    }
}
