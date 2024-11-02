public class Budget {
    private double monthlyLimit;
    private double currentTotal = 0;

    public Budget(double monthlyLimit) {
        this.monthlyLimit = monthlyLimit;
    }

    public void updateTotal(double amount) {
        currentTotal += amount;
    }

    public boolean isOverBudget() {
        return currentTotal > monthlyLimit;
    }

    public double getRemainingBudget() {
        return monthlyLimit - currentTotal;
    }
}