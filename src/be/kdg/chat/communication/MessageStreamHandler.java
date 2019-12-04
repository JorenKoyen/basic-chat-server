package be.kdg.chat.communication;

import be.kdg.chat.util.Logger;

import java.io.*;
import java.util.Map;
import java.util.TreeMap;

public class MessageStreamHandler {
    private static final String ORIGIN_IP_IDENTIFIER = "origin.IP";
    private static final String ORIGIN_PORT_IDENTIFIER = "origin.port";
    private static final String METHOD_IDENTIFIER = "method.name";
    private static final String MESSAGE_IDENTIFIER = "MethodCallMessage";

    // == PUBLIC METHODS ===================
    public static MethodCallMessage read(InputStream stream) {
        // create tokenizer that parses the incoming stream
        StreamTokenizer tokenizer =
                new StreamTokenizer(new BufferedReader(new InputStreamReader(stream)));

        tokenizer.quoteChar('\"');

        try {
            checkHeader(tokenizer);
            return parseMessage(tokenizer);
        } catch (IOException e) {
            System.err.println("Failed to read message from input stream");
            e.printStackTrace();
        }

        // if reached something went wrong -> return null
        return null;
    }

    public static void write(MethodCallMessage message, OutputStream stream) {
        PrintWriter writer = new PrintWriter(stream, true);

        // write header to output stream
        writeHeader(writer);

        // write complete method call to outputstream
        writeMessage(message, writer);
    }

    // == PRIVATE METHODS ==================
    /**
     * Checks if a stream begins with the right header.
     * The header is defined as the string 'MethodCallMessage'.
     *
     * @param tokenizer connected to the input stream.
     * @throws IOException is thrown when the stream cannot be read.
     */
    private static void checkHeader(StreamTokenizer tokenizer) throws IOException {
        int token = tokenizer.nextToken();

        // check if stream contains a message
        if (token != StreamTokenizer.TT_WORD && token != '\"')
            throw new IOException("Stream does not contain a message");

        // check if value is correct
        if (!String.valueOf(tokenizer.sval).equals(MESSAGE_IDENTIFIER))
            throw new IOException("Stream does not contain a message");
    }

    /**
     * Writes the header to a stream.
     * The header is defined as the string 'MethodCallMessage'.
     *
     * @param writer connected to the output stream
     */
    private static void writeHeader(PrintWriter writer) {
        writer.println(MESSAGE_IDENTIFIER);
    }

    /**
     * Parses a message from a stream, without the header.
     *
     * @param tokenizer connected to the input stream
     * @return the message that was read.
     * @throws IOException is thrown when the stream cannot be read.
     */
    private static MethodCallMessage parseMessage(StreamTokenizer tokenizer) throws IOException {
        NetworkAddress origin = readOrigin(tokenizer);
        String method = readMethodName(tokenizer);
        Map<String, String> params = readParameters(tokenizer);

        return new MethodCallMessage(origin, method, params);
    }

    /**
     * Writes a message (without header) to a stream.
     *
     * @param message the message to be written .
     * @param writer  the output stream.
     */
    private static void writeMessage(MethodCallMessage message, PrintWriter writer) {
        writeOrigin(message.getOrigin(), writer);
        writeMethodName(message.getMethod(), writer);
        writeParameters(message.getParameters(), writer);
    }

    /**
     * Reads the address of the originator of a message from a stream.
     *
     * @param tokenizer connected to the stream.
     * @return the address of the originator of the message.
     * @throws IOException is thrown when the stream cannot be read.
     */
    private static NetworkAddress readOrigin(StreamTokenizer tokenizer) throws IOException {
        // parse key value pair for the ip address from stream
        KeyValuePair address = readKeyValuePair(tokenizer);

        if (!address.getKey().equals(ORIGIN_IP_IDENTIFIER))
            throw new IOException("Stream does not contain the IP of the originator of the message");


        // parse key value pair for the port from stream
        KeyValuePair port = readKeyValuePair(tokenizer);

        if(!port.getKey().equals(ORIGIN_PORT_IDENTIFIER))
            throw new IOException("Stream does not contain the port of the originator of the message");

        // return network address from received values
        return new NetworkAddress(
                address.getValue(),
                Integer.parseInt(port.getValue())
        );
    }

