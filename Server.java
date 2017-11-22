import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


class Server{
    private final int port;
    private final String defaultRoom = "default";
    final ArrayList<ChatRoom> listChatRooms = new ArrayList<>();
    private final ChatRoom standardRoom = new ChatRoom(defaultRoom, this);

    Server(int port){
        this.port = port;
        serverOut("Server auf Port: " + port + " erstellt.");
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
            System.out.println(msg);
        }
}
