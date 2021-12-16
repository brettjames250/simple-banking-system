package banking;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Card {
    private int id;
    private long cardNumber;
    private String cardPin;
    private int balance;


    Card() throws Exception {
        generateCardNewCard();
        this.balance = 0;
    }

    private void generateCardNewCard() throws Exception {
        this.cardNumber = generateCardNumber();
        this.cardPin = generatePin();
    }

    void outputCreationMessage() {
        System.out.println();
        System.out.println("Your card has been created");
        System.out.println("Your card number:");
        System.out.println(this.cardNumber);
        System.out.println("Your card PIN:");
        System.out.println(this.cardPin);
    }


    private String generatePin() {
        Random random = new Random();
        return String.format("%04d", random.nextInt(10000));
    }

    private long generateCardNumber() throws Exception {
        // Bank Identification Number (6 numbers)
        String bankIdNum = "400000";

        // Account Identifier (9 digits)
        String accountIdNum = getAccountIdentifier();

        // Card number before check digit
        String cardNumberWithoutCheckDigit = bankIdNum + accountIdNum;

        // Check number
        String checkNumber = getCheckDigit(cardNumberWithoutCheckDigit);

        String validatedCardNumber = cardNumberWithoutCheckDigit + checkNumber;

        if (validatedCardNumber.length() != 16) {
            throw new Exception("NOT A 16 DIGIT CARD");
        }

        return Long.parseLong(validatedCardNumber);
    }

    static String getCheckDigit(String cardNumber) {

        Map<Integer, Integer> cardDigits = new HashMap<Integer, Integer>();

        for (int i = 0; i < cardNumber.length(); i++) {
            cardDigits.put(i + 1, Integer.parseInt(String.valueOf(cardNumber.charAt(i))));
        }

        int sumOfNumbers = 0;

        for (Map.Entry<Integer, Integer> number : cardDigits.entrySet()) {
            if (number.getKey() % 2 != 0) {
                cardDigits.put(number.getKey(), number.getValue() * 2);
            }
            if (number.getValue() > 9) {
                cardDigits.put(number.getKey(), number.getValue() - 9);
            }

            sumOfNumbers += number.getValue();
        }

        int checksum = findChecksum(sumOfNumbers);
        return String.valueOf(checksum);
    }

    public static boolean isNotValid(long cardNumber) {
        String cardString = String.valueOf(cardNumber);
        char checkDigit = cardString.charAt(cardString.length() - 1);

        Map<Integer, Integer> cardDigits = new HashMap<Integer, Integer>();

        for (int i = 0; i < cardString.length() - 1; i++) {
            cardDigits.put(i + 1, Integer.parseInt(String.valueOf(cardString.charAt(i))));
        }

        int sumOfNumbers = 0;

        for (Map.Entry<Integer, Integer> number : cardDigits.entrySet()) {
            if (number.getKey() % 2 != 0) {
                cardDigits.put(number.getKey(), number.getValue() * 2);
            }
            if (number.getValue() > 9) {
                cardDigits.put(number.getKey(), number.getValue() - 9);
            }

            sumOfNumbers += number.getValue();
        }

        int checksum = findChecksum(sumOfNumbers);

        return checksum != Character.getNumericValue(checkDigit);
    }

    static int findChecksum(int number) {
        if (number % 10 == 0) {
            return 0;
        } else {
            return 10 - (number % 10);
        }
    }

    static String getAccountIdentifier() {
        int min = (int) Math.pow(10, 9 - 1);
        int custAccNumber = min + new Random().nextInt(9 * min);
        return String.valueOf(custAccNumber);
    }

    public void setCardNumber(long cardNumber) {
        this.cardNumber = cardNumber;
    }

    public void setCardPin(String cardPin) {
        this.cardPin = cardPin;
    }


    public void setId(int id) {
        this.id = id;
    }

    public long getCardNumber() {
        return cardNumber;
    }

    public String getCardPin() {
        return cardPin;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }
}
