package be.kdg.chat.client;

public interface IChatClient {
    void receive(String message);
    String getName();
}
