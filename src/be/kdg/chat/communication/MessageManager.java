package be.kdg.chat.communication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class MessageManager {
    private ServerSocket socket;
    private NetworkAddress address;

    public MessageManager() {
        try {
            // creates a server socket with a dynamic allocated port
            this.socket = new ServerSocket(0);

            // setup network address wrapper
            InetAddress localhost = InetAddress.getLocalHost();
            this.address = new NetworkAddress(localhost.getHostAddress(), socket.getLocalPort());

        } catch (IOException e) {
            System.err.println("Failed to create server socket");
            e.printStackTrace();
            System.exit(1);
        }
    }
    public MessageManager(NetworkAddress address) {
        this.address = address;
        try {

            // create a server socket with the given address
            this.socket = new ServerSocket(this.address.getPort());

        } catch (IOException e) {
            System.err.println("Failed to create server socket");
            e.printStackTrace();
            System.exit(1);
        }
    }

    // == GETTER ===========================
    public NetworkAddress getAddress() { return this.address; }

    // == METHODS ==========================
    public MethodCallMessage receiveSync() {
        MethodCallMessage messageCall = null;

        try {
            // listen for incoming connections to be made and accept it
            Socket client = this.socket.accept();
            InputStream inStream = client.getInputStream();

            // read message call
            messageCall = MessageStreamHandler.read(inStream);

            // close connection
            client.close();


        } catch (IOException e) {
            System.err.println("Failed to receive incoming message");
            e.printStackTrace();
        }

        // finally return messageCall (if any)
        return messageCall;
    }

    public void send(MethodCallMessage message, NetworkAddress to) {
        try {

            // create socket for destination
            Socket destination = new Socket(to.getAddress(), to.getPort());
            OutputStream out = destination.getOutputStream();

            // write message to stream
            MessageStreamHandler.write(message, out);

            // close connection
            destination.close();

        } catch (IOException e) {
            System.err.println("Failed to send message to destination");
            e.printStackTrace();
        }
    }
}
