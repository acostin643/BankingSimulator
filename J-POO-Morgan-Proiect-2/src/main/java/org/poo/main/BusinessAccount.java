package org.poo.main;

import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.CommandInput;

import java.util.ArrayList;

import static org.poo.main.Helpers.THREE;
import static org.poo.main.Helpers.FIVE_HUNDRED;
import static org.poo.main.Helpers.userWithEmail;

@Getter
@Setter
public class BusinessAccount extends Account {
    private String ownerEmail;
    private ArrayList<Associate> managerAssociates = new ArrayList<>();
    private ArrayList<Associate> employeeAssociates = new ArrayList<>();
    private double spendingLimit = FIVE_HUNDRED;
    private double depositingLimit = FIVE_HUNDRED;
    private double totalDeposited = 0;
    private double totalSpent = 0;

    BusinessAccount() {
        super();
        this.setAccountType("business");
    }

    /**
     *
     * @param commandInput este comanda de tip
     *                     "addNewBusinessAssociate"
     *                     folosita pentru a crea un
     *                     nou cont business
     */
    public void addNewBusinessAssociate(final CommandInput commandInput) {
        String associateUserEmail = commandInput.getEmail();
        if (userWithEmail(associateUserEmail) == null) {
            return;
        }
        if (commandInput.getRole().equals("employee")) {
            Associate associate = new Associate();
            associate.setEmail(associateUserEmail);
            this.getEmployeeAssociates().add(associate);
        } else if (commandInput.getRole().equals("manager")) {
            Associate associate = new Associate();
            associate.setEmail(associateUserEmail);
            this.getManagerAssociates().add(associate);
        }
    }

    /**
     *
     * @param email este emailul unui cont pe care vrem sa il verificam daca este asociate
     * @return se va returna gradul utilizatorului cu emailul respectiv
     */
    public int roleCheck(final String email) {
        if (ownerEmail.equals(email)) {
            return THREE;  //se returneaza 3 daca este detinatorul
        }
        for (Associate associate : managerAssociates) {
            if (associate.getEmail().equals(email)) {
                return 2; // 2 daca este manager
            }
        }
        for (Associate associate : employeeAssociates) {
            if (associate.getEmail().equals(email)) {
                return 1; // 1 daca este angajat
            }
        }
        return 0; // 0 daca nu este asociat
    }
}
