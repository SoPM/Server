import java.lang.reflect.InvocationTargetException;
import java.sql.*;

public class DatabaseWorker {
    private static final String URL = "jdbc:mysql://localhost:3306/sopm?serverTimezone=Europe/Moscow&useSSL=false";
    private static final String USER_NAME = "SoPMAdmin";
    private static final String PASSWORD = "MPoS1nimdA!";

    private static Connection connection_ = null;

    public static void setConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            connection_ = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
            System.out.println("Connection established...");
        } catch (SQLException e) {
            System.out.println("Connection failed ...");
            System.out.println(e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void closeConnection() {
        try {
            connection_.close();
        } catch (SQLException e) { /* ignored */ }
    }

    public static String addNewUser(String login, String password, String email) {
        try {
            Statement statement = connection_.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT login FROM users WHERE login = '" + login + "'");
            boolean tmp = false;
            while(resultSet.next() && !tmp) {
                System.out.println(resultSet.getString(1));
                if (resultSet.getString(1) == login) {
                    tmp = true;
                }
            }
            if (tmp)
                return "Such user exists";
            else {
                statement.executeUpdate("INSERT INTO users (login, password, email) "
                        + "VALUES ( '" + login + "', '" + password + "', '" + email + "')");
                return "ok";
            }
        } catch (SQLException e) {
            System.out.println("Adding new user failed ...");
            System.out.println(e);
            return String.valueOf(e);
        }
    }

    public static String login(String login, String password) {
        try {
            Statement statement = connection_.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT login, password FROM users WHERE login = '" + login
                    + "' AND password = '" + password + "'");
            boolean tmp = false;
            while(resultSet.next() && !tmp) {
                if (resultSet.getString(1).equals(login) && resultSet.getString(2).equals(password)) {
                    tmp = true;
                    return "ok";
                }
            }
            return "Login/password is incorrected";
        } catch (SQLException e) {
            System.out.println("Login failed ...");
            System.out.println(e);
            return String.valueOf(e);
        }
    }
}
