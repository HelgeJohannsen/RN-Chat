import java.io.*;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;


public class ServerC {
    int port = 8881;
    ArrayList<ServerW> listClients = new ArrayList<>();
    ArrayList<ChatRoom> listChatRooms = new ArrayList<>();

    public ServerC() throws IOException {
    }

    public static void main(String[] args) throws IOException {
       ServerC sc = new ServerC();
       sc.run();
    }

    public void run(){
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ServerW worker = new ServerW(clientSocket, this);
                worker.start();
                listClients.add(worker);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    String leseNachricht(java.net.Socket socket) throws IOException {
        BufferedReader bufferedReader =
                new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream()));
        char[] buffer = new char[200];
        int anzahlZeichen = bufferedReader.read(buffer, 0, 200); // blockiert bis Nachricht empfangen
        String nachricht = new String(buffer, 0, anzahlZeichen);
        return nachricht;
    }

    void schreibeNachricht(java.net.Socket socket, String nachricht) throws IOException {
        PrintWriter printWriter =
                new PrintWriter(
                        new OutputStreamWriter(
                                socket.getOutputStream()));
        printWriter.print(nachricht);
        printWriter.flush();
    }
}
