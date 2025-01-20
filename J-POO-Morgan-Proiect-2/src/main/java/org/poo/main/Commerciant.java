package org.poo.main;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Commerciant {
    private String name;
    private double amountSpent;
    private int timesSpent;

    public Commerciant() {
        this.name = "none";
        this.amountSpent = 0;
        this.timesSpent = 0;
    }

    /**
     * @param amount
     */
    public void addAmountSpent(final double amount) {
        this.amountSpent += amount;
    }
}
