package be.kdg.chat.server;

import be.kdg.chat.client.ChatClientStub;
import be.kdg.chat.client.IChatClient;
import be.kdg.chat.communication.MessageManager;
import be.kdg.chat.communication.MethodCallMessage;
import be.kdg.chat.communication.NetworkAddress;
import be.kdg.chat.util.Logger;

public class ChatServerSkeleton {
    private final static Logger LOGGER = Logger.getLogger("Controller");
    private final MessageManager messageManager;
    private final MessageManager replyMessageManager;
    private final IChatServer chatServer;

    public ChatServerSkeleton(NetworkAddress address, IChatServer server) {
        messageManager = new MessageManager(address);
        this.replyMessageManager = new MessageManager();
        chatServer = server;
    }

    // == LISTEN ===========================
    public void listen() {
        LOGGER.info("Server started listening on " + this.messageManager.getAddress());

        // infinite loop waiting for requests
        while (true) {
            // wait sync for request
            MethodCallMessage request = messageManager.receiveSync();

            // ack that we have received the request
            ack(request.getOrigin());

            // handle request
            handleRequest(request);
        }
    }

    // == MESSAGE CONTROLLER ===============
    private void handleRequest(MethodCallMessage request) {
        LOGGER.info("Incoming request message | " + request);

        switch (request.getMethod()) {

            case "register":
                this.handleRegister(request);
                break;
            case "unregister":
                this.handleUnregister(request);
                break;
            case "send":
                this.handleSend(request);
                break;
            default:
                LOGGER.error("Received an unknown request");

        }
    }

    private void ack(NetworkAddress to) {
        MethodCallMessage ack = new MethodCallMessage(this.messageManager.getAddress(), "ack");
        ack.addParameter("result", "ok");
        this.messageManager.send(ack, to);
    }

    // == PRIVATE METHODS ==================
    private void handleRegister(MethodCallMessage request) {
        // get reply address a client
        String address = request.getParameter("receive.address");
        int port = Integer.parseInt(request.getParameter("receive.port"));
        NetworkAddress replyAddress = new NetworkAddress(address, port);

        // create client stub
        IChatClient client = new ChatClientStub(replyAddress, request.getOrigin(), this.replyMessageManager );

        // register client with implementation of chat server
        this.chatServer.register(client);
    }
    private void handleUnregister(MethodCallMessage request) {
        // recreate client sub
        IChatClient client = new ChatClientStub(request.getOrigin(), this.replyMessageManager);

        // unregister client with implementation of chat server
        this.chatServer.unregister(client);
    }
    private void handleSend(MethodCallMessage request) {
        // get parameters for implementation
        String name = request.getParameter("name");
        String message = request.getParameter("message");

        // send to chat server implementation
        this.chatServer.send(name, message);
    }
}
