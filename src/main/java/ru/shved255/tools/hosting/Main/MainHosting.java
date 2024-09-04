package ru.shved255.tools.hosting.Main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.shved255.tools.hosting.HostingAddress;

public class MainHosting {
    
	public static void start(String[] args) throws Throwable {
		
	    File output;
	    Boolean mode;
	    int timeout;
	    int threadsAmount;
	    ExecutorService executor;
        
        System.out.println("Ping logic by MeMax3d");
        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("");
        LinkedHashSet<HostingAddress> adrs = new LinkedHashSet<HostingAddress>();
        Scanner scan = new Scanner(System.in, "UTF-8");
        String filename = "";
        try {
            System.out.print("Введите кол-во потоков (15): ");
            threadsAmount = scan.nextInt();
            executor = Executors.newFixedThreadPool(threadsAmount);
            System.out.print("Введите таймаут в мс. (100): ");
            timeout = scan.nextInt();
            System.out.println("Начался поиск, ожидайте.");
        }
        catch (Exception ex) {
            System.out.println("Введите корректное число.");
            scan.close();
            return;
        }
        mode = null;
        filename = "servers";
        scan.close();
        try {
            output = new File(String.valueOf(filename) + ".txt");
            output.createNewFile();
        }
        catch (Exception ex) {
            scan.close();
            return;
        }
        while (true) {
			executor.submit(() -> {
            HostingAddress adres = HostingAddress.generate(mode, timeout);
            if (adrs.add(adres)) {
                if (adres.fetch()) {
                    try (BufferedWriter writer1 = new BufferedWriter(new FileWriter(output, true))) {
                        try {
                            writer1.write( ". " + adres.toString() + "\tИгроки: " + adres.getPinger().getPlayersOnline() + "/" + adres.getPinger().getMaxPlayers() + "\tВерсия: " + adres.getPinger().getGameVersion() + "\tОписание: " + adres.getPinger().getMotd() + "\n");
                            writer1.flush();
                        } finally {
                            if (writer1 != null) {
                                writer1.close();
                            }
                        }
                    }
                    catch (Throwable throwable2) {
                    }
                }
            } else if (adrs.contains(adres)) {
            		}
				});
        	}
        }
}
 