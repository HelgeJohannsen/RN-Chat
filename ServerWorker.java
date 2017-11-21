import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class ServerWorker extends Thread {

    private final Socket clientSocket;
    private ChatRoom chatRoom;
    private final Server server;
    private String nickName;
    OutputStream outputStream;
    boolean running = true;

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
        chatRoom.addClient(this);
        String text;
        while ((text = reader.readLine()) != null) {
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

    protected void switchChat(String newChat) {
        try {
                chatRoom = server.getChatRoom(newChat);
                chatRoom.addClient(this);
            sendMsgToClient("ChatRoom erfolgreich gewechselt\n");
        } catch (IOException | NullPointerException e) {
            sendMsgToClient("ChatRoom nicht vorhanden\n");
        }
    }

    protected void listUser() throws IOException {
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
