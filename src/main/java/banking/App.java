package banking;

import banking.enums.AppState;
import banking.enums.UserSession;

import java.util.Scanner;

public class App {

    private AppState state;
    private final DatabaseConnection dbConnection;

    App(String dbPath) {
        this.state = AppState.ACTIVE;
        this.dbConnection = new DatabaseConnection(dbPath);
    }

    public void run() throws Exception {

        while (state.equals(AppState.ACTIVE)) {

            System.out.println();
            System.out.println("1. Create an account");
            System.out.println("2. Log into account");
            System.out.println("0. Exit");

            Scanner scanner = new Scanner(System.in);
            int menuChoice = scanner.nextInt();

            switch (menuChoice) {
                case 1:
                    createAccount();
                    break;
                case 2:
                    loginToAccount(dbConnection);
                    break;
                case 0:
                    exitApp();
                    break;
                case 4:
                    dbConnection.getAllCards();
                    break;
                case 5:
                    dbConnection.deleteAllCards();
                    break;
            }
        }
    }


    private void exitApp() {
        System.out.println();
        System.out.println("Bye!");
        this.state = AppState.INACTIVE;
    }

    private void loginToAccount(DatabaseConnection dbConnection) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your card number:");
        long cardNumber = scanner.nextLong();
        System.out.println("Enter your PIN:");
        int pin = scanner.nextInt();

        if (isSuccessfulAuthentication(cardNumber, pin)) {
            System.out.println("You have successfully logged in!");
            Card authenticatedCard = getCardByNumber(cardNumber);
            UserSession userSession = new UserSession(authenticatedCard, dbConnection);
            userSession.userOptions();

            if (userSession.getAppState() == AppState.INACTIVE) {
                exitApp();
            }

        } else {
            System.out.println("Wrong card number or PIN!");
        }
    }

    private boolean isSuccessfulAuthentication(long providedCardNumber, int providedPin) throws Exception {
        Card card = getCardByNumber(providedCardNumber);
        if (card == null) {
            return false;
        }
        return card.getCardNumber() == providedCardNumber && Integer.parseInt(card.getCardPin()) == providedPin;
    }

    private Card getCardByNumber(long cardNumber) throws Exception {
        return dbConnection.getCardByNumber(cardNumber);
    }


    private void createAccount() throws Exception {
        Card newCard = new Card();
        dbConnection.addNewCard(newCard);
    }
}
