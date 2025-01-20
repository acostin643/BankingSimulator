package org.poo.fileio;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.poo.main.Account;
import org.poo.main.CustomSplitPaymentClass;
import org.poo.main.Transaction;

import java.util.ArrayList;
import java.util.Comparator;

@Data
@NoArgsConstructor
public final class UserInput {
    private String firstName;
    private String lastName;
    private String email;
    private String birthDate;
    private String occupation;

    private String planType;
    private ArrayList<CustomSplitPaymentClass> pendingCustomPayments = new ArrayList<>();
    private ArrayList<CustomSplitPaymentClass> pendingEqualPayments = new ArrayList<>();

    // camp adaugat de mine; pentru trecerea de la silver la gold gratuita
    private int nrOfTransactionsOver300;

    /**
     * creste nr de tranzactii peste 300 lei
     */
    public void increaseNrOfTransactionsOver300() {
        this.nrOfTransactionsOver300++;
    }

    //clasa adaugata de mine; reprezinta conturile asociate fiecarui utilizator
    private ArrayList<Account> accounts = new ArrayList<>();
    /**
     * @param account reprezinta contul care urmeaza sa fie
     *               adaugat in lista de conturi a utilizatorului
     */
    public void addAccount(final Account account) {
        this.accounts.add(account);
    }

    private ArrayList<Transaction> transactions = new ArrayList<>();

    /**
     * @param transaction reprezinta tranzactia care
     *                    urmeaza sa fie adaugata in lista de
     *                    tranzactii a utilizatorului
     */
    public void addTransaction(final Transaction transaction) {
        this.transactions.add(transaction);
        this.transactions.sort(Comparator.comparing(Transaction::getTimestamp));
    }

    /**
     *
     * @param customSplitPayment
     */
    public void addPendingCustomPayment(final CustomSplitPaymentClass customSplitPayment) {
        this.getPendingCustomPayments().add(customSplitPayment);
    }

    /**
     *
     * @param customSplitPayment
     */
    public void addPendingEqualPayment(final CustomSplitPaymentClass customSplitPayment) {
        this.getPendingEqualPayments().add(customSplitPayment);
    }
}
