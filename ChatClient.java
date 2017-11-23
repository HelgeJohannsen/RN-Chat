import java.io.*;
import java.net.Socket;

public class ChatClient {
    final String serverName;
    final int port;
    private Socket socket;
    private OutputStream serverOut;
    private BufferedReader bufferedIn;
    private boolean connected = false;
    private String chatName = "defaultName";
    private final BufferedReader brc = new BufferedReader(new InputStreamReader(System.in));


    private ChatClient(String serverName, int port) {
        this.serverName = serverName;
        this.port = port;
    }

    public static void main(String[] args) throws IOException {
        ChatClient client = new ChatClient("localhost", 8811);
        client.start();
    }

    private void start() {
        readConsole();
        connected = connect();
    }

    private void readConsole(){
        String inConsole;
        try {
            while((inConsole = brc.readLine()) != null){
                if(connected){
                    sendToServer(inConsole);
                }else{
                        switch (inConsole) {
                            case "/connect":
                                connected = connect();
                                break;
                            default:
                                chatOutput("Error 400 Keine Verbindung zum Server");
                        }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendToServer(String inConsole) {
        try {
            serverOut.write((inConsole).getBytes());
        } catch (IOException e) {
            chatOutput("Error 402 nachricht kann nicht gesendet werden");
            e.printStackTrace();
        }
    }


    private boolean connect() {
        try{
            this.socket = new Socket(serverName, port);
            this.serverOut = socket.getOutputStream();
            InputStream serverIn = socket.getInputStream();
            this.bufferedIn = new BufferedReader(new InputStreamReader(serverIn));
            serverOut.write(("/name " + chatName + "\n").getBytes());
            startMessageReader();
            chatOutput("Erfolgreich verbunden");
            return true;
        } catch (IOException e) {
            chatOutput("Error 407 Verbindung zum Server fehlgeschlagen");
            return false;
        }
    }

    private void chatOutput(String s) {
        String dateiname = "log";
        try {
            FileOutputStream outputToLog = new FileOutputStream(dateiname);
            outputToLog.write(s.getBytes());
        } catch (FileNotFoundException e) {
            System.out.println("Error 430 Datei nicht gefunden");
        } catch (IOException e) {
            System.out.println("Error 431 Log Datei kann nicht geschrieben werden");
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
                if (text.startsWith("/")) {
                    String[] tokens = text.split("\\s");
                    if (tokens.length > 0) {
                        switch (tokens[0]) {
                            case "/kickUser":
                                logout();
                                chatOutput("Error 401 Kicked from Server");
                                break;
                            case "/new":

                                break;
                        }
                    }
                } else {
                    chatOutput(text);
                }
            }
        } catch (Exception ex) {
            chatOutput("Error 405 Verbindung zum Server verloren");
            logout();
        }
    }

    private void logout() {
        try {
            connected = false;
            chatOutput("Verbindung beendet");
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
