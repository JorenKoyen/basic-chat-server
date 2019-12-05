package be.kdg.chat.client;

import be.kdg.chat.communication.MessageManager;
import be.kdg.chat.communication.MethodCallMessage;
import be.kdg.chat.communication.NetworkAddress;
import be.kdg.chat.util.Logger;

import java.util.Objects;

public class ChatClientStub implements IChatClient {
    private static final Logger LOGGER = Logger.getLogger("client-stub");
    private NetworkAddress replyAddress;
    private final NetworkAddress originAddress;
    private final MessageManager messageManager;

    public ChatClientStub(NetworkAddress replyAddress, NetworkAddress originAddress, MessageManager messageManager) {
        this.replyAddress = replyAddress;
        this.originAddress = originAddress;
        this.messageManager = messageManager;
    }
    public ChatClientStub(NetworkAddress origin, MessageManager messageManager) {
        this.originAddress = origin;
        this.messageManager = messageManager;
    }


    // == PRIVATE METHOD ===================
    private void waitForAck() {
        // infinite loop waiting for ack
        MethodCallMessage ack = waitForResponse();

        if (!ack.getMethod().equals("ack")) {
            LOGGER.error("Incorrect acknowledgement received");
            return;
        }

        if (!ack.getParameter("result").equals("ok")) {
            LOGGER.error("Expected to have received \"ok\" with acknowledgement");
            return;
        }

        LOGGER.info("Acknowledgement received");

    }
    private MethodCallMessage waitForResponse() {
        // infinite loop waiting for resp
        while (true) {

            // wait sync for resp
            MethodCallMessage resp = messageManager.receiveSync();

            // check if indeed resp
            return resp;
        }
    }

    // == INTERFACE METHODS ================
    @Override
    public void receive(String message) {
        MethodCallMessage methodCall = new MethodCallMessage(this.messageManager.getAddress(), "receive");
        methodCall.addParameter("message", message);
        messageManager.send(methodCall, this.replyAddress);

        waitForAck();
    }

    @Override
    public String getName() {
        MethodCallMessage methodCall = new MethodCallMessage(this.messageManager.getAddress(), "getName");
        messageManager.send(methodCall, this.replyAddress);


        // wait for response
        MethodCallMessage resp = waitForResponse();
        return resp.getParameter("value");
    }

    // == OVERRIDE METHODS =================
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatClientStub that = (ChatClientStub) o;
        return originAddress.equals(that.originAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(originAddress);
    }

    @Override
    public String toString() {
        return this.originAddress.toString();
    }
}
