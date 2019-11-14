import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class SoPMServer {
    private static ServerSocket serverSocket_;
    private static Socket socket_;
    private static Integer port_ = 5000;
    private final static Logger logger = Logger.getLogger("ServerLog");
    private static FileHandler fileHandler;

    public SoPMServer(Integer port_) {

    }

    public static void closeConnection() {
        try {
            serverSocket_.close();
            logger.info("Server finished.");
        }
        catch (IOException e) { /* cannot happen */ }
    }

    public static void start() throws Exception {
        try {
            socket_ = serverSocket_.accept();
        }
        catch (IOException e) { /* cannot happen */ }
    }

    public static void getClient(String login, String password) throws IOException {
        DatabaseWorker.setConnection();
        if (DatabaseWorker.login(login, password).equals("ok")) {
            DataOutputStream out = new DataOutputStream(socket_.getOutputStream());
            logger.info("Sending request to the ServerSocket");
            JSONObject obj2 = new JSONObject();
            JSONObject obj3 = new JSONObject();
            obj2.put("type", "ok");
            obj2.put("description", obj3);
            out.writeUTF(obj2.toString());
            logger.info("Server Wrote message to client - " + obj2.toString() + ".");
            out.flush();
            out.close();
        } else {
            DataOutputStream out = new DataOutputStream(socket_.getOutputStream());
            logger.info("Sending request to the ServerSocket");
            JSONObject obj2 = new JSONObject();
            JSONObject obj3 = new JSONObject();
            obj2.put("type", "error");
            obj2.put("description", DatabaseWorker.login(login, password));
            out.writeUTF(obj2.toString());
            logger.info("Server Wrote message to client - " + obj2.toString() + ".");
            out.flush();
            out.close();
        }
        DatabaseWorker.closeConnection();
    }

    public static void setNewUser(String login, String password, String email) throws IOException {
        DatabaseWorker.setConnection();
        if (DatabaseWorker.addNewUser(login, password, email).equals("ok")) {
            DataOutputStream out = new DataOutputStream(socket_.getOutputStream());
            logger.info("Sending request to the ServerSocket ...");
            JSONObject obj2 = new JSONObject();
            JSONObject obj3 = new JSONObject();
            obj2.put("type", "ok");
            obj2.put("description", obj3);
            out.writeUTF(obj2.toString());
            logger.info("Server Wrote message to client - " + obj2.toString() + ".");
            out.flush();
            out.close();
        } else {
            DataOutputStream out = new DataOutputStream(socket_.getOutputStream());
            logger.info("Sending request to the ServerSocket ...");
            JSONObject obj2 = new JSONObject();
            JSONObject obj3 = new JSONObject();
            obj2.put("type", "error");
            obj2.put("description", DatabaseWorker.addNewUser(login, password, email));
            out.writeUTF(obj2.toString());
            logger.info("Server Wrote message to client - " + obj2.toString() + ".");
            out.flush();
            out.close();
        }
        DatabaseWorker.closeConnection();
    }

    public static void main(String[] argv) throws Exception {
        serverSocket_ = new ServerSocket(port_);
        try {
            fileHandler = new FileHandler("ServerLog.txt");
            logger.addHandler(fileHandler);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
        } catch (Exception e){
            e.printStackTrace();
        }
        logger.info("Server started ...");
        while (true) {
            start();
            logger.info("Server connected ...");
            try {
                DataInputStream in = new DataInputStream(socket_.getInputStream());
                logger.info("DataInputStream created.");
                logger.info("Server reading from channel ...");
                String entry = in.readUTF();
                logger.info("READ from client message - " + entry + ".");

                JSONParser parser = new JSONParser();
                Object obj1 = parser.parse(entry);
                JSONObject answer = (JSONObject) obj1;
                logger.info("Server try writing to channel ...");
                if (answer.get("type").toString().equals("login")) {
                    answer = (JSONObject) answer.get("description");
                    getClient(answer.get("login").toString(), answer.get("password").toString());
                }
                else if (answer.get("type").toString().equals("registration")) {
                    answer = (JSONObject) answer.get("description");
                    setNewUser(answer.get("login").toString(), answer.get("password").toString(),
                            answer.get("email").toString());
                }

            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}