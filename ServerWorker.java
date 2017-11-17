import java.io.*;
import java.net.Socket;

public class ServerWorker extends Thread implements Runnable{

    private final Socket clientSocket;
    private ChatRoom chatRoom;
    private final Server server;
    private String nickName = "notSet";


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
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    private void createNickName() throws IOException {
        OutputStream outputStream = clientSocket.getOutputStream();
        InputStream inputStream = clientSocket.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        outputStream.write(("Bitte geben sie ihren Nicknamen ein:").getBytes());
        try {
            nickName = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    protected String getNickName(){
        return nickName;
    }


    protected void sendMsgToClient(String msg) throws IOException {
        OutputStream outputStream = clientSocket.getOutputStream();
        outputStream.write(msg.getBytes());
    }

    private void handleClientSocket() throws InterruptedException, IOException {
        InputStream inputStream = clientSocket.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String text;
        // Chat verlassen und vom server abmelden
        while((text = br.readLine())!= null){
                System.out.println(text);
                if(text.equals("\n")){
                    break;
                }
                if(text.charAt(0) == '/'){
                    String[] cmd = text.split("\\s");
                    switch(cmd[0]){
                        case "/leave":
                            //TODO schliesst mit Fehlermeldung
                            chatRoom.deleteClient(this);
                            clientSocket.close();
                            break;
                        case "/new":
                            server.listChatRooms.add(new ChatRoom(cmd[1]));
                            break;
                        case "/list":
                            for(ChatRoom c : server.listChatRooms){sendMsgToClient(c.toString());}
                            break;
                        case "/switch":
                            switchChat(cmd[1]);
                            break;
                        case "/listUser":
                            listUser();
                            break;
                        case "/name":
                            setNick(cmd[1]);
                            break;

                    }
                }else{
                    String msg = nickName + ": " + text + "\n";
                    chatRoom.msgToRoom(msg);
                    System.out.println(msg);}
        }
        System.out.println("AAAAAAAAA");
        clientSocket.close();
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
        System.out.print(nickName + " hat sich umbenannt in: " + newName);
        nickName = newName;
    }
     public String toString(){
        return nickName;
    }
}
