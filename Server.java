import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class Server{
    int port;
    String defaultRoom = "default";
    ArrayList<ChatRoom> listChatRooms = new ArrayList<>();
    ChatRoom standardRoom = new ChatRoom(defaultRoom);

    Server(int port){
        this.port = port;
        System.out.println("Server mit Port: " + port + " erstellt.\n");
    }

    void run() {
        listChatRooms.add(standardRoom);
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true){
                Socket clientSocket = serverSocket.accept();
                System.out.println(clientSocket);
            ServerWorker worker = new ServerWorker(clientSocket, standardRoom, this);
            worker.start();
        }
            }catch (IOException e) {
            e.printStackTrace();
        }
        }

     ChatRoom getChatRoom(String chatRoomName) throws NullPointerException{
        for(ChatRoom c: listChatRooms){
           if(c.toString().equals(chatRoomName)){
               return c;
           }
        }
        return null;
}
        void serverOut(String msg){
            System.out.println(msg + "\n");
        }
}
