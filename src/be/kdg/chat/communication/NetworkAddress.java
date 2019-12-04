package be.kdg.chat.communication;

import java.util.Objects;

public class NetworkAddress {
    private final String address;
    private final int port;

    public NetworkAddress(String address, int port) {
        this.address = address;
        this.port = port;
    }

    // == GETTERS ==========================
    public String getAddress() {
        return address;
    }
    public int getPort() {
        return port;
    }

    // == METHODS ==========================
    @Override
    public String toString() {
        return this.address + ":" + this.port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NetworkAddress address1 = (NetworkAddress) o;
        return port == address1.port &&
                Objects.equals(address, address1.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, port);
    }
}
