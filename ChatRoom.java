import java.io.IOException;
import java.util.ArrayList;

public class ChatRoom {
    ArrayList<ServerWorker> clients = new ArrayList<>();
    private final String roomName;

    ChatRoom(String name){
        roomName = name;
    }

    void addClient(ServerWorker client) throws IOException {
        clients.add(client);
        client.sendMsgToClient("Willkommen im Chat: " + roomName + "\n");
        String msg = client.getNickName() + " hat den Raum betreten\n";
        System.out.println(client.getNickName() + " hat den Raum betreten");
        msgToRoom(msg);
    }
    void deleteClient(ServerWorker client) throws IOException {
        clients.remove(client);
        String msg = client.getNickName() + " hat den Raum verlassen \n";
        msgToRoom(msg);
    }
    void msgToRoom(String msg) throws IOException {
        for (ServerWorker c:clients){
            c.sendMsgToClient(msg);
        }
    }

    public String toString(){
        return roomName;
    }

}
