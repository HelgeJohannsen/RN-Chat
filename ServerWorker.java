import java.io.*;
import java.net.Socket;

public class ServerWorker extends Thread {

    private final Socket clientSocket;
    private ChatRoom chatRoom;
    private final Server server;
    private String nickName = "notSet";
    OutputStream outputStream;

    public ServerWorker(Socket clientSocket, ChatRoom chatRoom, Server server) throws IOException {
        this.server = server;
        this.clientSocket = clientSocket;
        this.chatRoom = chatRoom;

    }

    @Override
    public void run(){
        try {
            handleClientSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected String getNickName(){
        return nickName;
    }


    protected void sendMsgToClient(String msg) throws IOException {
        outputStream.write(msg.getBytes());
    }

    private void handleClientSocket() throws IOException {
        InputStream inputStream = clientSocket.getInputStream();
        this.outputStream = clientSocket.getOutputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String text;
            while((text = reader.readLine())!= null) {
                String[] tokens = text.split(" ");
                if (tokens.length > 0) {
                    switch (tokens[0]) {
                        case "leave":
                            logOff();
                            break;
                        case "new":
                            server.listChatRooms.add(new ChatRoom(tokens[1]));
                            break;
                        case "list":
                            for (ChatRoom c : server.listChatRooms) {
                                sendMsgToClient(c.toString());
                            }
                            break;
                        case "switch":
                            switchChat(tokens[1]);
                            break;
                        case "listUser":
                            listUser();
                            break;
                        case "name":
                            setNick(tokens[1]);
                            break;
                    }
                }
                String msg = "msg " + nickName + text + "\n";
                chatRoom.msgToRoom(msg);
            }
        System.out.println("Socket closed");
        clientSocket.close();
    }

    private void logOff() {
        try {
            outputStream.close();
            clientSocket.close();
            chatRoom.deleteClient(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void switchChat(String newChat) throws IOException {
        if((chatRoom = server.getChatRoom(newChat)) == null){
            sendMsgToClient("ChatRoom nicht vorhanden");
        }else{
            chatRoom.addClient(this);
        }
    }
    protected void listUser(){
        try {
            sendMsgToClient("Alle User im Chat " + chatRoom + " ");
            for(ServerWorker s : chatRoom.clients){
                sendMsgToClient(s + " ");
            }
            sendMsgToClient("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    protected void setNick(String newName){
        try {
            chatRoom.msgToRoom(nickName + " hat sich umbenannt in: " + newName + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        nickName = newName;
    }
     public String toString(){
        return nickName;
    }
}
