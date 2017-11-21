import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class ChatClient {
    final String serverName;
    final int port;
    Socket socket;
    private OutputStream serverOut;
    private InputStream serverIn;
    private BufferedReader bufferedIn;
    String chatName = "Peter2000";
    BufferedReader brc = new BufferedReader(new InputStreamReader(System.in));

    public ChatClient(String serverName, int port) {
        this.serverName = serverName;
        this.port = port;
    }

    public static void main(String[] args) throws IOException {
        ChatClient client = new ChatClient("localhost", 8811);

        if(!client.connect()){
            System.err.println("Connect failed.\n");
        } else {
            System.out.printf("Connect sucessful\n");
                client.login();

        }

    }

    public void read(){
        String inConsole;
        try {
            while((inConsole = brc.readLine()) != null){
                writeToServer(inConsole);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void login() throws IOException {
        serverOut.write(("name " + chatName + "\n").getBytes());
        startMessageReader();
        read();
    }

    private void writeToServer(String msg){
        try {
            serverOut.write((msg+"\n").getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean connect() {
        try{
            this.socket = new Socket(serverName, port);
            this.serverOut = socket.getOutputStream();
            this.serverIn = socket.getInputStream();
            this.bufferedIn = new BufferedReader(new InputStreamReader(serverIn));
            return true;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
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
            String line;
            while ((line = bufferedIn.readLine()) != null) {
                String[] tokens = line.split(" ", 3);
                String cmd = tokens[0];
                String user = tokens[1];
                String text = tokens[2];
                if (tokens.length > 2) {
                    if ("system".equalsIgnoreCase(cmd)) {
                        System.out.println(text);
                    } else if ("offline".equalsIgnoreCase(cmd)) {

                    } else if ("msg".equalsIgnoreCase(cmd)) {
                        if(chatName!= tokens[1]) System.out.println(user + ": " + text);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
