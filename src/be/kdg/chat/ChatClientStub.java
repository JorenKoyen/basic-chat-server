package be.kdg.chat;

import be.kdg.chat.communication.MessageManager;
import be.kdg.chat.communication.MethodCallMessage;
import be.kdg.chat.communication.NetworkAddress;

public class ChatClientStub implements IChatClient {
    private final NetworkAddress remoteAddress;
    private final MessageManager messageManager;

    public ChatClientStub(NetworkAddress remoteAddress, MessageManager messageManager) {
        this.remoteAddress = remoteAddress;
        this.messageManager = messageManager;
    }




    // == GETTERS ==========================
    public NetworkAddress getRemoteAddress() {
        return remoteAddress;
    }


    // == METHODS ==========================
    @Override
    public void receive(String message) {
        MethodCallMessage methodCall = new MethodCallMessage(this.messageManager.getAddress(), "receive");
        methodCall.addParameter("message", message);
        messageManager.send(methodCall, this.remoteAddress);
    }
    @Override
    public boolean equals(IChatClient client) {
        return this.remoteAddress.equals(((ChatClientStub) client).getRemoteAddress());
    }
    @Override
    public String toString() {
        return this.remoteAddress.toString();
    }
}
