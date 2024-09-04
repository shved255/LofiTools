package ru.shved255;

import java.util.Scanner;

import ru.shved255.tools.Utilities;
import ru.shved255.tools.bot.Bot;
import ru.shved255.tools.hosting.Main.MainHosting;
import ru.shved255.tools.nodeportscanner.NodeMinecraftPortScanner;
import ru.shved255.tools.nullping.Nullping;
import ru.shved255.tools.portscan.PortScanner;
import ru.shved255.tools.socketdos.SocketDoS;

public class Main {
	
	public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
			PortScanner portScannerTool = new PortScanner();
			SocketDoS socketDoSTool = new SocketDoS();
			Bot bot1 = new Bot();
			NodeMinecraftPortScanner node = new NodeMinecraftPortScanner();
			System.out.println("██╗      ██████╗ ███████╗██╗████████╗ ██████╗  ██████╗ ██╗     ███████╗");
			System.out.println("██║     ██╔═══██╗██╔════╝██║╚══██╔══╝██╔═══██╗██╔═══██╗██║     ██╔════╝");
			System.out.println("██║     ██║   ██║█████╗  ██║   ██║   ██║   ██║██║   ██║██║     ███████╗");
			System.out.println("██║     ██║   ██║██╔══╝  ██║   ██║   ██║   ██║██║   ██║██║     ╚════██║");
			System.out.println("███████╗╚██████╔╝██║     ██║   ██║   ╚██████╔╝╚██████╔╝███████╗███████║");
			System.out.println("╚══════╝ ╚═════╝ ╚═╝     ╚═╝   ╚═╝    ╚═════╝  ╚═════╝ ╚══════╝╚══════╝");
			System.out.println("created by shved255						       ");
			System.out.println("");
			
			    System.out.print("1. Port scanner                       ||  Порт сканер открытых портов.\n");
			    System.out.print("2. Node minecraft servers scanner     ||  Порт сканер, который ищетоткрытый порт связанный с майнкрафтом.\n");
			    System.out.print("3. Socket DoS                         ||  Отправялет Java сокет-запросы на IP.\n");
			    System.out.print("4. Minecraft Hostings ping            ||  Поиск много ру-хостингов майнкрафт.\n");
			    System.out.print("5. NullPing                           ||  ДоС пакетами на серваки.\n");
			    System.out.print("6. Minecraft bots                     ||  Боты майнкрафт, специально оптимизированны.\n");		
			    System.out.print("7. Exit			              ||  Выход из приложения.\n");
			    System.out.println("");
			    System.out.print("Выберите операцию которая будет выполняться: ");
			    int mode = scanner.nextInt();
			        if(mode == 1) {
			            Utilities.clearScreen();
			            portScannerTool.start();
			        } else if (mode == 2) {
			            Utilities.clearScreen();
			            node.start();
			        } else if (mode == 3) {
			            Utilities.clearScreen();
			            socketDoSTool.start();
			        } else if (mode == 7) {
			            System.exit(0);
			        } else if (mode == 4) {
			        	Utilities.clearScreen();
						try {
							MainHosting.start(null);
						} catch (Throwable e) {
							e.printStackTrace();
						};
			        } else if (mode == 5) {
			        	Utilities.clearScreen();
			        	Nullping.main(null);
			        } else if (mode == 6) {
			        	Utilities.clearScreen();
			        	bot1.start();
			        }
			        
			    System.out.println("Неизвестная операция, выберите от 1 до 8.\n");
			}
		}
    }

