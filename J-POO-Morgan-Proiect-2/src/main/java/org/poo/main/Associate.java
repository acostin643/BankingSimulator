package org.poo.main;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Associate {
    private String email;
    private double amountSpent;
    private double amountDeposited;

    public Associate() {
        this.amountSpent = 0;
        this.amountDeposited = 0;
    }
}
