import java.io.*;
import java.net.Socket;

public class ServerWorker extends Thread {

    private final Socket clientSocket;
    private final Server server;
    boolean running = true;
    private ChatRoom chatRoom = null;
    private String nickName = "noname";
    private OutputStream outputStream;

    ServerWorker(Socket clientSocket, ChatRoom chatRoom, Server server) {
        this.server = server;
        this.clientSocket = clientSocket;
        this.chatRoom = chatRoom;
    }

    @Override
    public void run() {
        try {
            handleClientSocket();
        } catch (IOException e) {
            server.serverOut("Verbindung zu " + clientSocket + " verloren");
            logOff();
        }
    }

    String getNickName() {
        return nickName;
    }


    void sendMsgToClient(String msg) {
        try {
            outputStream.write((msg + "\n").getBytes());
        } catch (IOException e) {
            server.serverOut("Nachricht kann nichtgesendet werden" + e.getMessage());
        }
    }

    private void handleClientSocket() throws IOException {
        InputStream inputStream = clientSocket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        this.outputStream = clientSocket.getOutputStream();
        sendMsgToClient("Willkommen auf dem Server, Befehle bekommen sie mit /help");
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
                        case "/list":
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
                server.serverOut(chatRoom + " " + nickName + ": " + text);
                String msg = nickName + ": " + text;
                chatRoom.msgToRoom(msg);
            }
        }
        logOff();
    }

    private void listCommands() {
        String commands = "/new /name /switch /list /listChats /listUser /leave";
        sendMsgToClient(commands + "\n");
    }

    private void createChat(String token) {
        try {
            server.listChatRooms.add(new ChatRoom(token, server));
            server.serverOut("Chatroom \"" + token + "\" wurde erstellt");
            sendMsgToClient("Chatroom \"" + token + "\" wurde erstellt");
        } catch (Exception e) {
            System.out.println("Chatroom \"" + token + "\" konnte nicht erstellt werden");
            server.serverOut("Chatroom \"" + token + "\" bereits vorhanden");
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
            chatRoom.deleteClient(this);
            chatRoom = server.getChatRoom(newChat);
            chatRoom.addClient(this);
            sendMsgToClient("ChatRoom erfolgreich gewechselt");
            server.serverOut(nickName + "hat zu Chat " + newChat + "gewechselt");
        } catch (IOException | NullPointerException e) {
            sendMsgToClient("ChatRoom nicht vorhanden");
        }
    }

    private void listUser() {
        StringBuilder msg = new StringBuilder("Alle User im Chat \"" + chatRoom + "\"\n");
        for (ServerWorker s : chatRoom.clients) {
            msg.append(s).append(" ");
        }
        sendMsgToClient(msg.toString());
    }

    private void listUserAll() {
        String msg = "Server: \n";
        for (ChatRoom chat : server.listChatRooms) {
            msg = msg + "User im Chat: \"" + chat + "\":";
            for (ServerWorker s : chat.clients) {
                msg = msg + " " + s;
            }
            msg = msg + "\n";
        }
        sendMsgToClient(msg);
    }

    private void listChats() {
        String msg = "Alle Chats des Servers\n";
        for (ChatRoom c : server.listChatRooms) {
            msg = msg + c.toString() + " ";
        }
        sendMsgToClient(msg);
    }

    private void setNick(String newName) {
        String text = nickName + " hat sich umbenannt in: " + newName;
        nickName = newName;
        chatRoom.msgToRoom(text);
        server.serverOut(text);
    }

    public String toString() {
        return nickName;
    }
}
