package ru.shved255.tools.nodeportscanner;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.json.JSONObject;

import ru.shved255.Main;

public class NodeMinecraftPortScanner {
	
    int threadsAmount;
    int startPort;
    int endPort;
    String serverIp;

    public void start() {
        	try (Scanner scanner = new Scanner(System.in)) {
				System.out.print("Введите айпи (127.0.0.1): ");
				String ip1 = scanner.nextLine();
				String ip = ip1;
				System.out.print("Введите кол-во потоков (15): ");
				threadsAmount = Integer.parseInt(scanner.nextLine());
				System.out.print("Введите диапозон портов (25000-25800): ");
				String[] parts = scanner.nextLine().split("-");
				startPort = Integer.parseInt(parts[0]);
				endPort = Integer.parseInt(parts[1]);
				ExecutorService executor = Executors.newFixedThreadPool(threadsAmount);
				long startTime = System.nanoTime();
				for (int port = startPort; port <= endPort; ++port) {
				serverIp = ip + ":" + port;
				executor.submit(() -> {
				    try {
				        JSONObject serverInfo = new JSONObject(new BufferedReader(new InputStreamReader(new URL("https://api.mcstatus.io/v2/status/java/" + serverIp).openStream())).lines().collect(Collectors.joining()));
				        if (serverInfo.getBoolean("online")) {
				            System.out.println(serverIp + "    :    " + serverInfo.getJSONObject("motd").getString("clean").replace("\n", "\\n"));
					        }
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
      Math.round(endTime);
      Math.round(startTime);
      System.out.println("All " + serverIp + " Все порты успешно были пропингованы за " + (double)(endTime - startTime) / 1.0E9 + " s.");
      Main.main(null);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
    }
}

