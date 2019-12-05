package be.kdg.chat.server;

import be.kdg.chat.client.IChatClient;

public interface IChatServer {
    void register(IChatClient client);
    void unregister(IChatClient client);
    void send(String name, String message);
}