    /**
     * Writes the originator of a message to the output stream.
     *
     * @param origin the originator of the message.
     * @param writer the output stream.
     */
    private static void writeOrigin(NetworkAddress origin, PrintWriter writer) {
        writer.println(formatKeyValueForStream(ORIGIN_IP_IDENTIFIER, origin.getAddress()));
        writer.println(formatKeyValueForStream(ORIGIN_PORT_IDENTIFIER, String.valueOf(origin.getPort())));
    }

    /**
     * Reads the name of the method to be called from a stream.
     *
     * @param tokenizer connected to the stream.
     * @return the name of the method.
     * @throws IOException is thrown when the stream cannot be read.
     */
    private static String readMethodName(StreamTokenizer tokenizer) throws IOException {
        KeyValuePair method = readKeyValuePair(tokenizer);

        if (!method.getKey().equals(METHOD_IDENTIFIER))
            throw new IOException("Stream does not contain the method name of the message");

        return method.getValue();
    }

    /**
     * Writes the name of the method to be called to a stream.
     *
     * @param methodName the name of the method.
     * @param writer     the stream.
     */
    private static void writeMethodName(String methodName, PrintWriter writer) {
        writer.println(formatKeyValueForStream(METHOD_IDENTIFIER, methodName));
    }

    /**
     * Reads the parameters that are passed with a procedure-call from a stream.
     *
     * @param tokenizer connected to the stream.
     * @return the parameters as (String parameterName, String parameterValue) values.
     * @throws IOException is thrown when the stream cannot be read.
     */
    private static Map<String, String> readParameters(StreamTokenizer tokenizer) throws IOException {
        Map<String, String> params = new TreeMap<>();

        // loop until EOF (end of file) reached
        while (peekToken(tokenizer) != StreamTokenizer.TT_EOF) {

            // read next parameter in stream
            KeyValuePair pair = readKeyValuePair(tokenizer);
            params.put(pair.getKey(), pair.getValue());

        }

        return params;
    }

    /**
     * Writes the parameters that are passed with a procedure-call to a stream.
     *
     * @param parameters the parameters as (String parameterName, String parameterValue) values.
     * @param writer     the stream.
     */
    private static void writeParameters(Map<String, String> parameters, PrintWriter writer) {
        for (String key : parameters.keySet()) {
            writer.println(formatKeyValueForStream(key, parameters.get(key)));
        }
    }

    /**
     * Reads a name and a value from a stream.
     *
     * @param tokenizer connected to the stream.
     * @return the key and value read from the stream.
     * @throws IOException is thrown when the stream cannot be read.
     */
    private static KeyValuePair readKeyValuePair(StreamTokenizer tokenizer) throws IOException {

        // force check if key is indeed a word
        int token = tokenizer.nextToken();
        forceCheckTokenIsWord(token);
        String key = tokenizer.sval;

        // force check if value is indeed a word
        token = tokenizer.nextToken();
        forceCheckTokenIsWord(token);
        String value = tokenizer.sval;

        // return key value pair
        return new KeyValuePair(key, value);

    }

    private static int peekToken(StreamTokenizer tokenizer) throws IOException {
        int token = tokenizer.nextToken();
        tokenizer.pushBack();

        return token;
    }

    private static String formatKeyValueForStream(String key, String value) {
        return String.format("%s \"%s\"", key, value);
    }

    private static void forceCheckTokenIsWord(int token) throws IOException {
        if (token != StreamTokenizer.TT_WORD && token != '\"')
            throw new IOException("Expected a word as token but found " + token);
    }
}
