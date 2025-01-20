package org.poo.main;

import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.CommandInput;

@Getter
@Setter
final class SavingsAccount extends Account {
    private double interestRate;

    SavingsAccount() {
        super();
        this.setAccountType("savings");
    }

    SavingsAccount(final CommandInput commandInput) {
        super(commandInput);
        this.setInterestRate(commandInput.getInterestRate());
    }

    @Override
    public void setInterestRate(final double interestRate) {
        this.interestRate = interestRate;
    }

    @Override
    public void addInterest() {
        this.setBalance(this.getBalance() * (1 + this.interestRate));
    }

    public void addInterestWithTransaction(final CommandInput commandInput) {
        InterestRateTransaction interestRateTransaction = new InterestRateTransaction(commandInput);
        interestRateTransaction.setAmount(this.getBalance() * this.interestRate);
        interestRateTransaction.setCurrency(this.getCurrency());
        this.getOwnerUser().addTransaction(interestRateTransaction);

        this.setBalance(this.getBalance() * (1 + this.interestRate));
    }
}
