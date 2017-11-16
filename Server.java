import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args){
        ServerSocket ss = null;
        try {
            ss = new ServerSocket(1201);
            Socket s = ss.accept();

            DataInputStream din = new DataInputStream(s.getInputStream());
            DataOutputStream dout = new DataOutputStream(s.getOutputStream());

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            String msgin = "", msgout = "";
            while(!msgin.equals("end")){
                msgin = din.readUTF();
                System.out.println(msgin);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
