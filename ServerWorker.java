import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.regex.Matcher;

public class ServerWorker extends Thread {

    private final Socket clientSocket;
    private final Server server;
    private final int maxMsgLength = 1000;
    private ChatRoom chatRoom = null;
    private String nickName;
    private OutputStream outputStream;

    private HashMap<Integer,String> responseCodes;

    ServerWorker(Socket clientSocket,String nickName, ChatRoom chatRoom, Server server) {
        this.server = server;
        this.clientSocket = clientSocket;
        this.chatRoom = chatRoom;
        this.nickName = nickName;
        responseCodes = new HashMap<Integer,String>();
        responseCodes.put(200, "/200 ok");
        responseCodes.put(201, "/201 GoodyBye");
        responseCodes.put(300, "/300 Missing Argument");
        responseCodes.put(301, "/301 Name already taken");
    }

    @Override
    public void run() {
        try {
            handleClientSocket();
        } catch (IOException e) {
            server.serverOut(e.getMessage());
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
            //server.serverOut("Nachricht kann nichtgesendet werden" + e.getMessage());
        }
    }

    private void handleClientSocket() throws IOException {
        InputStream inputStream = clientSocket.getInputStream();
        Reader reader = new InputStreamReader(inputStream);
        this.outputStream = clientSocket.getOutputStream();
        sendMsgToClient("Willkommen auf dem Server, Befehle bekommen sie mit /help");
        chatRoom.addClient(this);
        String text;
        int msgLength = 0;
        char[] cbuf = new char[maxMsgLength];
        boolean hasLeft = false;

        while (!hasLeft && (msgLength = reader.read(cbuf, 0, maxMsgLength)) != -1) {
            text = String.copyValueOf(cbuf, 0, msgLength);
            cbuf = new char[maxMsgLength];
            //if(nickName == ""){
//                sendMsgToClient("Bitte geben sie einen Namen ein mit /name Beispielname\n");
//            }
//            if (chatRoom == null){
//                sendMsgToClient("Bitte wählen sie einen Chatroom mit /switch RoomName\n");
            text = Matcher.quoteReplacement(text).replaceFirst("\n", "");
            text = Matcher.quoteReplacement(text).replaceFirst("\r", "");
//            }
            if (text.startsWith("/")) {
                String[] tokens = text.split("\\s");
                if (tokens.length > 0) {
                    switch (tokens[0]) {
                        case "/leave":
                            logOff();
                            hasLeft = true;
                            break;
                        case "/new":
                            if(tokens.length > 1)
                                createChat(tokens[1]);
                            else
                                sendMsgToClient(responseCodes.get(300));
                            break;
                        case "/listChats":
                            listChats();
                            break;
                        case "/switch":
                            if(tokens.length > 1)
                                switchChat(tokens[1]);
                            else
                                sendMsgToClient(responseCodes.get(300));
                            break;
                        case "/listUser":
                            listUser();
                            break;
                        case "/list":
                            listUserAll();
                            break;
                        case "/name":
                            if(tokens.length > 1)
                                setNick(tokens[1]);
                            else
                                sendMsgToClient(responseCodes.get(300));
                            break;
                        case "/help":
                            listCommands();
                            break;
                        default:
                            listCommands();
                    }
                }
            } else if(!text.isEmpty()) {
                server.serverOut(chatRoom + " " + nickName + ": " + text);
                String msg = nickName + ": " + text;
                chatRoom.msgToRoom(msg);
            }

        }
        //Verbindung nicht ordentlich beendet
        if(!hasLeft)
            throw new IOException("Verbindung zu " + clientSocket + " verloren!");
    }

    private void listCommands() {
        String commands = "Befehle: /new /name /switch /list /listChats /listUser /leave /help";
        sendMsgToClient(commands + "\n");
    }

    private void createChat(String token) {
        if(server.addChatRoom(token))
        {
            server.serverOut("Chatroom \"" + token + "\" wurde erstellt");
            sendMsgToClient("Chatroom \"" + token + "\" wurde erstellt");
        }
        else
        {
            sendMsgToClient(responseCodes.get(301));
        }
    }

    private void logOff() {
        try {
            sendMsgToClient(responseCodes.get(201));
            outputStream.close();
            chatRoom.deleteClient(this);
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
        for (ServerWorker s : chatRoom.getClientList()) {
            msg.append(s).append(" ");
        }
        sendMsgToClient(msg.toString());
    }

    private void listUserAll() {
        String msg = "Server: \n";
        for (ChatRoom chat : server.getChatRoomList()) {
            msg = msg + "User im Chat: \"" + chat + "\":";
            for (ServerWorker s : chat.getClientList()) {
                msg = msg + " " + s;
            }
            msg = msg + "\n";
        }
        sendMsgToClient(msg);
    }

    private void listChats() {
        String msg = "Alle Chats des Servers\n";
        for (ChatRoom c : server.getChatRoomList()) {
            msg = msg + c.toString() + " ";
        }
        sendMsgToClient(msg);
    }

    private void setNick(String newName) {
        String text = "";
        if(server.checkNickname(newName))
        {
            text = nickName + " hat sich umbenannt in: " + newName;
            nickName = newName;
        }
        else
        {
            text = responseCodes.get(301);
        }
        chatRoom.msgToRoom(text);
        server.serverOut(text);
    }

    public String toString() {
        return nickName;
    }
}