package be.kdg.chat.communication;

import be.kdg.chat.util.MapConverter;

import java.util.Map;
import java.util.TreeMap;

public class MethodCallMessage {
    private final NetworkAddress origin;
    private final String method;
    private final Map<String, String> parameters;

    // == CONSTRUCTORS =====================
    public MethodCallMessage(NetworkAddress origin, String method) {
        this.origin = origin;
        this.method = method;
        this.parameters = new TreeMap<>();
    }

    public MethodCallMessage(NetworkAddress origin, String method, Map<String, String> parameters) {
        this.origin = origin;
        this.method = method;
        this.parameters = parameters;
    }

    // == GETTERS ==========================
    public NetworkAddress getOrigin() {
        return origin;
    }
    public String getMethod() {
        return method;
    }
    public String getParameter(String parameter) {
        return this.parameters.get(parameter);
    }
    public Map<String, String> getParameters() {
        // return copy of parameter map
        return new TreeMap<>(this.parameters);
    }

    // == METHODS ==========================
    public void addParameter(String name, String value) {
        this.parameters.put(name, value);
    }

    @Override
    public String toString() {
        return String.format("Message: [%s] -> %s %s",
                this.origin,
                this.method,
                MapConverter.convertToString(this.parameters));
    }
}
