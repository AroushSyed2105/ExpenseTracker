import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ExpenseManager {
    private List<Expense> expenses = new ArrayList<>();

    public void addExpense(Expense expense) {
        expenses.add(expense);
    }

    public void editExpense(Expense expense, double newAmount, String newCategory, LocalDate newDate, String newDescription) {
        expense.setAmount(newAmount);
        expense.setCategory(newCategory);
        expense.setDate(newDate);
        expense.setDescription(newDescription);
    }

    public void deleteExpense(Expense expense) {
        expenses.remove(expense);
    }

    public List<Expense> filterByCategory(String category) {
        return expenses.stream()
                .filter(expense -> expense.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public double getTotalExpenses() {
        return expenses.stream().mapToDouble(Expense::getAmount).sum();
    }
}
