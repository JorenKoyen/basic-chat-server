package be.kdg.chat;

import be.kdg.chat.communication.NetworkAddress;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Start {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java Start <port>");
            System.exit(1);
        }

        NetworkAddress address = null;

        try {
            int port = Integer.parseInt(args[0]);
            InetAddress localhost = InetAddress.getLocalHost();

            address = new NetworkAddress(localhost.getHostAddress(), port);

        } catch (UnknownHostException e) {
            System.err.println("Failed to get network address");
            e.printStackTrace();
            System.exit(1);
        }


        ChatServerSkeleton skeleton = new ChatServerSkeleton(address);
        skeleton.run();

    }


}
