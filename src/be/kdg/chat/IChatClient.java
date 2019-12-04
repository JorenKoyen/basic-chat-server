package be.kdg.chat;

public interface IChatClient {
    void receive(String message);
    boolean equals(IChatClient client);
}
