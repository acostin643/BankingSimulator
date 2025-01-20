package org.poo.main;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.HashSet;

final class CurrencyConverter {
    private final CurrencyGraph currencyGraph;

    CurrencyConverter(final CurrencyGraph currencyGraph) {
        this.currencyGraph = currencyGraph;
    }

    // Se foloseste algoritmul BFS pentru a gasi rata de conversie
    public double convertCurrency(final double amount, final String from, final String to) {
        if (from.equals(to)) {
            return amount; // nu este nevoie de conversie
        }

        Queue<Path> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        queue.add(new Path(from, 1.0));
        visited.add(from);

        while (!queue.isEmpty()) {
            Path current = queue.poll();
            String currentCurrency = current.getCurrency();
            double currentRate = current.getRate();

            // Se verifica vecinii
            for (Edge edge : currencyGraph.getNeighbors(currentCurrency)) {
                if (!visited.contains(edge.getTo())) {
                    double newRate = currentRate * edge.getRate();

                    // Daca se gaseste moneda tinta, se returneaza
                    // valoarea convertita
                    if (edge.getTo().equals(to)) {
                        return amount * newRate;
                    }

                    // Continue BFS
                    queue.add(new Path(edge.getTo(), newRate));
                    visited.add(edge.getTo());
                }
            }
        }

        // Daca nu exista conversie, se returneaza 0
        return 0;
    }
}

