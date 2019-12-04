package be.kdg.chat;

import be.kdg.chat.communication.MessageManager;
import be.kdg.chat.communication.MethodCallMessage;
import be.kdg.chat.communication.NetworkAddress;
import be.kdg.chat.util.Logger;

public class ChatServerSkeleton {
    private final MessageManager messageManager;
    private final IChatServer chatServer;
    private final static Logger LOGGER = Logger.getLogger("Controller");

    public ChatServerSkeleton(NetworkAddress address) {
        messageManager = new MessageManager(address);
        chatServer = new ChatServer();
    }

    // == PUBLIC METHODS ===================
    public void run() {
        LOGGER.info("Server started listening on " + this.messageManager.getAddress());

        // infinite loop waiting for requests
        while (true) {
            MethodCallMessage request = messageManager.receiveSync();
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

    // == PRIVATE METHODS ==================
    private void handleRegister(MethodCallMessage request) {
        // create new client stub
        IChatClient client = new ChatClientStub(request.getOrigin(), this.messageManager);

        // register client with implementation of chat server
        this.chatServer.register(client);
    }
    private void handleUnregister(MethodCallMessage request) {
        // recreate client sub
        IChatClient client = new ChatClientStub(request.getOrigin(), this.messageManager);

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
