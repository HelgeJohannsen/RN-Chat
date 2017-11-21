import java.io.*;
import java.net.Socket;
import java.util.List;

public class ServerWorker extends Thread {

    private final Socket clientSocket;
    private ChatRoom chatRoom = null;
    private final Server server;
    private String nickName = "noname";
    private OutputStream outputStream;
    boolean running = true;
    private String commands = "/new /name /switch /listUSerAll /listChats /listUser /leave";

    public ServerWorker(Socket clientSocket, ChatRoom chatRoom, Server server) {
        this.server = server;
        this.clientSocket = clientSocket;
        this.chatRoom = chatRoom;
    }

    @Override
    public void run() {
        try {
            handleClientSocket();
        } catch (IOException e) {
            System.out.println("Verbindung zu " + clientSocket + " verloren");
            logOff();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected String getNickName() {
        return nickName;
    }


    protected void sendMsgToClient(String msg) {
        try {
            outputStream.write(msg.getBytes());
        } catch (IOException e) {
            System.out.println("Nachricht kann nichtgesendet werden" + e.getMessage());
        }
    }

    private void handleClientSocket() throws IOException, InterruptedException {
        InputStream inputStream = clientSocket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        this.outputStream = clientSocket.getOutputStream();
        sendMsgToClient("Willkommen auf dem Server, Befehle bekommen sie mit /help\n");
        chatRoom.addClient(this);
        String text;
        while ((text = reader.readLine()) != null) {
//            if(nickName == ""){
//                sendMsgToClient("Bitte geben sie einen Namen ein mit /name Beispielname\n");
//            }
//            if (chatRoom == null){
//                sendMsgToClient("Bitte wählen sie einen Chatroom mit /switch RoomName\n");
//            }
            if (text.startsWith("/")) {
                String[] tokens = text.split("\\s");
                if (tokens.length > 0) {
                    switch (tokens[0]) {
                        case "/leave":
                            logOff();
                            break;
                        case "/new":
                            createChat(tokens[1]);
                            break;
                        case "/listChats":
                            listChats();
                            break;
                        case "/switch":
                            switchChat(tokens[1]);
                            break;
                        case "/listUser":
                            listUser();
                            break;
                        case "/listUserAll":
                            listUserAll();
                            break;
                        case "/name":
                            setNick(tokens[1]);
                            break;
                        case "/help":
                            listCommands();
                            break;
                         default:
                             listCommands();
                    }
                }
            } else {
                System.out.println(chatRoom + " " + nickName + ": " + text);
                String msg = "#" + nickName + " " + text + "\n";
                chatRoom.msgToRoom(msg);
            }
        }
        logOff();
    }

    private void listCommands() {
        sendMsgToClient(commands + "\n");
    }

    private void createChat(String token) {
        try {
            server.listChatRooms.add(new ChatRoom(token));
            System.out.println("Chatroom \"" + token + "\" wurde erstellt");
            sendMsgToClient("Chatroom \"" + token + "\" wurde erstellt\n");
        } catch (Exception e) {
            System.out.println("Chatroom \"" + token + "\" konnte nicht erstellt werden\n");
            sendMsgToClient("Chatroom \"" + token + "\" bereits vorhanden\n");
        }
    }

    private void logOff() {
        try {
            outputStream.close();
            chatRoom.deleteClient(this);
            running = false;
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void switchChat(String newChat) {
        try {
                chatRoom = server.getChatRoom(newChat);
                chatRoom.addClient(this);
            sendMsgToClient("ChatRoom erfolgreich gewechselt\n");
        } catch (IOException | NullPointerException e) {
            sendMsgToClient("ChatRoom nicht vorhanden\n");
        }
    }

    private void listUser() throws IOException {
        sendMsgToClient("Alle User im Chat \"" + chatRoom + "\"\n");
        for (ServerWorker s : chatRoom.clients) {
            sendMsgToClient(s + " ");
        }
        sendMsgToClient("\n");
    }
    protected void listUserAll() throws IOException {
        sendMsgToClient("Alle auf dem Server \"" + "\"\n");
        for (ChatRoom chat : server.listChatRooms) {
            sendMsgToClient("User im Chat: \"" + chat );
            for (ServerWorker s : chat.clients) {
                sendMsgToClient(" " + s);
            }
            sendMsgToClient("\n");
        }
    }

    protected void listChats(){
        sendMsgToClient("Alle Chats des Servers\n");
        for (ChatRoom c : server.listChatRooms) {
            sendMsgToClient(c.toString() + " ");
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

    public String toString() {
        return nickName;
    }
}
