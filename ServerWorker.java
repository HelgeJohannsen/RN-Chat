import java.io.*;
import java.net.Socket;

public class ServerWorker extends Thread {

    private final Socket clientSocket;
    private ChatRoom chatRoom;
    private final Server server;
    private String nickName = "notSet";
    OutputStream outputStream;
    boolean running = true;

    public ServerWorker(Socket clientSocket, ChatRoom chatRoom, Server server){
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


    protected void sendMsgToClient(String msg) {
        try {
            outputStream.write(msg.getBytes());
        } catch (IOException e) {
            System.out.println("AAAA" + e.getMessage());
        }
    }

    private void handleClientSocket() throws IOException{
        InputStream inputStream = clientSocket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        this.outputStream = clientSocket.getOutputStream();
        chatRoom.addClient(this);
        String text;
            while((text = reader.readLine())!= null) {
                if (text.startsWith("/")) {
                    String[] tokens = text.split("\\s");
                    if (tokens.length > 0) {
                        switch (tokens[0]) {
                            case "/leave":
                                logOff();
                                break;
                            case "/new":
                                server.listChatRooms.add(new ChatRoom(tokens[1]));
                                break;
                            case "/list":
                                for (ChatRoom c : server.listChatRooms) {
                                    sendMsgToClient(c.toString());
                                }
                                break;
                            case "/switch":
                                switchChat(tokens[1]);
                                break;
                            case "/listUser":
                                listUser();
                                break;
                            case "/name":
                                setNick(tokens[1]);
                                break;
                        }
                    }
                }else {
                    System.out.println(chatRoom + " " + nickName + ": " + text);
                    String msg = "#" + nickName + " " + text + "\n";
                    chatRoom.msgToRoom(msg);
                }
            }

        clientSocket.close();
    }

    private void logOff() throws IOException {
//            outputStream.close();
//            clientSocket.close();
//            chatRoom.deleteClient(this);
        System.out.println("Socket closed Log Off");
        running = false;
        clientSocket.close();
    }

    protected void switchChat(String newChat) throws IOException {
        if((chatRoom = server.getChatRoom(newChat)) == null){
            sendMsgToClient("ChatRoom nicht vorhanden");
        }else{
            chatRoom.addClient(this);
        }
    }
    protected void listUser() throws IOException {
        sendMsgToClient("Alle User im Chat " + chatRoom + " ");
        for(ServerWorker s : chatRoom.clients){
            sendMsgToClient(s + " ");
        }
        sendMsgToClient("\n");
    }
    protected void setNick(String newName) {
        try {
            chatRoom.msgToRoom(nickName + " hat sich umbenannt in: " + newName + "\n");
            nickName = newName;
        } catch (IOException e) {
            sendMsgToClient("Name schon vorhanden");
        }
        nickName = newName;
    }
     public String toString(){
        return nickName;
    }
}
