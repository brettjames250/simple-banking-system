package banking;

import java.sql.*;
import java.util.Scanner;

public class DatabaseConnection {

    private final String dbPath;
    private final static String DB_URL = "jdbc:sqlite:";

    DatabaseConnection(String dbPath) {
        this.dbPath = dbPath;
        createCardTable();
    }

    private Connection connect(boolean autoCommit) {
        String url = DB_URL + dbPath;
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
            conn.setAutoCommit(autoCommit);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    void createCardTable() {

        String url = DB_URL + dbPath;

        String sql = "CREATE TABLE IF NOT EXISTS card (\n"
                + "	id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n"
                + "	number TEXT,\n"
                + "	pin  TEXT,\n"
                + "	balance INTEGER DEFAULT 0\n"
                + ");";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            if (stmt.execute(sql)) {
                System.out.println("Table created");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    void getAllCards() {
        String sql = "SELECT id, number, pin, balance FROM card";
        try (Connection conn = this.connect(true);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("current cards in DB");

            while (rs.next()) {
                System.out.println(rs.getInt("id") + "\t" +
                        rs.getString("number") + "\t" +
                        rs.getString("pin") + "\t" +
                        rs.getInt("balance"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    void addNewCard(Card newCard) {
        String sql = "INSERT INTO card (number,pin,balance) VALUES(?,?,?)";
        try (Connection conn = this.connect(true);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, newCard.getCardNumber());
            pstmt.setString(2, newCard.getCardPin());
            pstmt.setInt(3, newCard.getBalance());
            if (pstmt.executeUpdate() == 1) {
                newCard.outputCreationMessage();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());

        }
    }

    public void deleteCard(Card card) {
        String sql = "DELETE FROM card WHERE number = ?";
        try (Connection conn = this.connect(true);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, card.getCardNumber());
            pstmt.executeUpdate();
            System.out.println("The account has been closed!");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void deleteAllCards() {
        String sql = "DELETE FROM card";

        try (Connection conn = this.connect(true);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public Card getCardByNumber(long cardNumber) throws Exception {
        Card cardToReturn = new Card();
        String sql = "SELECT id, number, pin, balance FROM card WHERE number = ?";
        try (Connection conn = this.connect(true);

             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, cardNumber);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                cardToReturn.setId(rs.getInt("id"));
                cardToReturn.setCardNumber(Long.parseLong(rs.getString("number")));
                cardToReturn.setCardPin(rs.getString("pin"));
                cardToReturn.setBalance(rs.getInt("balance"));
                return cardToReturn;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void addIncomeToAccount(int amountToAdd, long cardNumber) {
        String sql = "UPDATE card SET balance = balance + ? WHERE number = ?";

        try (Connection conn = this.connect(true);

             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, amountToAdd);
            pstmt.setLong(2, cardNumber);
            pstmt.executeUpdate();
            System.out.println("Income was added!");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    public Object getBalance(long cardNumber) throws Exception {
        Card retrivedCard = getCardByNumber(cardNumber);
        return retrivedCard.getBalance();
    }

    public void doTransfer(Card card, long recipientCardNumber) throws Exception {

        Card recipientAccount = getCardByNumber(recipientCardNumber);

        if (Card.isNotValid(recipientCardNumber)) {
            System.out.println("Probably you made a mistake in the card number. Please try again!");
            return;
        }

        if (recipientAccount == null) {
            System.out.println("Such a card does not exist.");
            return;
        }

        if (recipientAccount.getCardNumber() == card.getCardNumber()) {
            System.out.println("You can't transfer money to the same account!");
            return;
        }

        System.out.println("Enter how much money you want to transfer:");

        Scanner sc = new Scanner(System.in);

        int transferAmount = sc.nextInt();
        int currentBalance = getCardByNumber(card.getCardNumber()).getBalance();

        if (transferAmount > currentBalance) {
            System.out.println("Not enough money!");
            return;
        }


        String removeFundsSQL = "UPDATE card SET balance = balance - ? WHERE number = ?";
        String addFundsSQL = "UPDATE card SET balance = balance + ? WHERE number = ?";

        try (Connection conn = this.connect(false)) {

            try (PreparedStatement removeFunds = conn.prepareStatement(removeFundsSQL);
                 PreparedStatement addFunds = conn.prepareStatement(addFundsSQL)) {

                removeFunds.setLong(1, transferAmount);
                removeFunds.setLong(2, card.getCardNumber());
                removeFunds.executeUpdate();

                addFunds.setLong(1, transferAmount);
                addFunds.setLong(2, recipientCardNumber);
                addFunds.executeUpdate();

                conn.commit();
                System.out.println("Success!");

            }
        } catch (
                SQLException e) {
            System.out.println(e.getMessage());
        }

    }

}


