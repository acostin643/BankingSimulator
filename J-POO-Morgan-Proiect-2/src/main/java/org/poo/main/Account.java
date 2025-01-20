package org.poo.main;

import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.CommandInput;
import org.poo.fileio.CommerciantInput;
import org.poo.fileio.UserInput;

import java.util.ArrayList;
import java.util.Comparator;

import static org.poo.main.Helpers.userWithEmail;
import static org.poo.utils.Utils.generateIBAN;

@Getter
@Setter
public class Account {
    //aici sunt diversele campuri asociate fiecarui cont
    private String ownerEmail;
    private String iban;
    private double balance;
    private double minimumBalance;
    private String currency;
    private String accountType;
    private String planType;
    private ArrayList<Card> cards;
    private String alias;
    private UserInput ownerUser;
    private ArrayList<Transaction> transactions = new ArrayList<>();
    private ArrayList<OnlinePaymentTransaction> spendingTransactions = new ArrayList<>();
    private ArrayList<Commerciant> commerciants = new ArrayList<>();

    // fiecare variabila cashback poate lua 3 valori:
    // 0, daca utilizatorul nu a primit cashbackul respectiv
    // 1, daca l-a primit dar nu l-a folosit
    // 2, daca l-a primit si l-a folosit
    private int foodCashBack;
    private int clothesCashBack;
    private int techCashBack;


    // variabila spendingStatus, de asemenea, ia 3 valori:
    // 1, daca s-a atins pragul de 100 ron
    // 2, daca s-a atins pragul de 300 ron
    // 3, daca s-a atins pragul de 500 ron
    private int spendingStatus;

    public Account() {
        this.cards = new ArrayList<>();
        this.minimumBalance = 0;
        this.accountType = "classic";
        this.planType = "standard";
        this.foodCashBack = 0;
        this.clothesCashBack = 0;
        this.techCashBack = 0;
        this.spendingStatus = 0;
    }

    public Account(final CommandInput commandInput) {
        this.setBalance(0);
        this.setCurrency(commandInput.getCurrency());
        this.setAccountType(commandInput.getAccountType());
        this.setIban(generateIBAN());
        this.setOwnerUser(userWithEmail(commandInput.getEmail()));
        this.setAlias(null);
        if (this.getOwnerUser().getOccupation().equals("student")) {
            this.setPlanType("student");
        } else if (this.getOwnerUser().getAccounts().get(0) != null) {
            this.setPlanType(this.getOwnerUser().getAccounts().get(0).getPlanType());
        } else {
            this.setPlanType("standard");
        }
    }

    /**
     * @param card este cardul care urmeaza sa fie adaugat in lista contului
     */
    public void addCard(final Card card) {
        cards.add(card);
    }

    /**
     * @param transaction este tranzactia care urmeaza sa fie adaugata in lista contului
     */
    public void addTransaction(final Transaction transaction) {
        this.transactions.add(transaction);
    }

    /**
     * @param spendingTransaction este o tranzactie separata ce este adaugata
     *                            in lista contului, pentru comanda "spendingsReport"
     */
    public void addSpendingTransaction(final OnlinePaymentTransaction spendingTransaction) {
        this.spendingTransactions.add(spendingTransaction);
    }

    /**
     *
     * @param commerciantInput contine informatiile
     *                         comerciantului care va fi adaugat
     *                         in lista contului
     */
    public void addCommerciant(final CommerciantInput commerciantInput) {
        for (Commerciant commerciant : this.commerciants) {
            if (commerciant.getName().equals(commerciantInput.getCommerciant())) {
                return;
            }
        }
        Commerciant newCommerciant = new Commerciant();
        newCommerciant.setName(commerciantInput.getCommerciant());
        this.commerciants.add(newCommerciant);
        this.commerciants.sort(Comparator.comparing(Commerciant::getAmountSpent));
    }


    /**
     * @param spendingTransaction este valoarea care trebuie adaugata
     *                            fiecarui comerciant
     */
    public void addAmountToCommerciant(final OnlinePaymentTransaction spendingTransaction) {
        Commerciant chosenCommerciant = null;
        for (Commerciant commerciant : commerciants) {
            if (commerciant.getName().equals(spendingTransaction.getCommerciant())) {
                chosenCommerciant = commerciant;
            }
        }
        if (chosenCommerciant == null) {
            // daca este null, inseamna ca nu a fost gasit comerciantul si trebuie adaugat
            chosenCommerciant = new Commerciant();
            chosenCommerciant.setName(spendingTransaction.getCommerciant());
            chosenCommerciant.setAmountSpent(spendingTransaction.getAmount());
            commerciants.add(chosenCommerciant);
            //commerciants.sort(Comparator.comparing(Commerciant::getName));
            commerciants.sort(Comparator.comparing(Commerciant::getAmountSpent).reversed());
            // comerciantii trebuie sortati in ordine ASCII
        } else {
            // daca nu este null, inseamna ca exista deja si doar
            // trebuie sa i se mareasca suma asociata
            chosenCommerciant.setAmountSpent(chosenCommerciant.getAmountSpent()
                    + spendingTransaction.getAmount());
        }
    }

    /**
     * @param interestRate reprezinta rata de interes care
     *                     va fi schimbata in cazul unui cont de economii
     */
    public void setInterestRate(final double interestRate) {

    }

    /**
     * aceasta metoda va fi folosita la contul de economii
     */
    public void addInterest() {
        this.setBalance(this.getBalance());
    }
}
