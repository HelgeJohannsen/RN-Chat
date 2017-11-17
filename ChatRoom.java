import java.io.IOException;
import java.util.ArrayList;

public class ChatRoom {
    ArrayList<ServerWorker> clients = new ArrayList<>();
    final String roomName;

    protected ChatRoom(String name){
        roomName = name;
    }

    public  void addClient(ServerWorker client) throws IOException {
        clients.add(client);
        String msg = client.getNickName() + " hat den Raum betreten\n";
        System.out.println(client.getNickName() + " hat den Raum betreten\n");
        msgToRoom(msg);
    }
    public  void deleteClient(ServerWorker client) throws IOException {
        clients.remove(client);
        String msg = client.getNickName() + " hat den Raum betreten\n";
        msgToRoom(msg);
    }
    protected void msgToRoom(String msg) throws IOException {
        for (ServerWorker c:clients){
            c.sendMsgToClient(msg);
        }
    }

    public String toString(){
        return roomName;
    }

}
