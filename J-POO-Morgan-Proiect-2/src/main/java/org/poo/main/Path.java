package org.poo.main;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
class Path {
    private String currency;
    private double rate;

    Path(final String currency, final double rate) {
        this.currency = currency;
        this.rate = rate;
    }
}
