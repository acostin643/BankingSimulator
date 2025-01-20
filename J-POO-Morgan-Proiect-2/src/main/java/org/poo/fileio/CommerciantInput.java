package org.poo.fileio;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public final class CommerciantInput {
    private String commerciant;
    private int id;
    private String account;
    private String type;
    private String cashbackStrategy;

    //camp adaugat de mine, pentru functionalitatea
    //cu codul din prima etapa
    private double amountSpent;

    //camp adaugat de mine, pentru functionalitatea cashback
    private int timesSpent = 0;
}
