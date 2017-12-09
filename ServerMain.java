public class ServerMain {
    public static void main(String[] args) {
        int port = 8812;
        Server server = new Server(port);
        server.run();
    }
}
