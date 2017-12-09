import java.io.IOException;
import java.util.ArrayList;

public class ChatRoom {
    private ArrayList<ServerWorker> clients = new ArrayList<>();
    private final String roomName;
    private final Server server;

    ChatRoom(String name, Server server){
        roomName = name;
        this.server = server;
    }

    synchronized void addClient(ServerWorker client) throws IOException {
        clients.add(client);
        client.sendMsgToClient("Willkommen im Chat: " + roomName);
        String msg = client.getNickName() + " hat den Raum betreten";
        server.serverOut(client.getNickName() + " hat den Raum betreten");
        msgToRoom(msg);
    }
    synchronized void deleteClient(ServerWorker client) throws IOException {
        clients.remove(client);
        String msg = client.getNickName() + " hat den Raum verlassen";
        msgToRoom(msg);
    }
    synchronized void msgToRoom(String msg) {
        for (ServerWorker c:clients){
            c.sendMsgToClient(msg);
        }
    }

    synchronized public ArrayList<ServerWorker> getClientList()
    {
        return clients;
    }

    synchronized public String getName()
    {
        return roomName;
    }

    public String toString(){
        return roomName;
    }

}
