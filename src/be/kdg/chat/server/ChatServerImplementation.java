package be.kdg.chat.server;

import be.kdg.chat.client.IChatClient;
import be.kdg.chat.server.IChatServer;
import be.kdg.chat.util.Logger;

import java.util.ArrayList;
import java.util.List;

public class ChatServerImplementation implements IChatServer {
    private static final Logger LOGGER = Logger.getLogger("ChatServer");
    private List<IChatClient> clients;

    public ChatServerImplementation() {
        this.clients = new ArrayList<>();
    }


    // == PUBLIC METHODS ===================
    @Override
    public void register(IChatClient client) {
        // try to check if client is double registering
        if (clientExists(client)) {
            LOGGER.error("Client already exists");
            return;
        }

        // register client to server
        this.clients.add(client);
        String name = client.getName();
        LOGGER.info("Client " + name + " has been registered" );

        // notify users
        this.send("SERVER", name + " has connected to the server from " + client.toString());

    }

    @Override
    public void unregister(IChatClient client) {
        // check if client was registered
        if (!clientExists(client)) {
            LOGGER.error("Unable to find client " + client.toString());
        }

        // unregister client from server
        IChatClient registeredClient = this.clients.stream()
                .filter(c -> c.equals(client))
                .findFirst().orElseThrow();

        // remove client from server
        this.clients.remove(registeredClient);
        LOGGER.info("Client " + client.toString() + " has ben unregistered");

        // notify users
        this.send("SERVER", "client disconnected from server from address " + client);

    }

    @Override
    public void send(String name, String message) {
        String transmittedMessage = String.format("[%s] - %s", name, message);

        for (IChatClient client : this.clients) {

            // relay message to all clients
            client.receive(transmittedMessage);

            // log transmission
            LOGGER.info(String.format("Sending \"%s\" to client %s", transmittedMessage, client.toString()));

        }

    }

    // == PRIVATE METHODS ==================
    private boolean clientExists(IChatClient client) {

        for (IChatClient c : this.clients) {
            if (c.equals(client)) return true;
        }

        return false;
    }


}
