import java.io.*;
import java.net.*;

public class ServerW extends Thread{
    private final Socket clientSocket;
    private final ServerC server;

    public ServerW(Socket clientSocket, ServerC server){
        this.clientSocket = clientSocket;
        this.server = server;
    }
    @Override
    public void run(){
        try {
            handleClientSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClientSocket() throws IOException {
        InputStream inputStream = clientSocket.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String text;
        // Chat verlassen und vom server abmelden
        toClient("AA");
        while((text = br.readLine())!= null){
            System.out.println(text);
        }
        }

    private void readInput() {
        BufferedReader bufferedReader =
                null;
        try {
            bufferedReader = new BufferedReader(
                    new InputStreamReader(
                            clientSocket.getInputStream()));
            char[] buffer = new char[200];
            int anzahlZeichen = 0; // blockiert bis Nachricht empfangen
            anzahlZeichen = bufferedReader.read(buffer, 0, 100);
            String nachricht = new String(buffer, 0, anzahlZeichen);
            System.out.println(nachricht);
        //    toClient("Server ");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    protected void toClient(String msg) throws IOException {
        PrintWriter printWriter =
                new PrintWriter(
                        new OutputStreamWriter(
                                clientSocket.getOutputStream()));
        printWriter.print(msg);
        printWriter.flush();
    }
}
