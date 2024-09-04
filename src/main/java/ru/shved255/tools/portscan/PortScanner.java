package ru.shved255.tools.portscan;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import ru.shved255.Main;

public class PortScanner {
    String ip;
    int threadsAmount;
    int startPort;
    int endPort;

    public void start() {
        try (Scanner scanner = new Scanner(System.in)) {
			System.out.print("Введите айпи (127.0.0.1): ");
			this.ip = scanner.nextLine();
			System.out.print("Введите кол-во потоков (15): ");
			this.threadsAmount = Integer.parseInt(scanner.nextLine());
			System.out.print("Введите диапозон портов (1-65535): ");
			String[] parts = scanner.nextLine().split("-");
			this.startPort = Integer.parseInt(parts[0]);
			this.endPort = Integer.parseInt(parts[1]);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
        ExecutorService executor = Executors.newFixedThreadPool(this.threadsAmount);
        long startTime = System.nanoTime();
        int port_ = this.startPort;
        while (port_ <= this.endPort) {
            int port = port_++;
            executor.submit(() -> {
                try (SocketChannel socketChannel = SocketChannel.open();){
                    socketChannel.configureBlocking(true);
                    socketChannel.socket().connect(new InetSocketAddress(this.ip, port), 150);
                    System.out.println(this.ip + ":" + port + " открыт.");
                } catch (Exception exception) {
                }
            });
        }
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
        long endTime = System.nanoTime();
        System.out.println("Все " + this.ip + " порты были успешно пропингованы " + (double)(endTime - startTime) / 1.0E9 + " s.");
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
    }
}

