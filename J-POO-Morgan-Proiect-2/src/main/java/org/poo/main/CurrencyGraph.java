package org.poo.main;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

final class CurrencyGraph {
    private final Map<String, List<Edge>> graph = new HashMap<>();

    // Se adauga o rata de conversie la graf
    public void addRate(final String from, final String to, final double rate) {
        graph.putIfAbsent(from, new ArrayList<>());
        graph.putIfAbsent(to, new ArrayList<>());
        graph.get(from).add(new Edge(to, rate));
        graph.get(to).add(new Edge(from, 1 / rate)); // Symmetrical rate
    }

    // Returneaza vecinii unui nod de valuta
    public List<Edge> getNeighbors(final String currency) {
        return graph.getOrDefault(currency, new ArrayList<>());
    }
}
