package org.poo.main;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
class Edge {
    private String to;
    private double rate;

    Edge(final String to, final double rate) {
        this.to = to;
        this.rate = rate;
    }
}
