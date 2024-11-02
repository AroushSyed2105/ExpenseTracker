import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.ArrayList;

public class ExpenseTrackerApp extends Application {

    private ExpenseManager expenseManager = new ExpenseManager();
    private Budget budget;
    private Label budgetStatusLabel = new Label(); // Label to show budget status

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Expense Tracker");

        // Budget entry screen
        VBox budgetEntryLayout = new VBox(10);
        Label budgetPrompt = new Label("Enter your monthly budget:");
        TextField budgetInput = new TextField();
        budgetInput.setPromptText("Monthly Budget");
        Button setBudgetButton = new Button("Set Budget");

        setBudgetButton.setOnAction(e -> {
            double monthlyLimit = Double.parseDouble(budgetInput.getText());
            budget = new Budget(monthlyLimit);
            showMainPage(primaryStage);
        });

        budgetEntryLayout.getChildren().addAll(budgetPrompt, budgetInput, setBudgetButton);
        Scene budgetScene = new Scene(budgetEntryLayout, 300, 200);
        primaryStage.setScene(budgetScene);
        primaryStage.show();
    }

    private void showMainPage(Stage primaryStage) {
        TableView<Expense> tableView = new TableView<>();
        tableView.setPlaceholder(new Label("No expenses added"));

        // Add columns
        TableColumn<Expense, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<Expense, Double> amountColumn = new TableColumn<>("Amount");
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));

        TableColumn<Expense, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<Expense, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        tableView.getColumns().addAll(categoryColumn, amountColumn, dateColumn, descriptionColumn);

        // Input fields
        TextField categoryInput = new TextField();
        categoryInput.setPromptText("Category");

        TextField amountInput = new TextField();
        amountInput.setPromptText("Amount");

        TextField dateInput = new TextField();
        dateInput.setPromptText("Date (yyyy-mm-dd)");

        TextField descriptionInput = new TextField();
        descriptionInput.setPromptText("Description");

        // Add Expense Button with budget check
        Button addButton = new Button("Add Expense");
        addButton.setOnAction(e -> {
            String category = categoryInput.getText();
            double amount = Double.parseDouble(amountInput.getText());
            LocalDate date = LocalDate.parse(dateInput.getText());
            String description = descriptionInput.getText();

            Expense expense = new Expense(category, amount, date, description);
            expenseManager.addExpense(expense);
            tableView.getItems().add(expense);

            budget.updateTotal(amount);
            updateBudgetStatus();
            DataManager.saveExpenses(new ArrayList<>(tableView.getItems()));

        });

        // Edit Expense Button
        Button editButton = new Button("Edit Expense");
        editButton.setOnAction(e -> {
            Expense selectedExpense = tableView.getSelectionModel().getSelectedItem();
            if (selectedExpense != null) {
                String newCategory = categoryInput.getText();
                double newAmount = Double.parseDouble(amountInput.getText());
                LocalDate newDate = LocalDate.parse(dateInput.getText());
                String newDescription = descriptionInput.getText();

                double oldAmount = selectedExpense.getAmount();
                expenseManager.editExpense(selectedExpense, newAmount, newCategory, newDate, newDescription);
                tableView.refresh(); // Refresh the table view to show updated expense

                budget.updateTotal(newAmount - oldAmount); // Update budget with the difference
                updateBudgetStatus();
            } else {
                showAlert("Please select an expense to edit.");
            }
        });

        // Remove Expense Button
        Button removeButton = new Button("Remove Expense");
        removeButton.setOnAction(e -> {
            Expense selectedExpense = tableView.getSelectionModel().getSelectedItem();
            if (selectedExpense != null) {
                tableView.getItems().remove(selectedExpense);
                expenseManager.deleteExpense(selectedExpense);

                budget.updateTotal(-selectedExpense.getAmount()); // Deduct from budget
                updateBudgetStatus();
            } else {
                showAlert("Please select an expense to remove.");
            }
        });

        // Get Subtotal Button
        Button subtotalButton = new Button("Get Subtotal");
        subtotalButton.setOnAction(e -> {
            double subtotal = expenseManager.getTotalExpenses();
            showAlert("Subtotal of all expenses: $" + String.format("%.2f", subtotal));
        });

        VBox layout = new VBox(10, tableView, categoryInput, amountInput, dateInput, descriptionInput,
                addButton, editButton, removeButton, subtotalButton, budgetStatusLabel);
        Scene mainScene = new Scene(layout, 600, 500);

        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    private void updateBudgetStatus() {
        if (budget.isOverBudget()) {
            budgetStatusLabel.setText("Warning: You are over your monthly budget!");
            budgetStatusLabel.setStyle("-fx-text-fill: red;");
        } else {
            budgetStatusLabel.setText("Remaining Budget: $" + String.format("%.2f", budget.getRemainingBudget()));
            budgetStatusLabel.setStyle("-fx-text-fill: black;");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
