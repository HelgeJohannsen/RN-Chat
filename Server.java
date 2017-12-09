import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


class Server{
    private final int port;
    private final String defaultRoom = "default";
    private ArrayList<ChatRoom> listChatRooms = new ArrayList<>();
    private ChatRoom standardRoom = new ChatRoom(defaultRoom, this);
    private final String nickName = "noname";

    Server(int port){
        this.port = port;
        serverOut("Server auf Port: " + port + " erstellt.");
    }

    void run() {
        listChatRooms.add(standardRoom);
        int nickIndex = 0;
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true){
                Socket clientSocket = serverSocket.accept();
                System.out.println(clientSocket);
                ServerWorker worker = new ServerWorker(clientSocket,nickName + nickIndex, standardRoom, this);
                nickIndex++;
                worker.start();
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    synchronized ChatRoom getChatRoom(String chatRoomName) throws NullPointerException{
        for(ChatRoom c: listChatRooms){
            if(c.toString().equals(chatRoomName)){
                return c;
            }
        }
        return null;
    }
    synchronized public boolean checkNickname(String nickName)
    {
        for(ChatRoom c: listChatRooms){
            for(ServerWorker client: c.getClientList())
            {
                if( client.getNickName().equals(nickName))
                    return false;
            }
        }
        return true;

    }

    synchronized public boolean checkChatName(String chatName)
    {
        for(ChatRoom c: listChatRooms){
            if(c.getName().equals(chatName))
                return false;
        }
        return true;

    }

    synchronized public ArrayList<ChatRoom> getChatRoomList()
    {
        return listChatRooms;
    }

    synchronized public boolean addChatRoom(String roomnName)
    {
        try {
            if(checkChatName(roomnName))
            {
                listChatRooms.add(new ChatRoom(roomnName, this));
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    synchronized void serverOut(String msg){
        System.out.println(msg);
    }
}
