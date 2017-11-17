import java.io.*;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws IOException {
        Socket s = null;
        String nickName = "";

            s = new Socket("192.168.2.152",8811);
            DataInputStream din = new DataInputStream(s.getInputStream());
            DataOutputStream dout = new DataOutputStream(s.getOutputStream());

            BufferedReader brConsole = new BufferedReader(new InputStreamReader(System.in));
            BufferedReader brServer = new BufferedReader(new InputStreamReader(din));
            String textFromServer, msgToServer = "/name Ute";

            // Chat verlassen und vom server abmelden
        while(true) {
            while ((textFromServer = brServer.readLine()) != null) {


                System.out.println(textFromServer);

            }
            while((msgToServer = brConsole.readLine()) != null){
                dout.write(msgToServer.getBytes());
            }
        }

    }
}
