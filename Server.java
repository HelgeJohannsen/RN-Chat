import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;


public class Server {
    int port = 8811;
    String defaultRoom = "WelcomeRoom";
    ArrayList<ServerWorker> listClients = new ArrayList<>();
    ArrayList<ChatRoom> listChatRooms = new ArrayList<>();
    ChatRoom standardRoom = new ChatRoom(defaultRoom);

    Server(int port){
        this.port = port;
    }

    public void run() {
        listChatRooms.add(standardRoom);
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
               Socket clientSocket = serverSocket.accept();
               ServerWorker worker = new ServerWorker(clientSocket, standardRoom, this);
               worker.start();
               listClients.add(worker);
               standardRoom.addClient(worker);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
     protected ChatRoom getChatRoom(String chatRoomName){
        for(ChatRoom c: listChatRooms){
           if(c.toString().equals(chatRoomName)){
               return c;
           }
        }
        return null;
}
}
