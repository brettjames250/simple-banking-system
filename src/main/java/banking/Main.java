package banking;

public class Main {

    public static void main(String[] args) throws Exception {
        String dbPath = args[1];
        App app = new App(dbPath);
        app.run();
    }

}