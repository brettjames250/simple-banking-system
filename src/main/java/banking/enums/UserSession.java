package banking.enums;

import banking.Card;
import banking.DatabaseConnection;

import java.sql.SQLException;
import java.util.Scanner;

public class UserSession {

    enum SessionState {
        ACTIVE, INACTIVE
    }

    private AppState appState;
    private SessionState sessionState;
    private final Card card;
    private DatabaseConnection dbConnection;


    public UserSession(Card card, DatabaseConnection dbConnection) {
        this.sessionState = SessionState.ACTIVE;
        this.appState = AppState.ACTIVE;
        this.card = card;
        this.dbConnection = dbConnection;
    }

    public void userOptions() throws Exception {
        Scanner scanner = new Scanner(System.in);

        while (sessionState == SessionState.ACTIVE) {

            printUserOptions();
            int menuOption = scanner.nextInt();

            switch (menuOption) {
                case 1:
                    printBalance();
                    break;
                case 2:
                    addIncome(scanner);
                    break;
                case 3:
                    doTransfer(scanner);
                    break;
                case 4:
                    closeAccount();
                    break;
                case 5:
                    logOut();
                    break;
                case 0:
                    exitApp();
                    break;
                default:
                    System.out.println("Please select a valid option");
            }
        }
    }

    private void closeAccount() {
        dbConnection.deleteCard(card);
        logOut();
    }

    private void doTransfer(Scanner sc) throws Exception {
        System.out.println("Transfer");
        System.out.println("Enter card number:");
        long recipientAccountNumber = sc.nextLong();
        dbConnection.doTransfer(card, recipientAccountNumber);
    }

    private void addIncome(Scanner sc) {
        System.out.println("Enter income:");
        int amountToAdd = sc.nextInt();
        dbConnection.addIncomeToAccount(amountToAdd, card.getCardNumber());
    }

    private void exitApp() {
        this.appState = AppState.INACTIVE;
        this.sessionState = SessionState.INACTIVE;
    }

    private void logOut() {
        this.sessionState = SessionState.INACTIVE;
    }

    private void printBalance() throws Exception {
        System.out.println("Balance: " + dbConnection.getBalance(card.getCardNumber()));
    }

    public AppState getAppState() {
        return appState;
    }

    private void printUserOptions() {
        System.out.println();
        System.out.println("1. Balance");
        System.out.println("2. Add income");
        System.out.println("3. Do transfer");
        System.out.println("4. Close account");
        System.out.println("5. Log out");
        System.out.println("0. Exit");
    }
}
