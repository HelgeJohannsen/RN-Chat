import java.io.IOException;
import java.util.ArrayList;

public class ChatRoom {
    final ArrayList<ServerWorker> clients = new ArrayList<>();
    private final String roomName;
    private final Server server;

    ChatRoom(String name, Server server){
        roomName = name;
        this.server = server;
    }

    void addClient(ServerWorker client) throws IOException {
        clients.add(client);
        client.sendMsgToClient("Willkommen im Chat: " + roomName);
        String msg = client.getNickName() + " hat den Raum betreten";
        server.serverOut(client.getNickName() + " hat den Raum betreten");
        msgToRoom(msg);
    }
    void deleteClient(ServerWorker client) throws IOException {
        clients.remove(client);
        String msg = client.getNickName() + " hat den Raum verlassen";
        msgToRoom(msg);
    }
    void msgToRoom(String msg) {
        for (ServerWorker c:clients){
            c.sendMsgToClient(msg);
        }
    }

    public String toString(){
        return roomName;
    }

}
