import java.io.*;
import java.net.Socket;

public class ChatClient {
    final String serverName;
    final int port;
    private Socket socket;
    private OutputStream serverOut;
    private BufferedReader bufferedIn;
    private final BufferedReader brc = new BufferedReader(new InputStreamReader(System.in));


    private ChatClient(String serverName, int port) {
        this.serverName = serverName;
        this.port = port;
    }

    public static void main(String[] args) throws IOException {
        ChatClient client = new ChatClient("localhost", 8811);
        client.connect();
    }

    private void readConsole(){
        String inConsole;
        try {
            while((inConsole = brc.readLine()) != null){
                writeToServer(inConsole);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeToServer(String msg){
        try {
            serverOut.write((msg+"\n").getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void connect() {
        try{
            this.socket = new Socket(serverName, port);
            this.serverOut = socket.getOutputStream();
            InputStream serverIn = socket.getInputStream();
            this.bufferedIn = new BufferedReader(new InputStreamReader(serverIn));
            String chatName = "Peter2000";
            serverOut.write(("/name " + chatName + "\n").getBytes());
            startMessageReader();
            readConsole();
            writeConsole("Erfolgreich verbunden");
        } catch (IOException e) {
            writeConsole("Verbindung zum Server fehlgeschlagen");
        }
    }

    private void writeConsole(String s) {
        String dateiname = "log";
       // TODO
        try {
            FileOutputStream outputToLog = new FileOutputStream(dateiname);
            outputToLog.write(s.getBytes());
        } catch (FileNotFoundException e) {
            System.out.println("Log Datei nicht gefunden");
        } catch (IOException e) {
            System.out.println("Log Datei kann nicht geschrieben werden");
        }

        System.out.println(s);
    }

    private void startMessageReader() {
        Thread t = new Thread() {
            @Override
            public void run() {
                readMessageLoop();
            }
        };
        t.start();
    }
    private void readMessageLoop() {
        try {
            String text;
            while ((text = bufferedIn.readLine()) != null) {
                writeConsole(text);
                }
        } catch (Exception ex) {
            writeConsole("Verbindung zum Server verloren");
            logout();
        }
    }

    private void logout() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
