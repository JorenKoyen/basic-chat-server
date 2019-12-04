package be.kdg.chat;

public interface IChatServer {
    void register(IChatClient client);
    void unregister(IChatClient client);
    void send(String name, String message);
}
