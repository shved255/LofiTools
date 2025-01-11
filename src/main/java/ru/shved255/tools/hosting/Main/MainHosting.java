package ru.shved255.tools.hosting.Main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.json.JSONException;
import org.json.JSONObject;

import ru.shved255.tools.hosting.HostingAddress;

public class MainHosting {
	
    public void start() {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Введите айпи и порт (127.0.0.1:25565): ");
            String ip = scanner.nextLine();
            System.out.print("Введите название картинки (popa): ");
            String name = scanner.nextLine();
            JSONObject serverInfo;
			try {
				serverInfo = fetchServerInfo(ip);
	            if(serverInfo != null) {
	            	System.out.println("Если будет ошибка, вероятно, что у сервера нет иконки.");
	                String icon = serverInfo.getString("icon");
	                if(icon != null && !icon.isEmpty()) {
	                    download(icon, name + ".png");
	                } else {
	                    System.out.println("Иконка сервера не найдена.");
	                }
	            } else {
	                System.out.println("Не удалось получить информацию о сервере.");
	            }
			} catch (JSONException | IOException e) {
				e.printStackTrace();
			}
        }
    }
	
	public static void start(String[] args) throws Throwable {		
	    File output;
	    Boolean mode;
	    int timeout;
	    int threadsAmount;
	    ExecutorService executor;
	    AtomicInteger a = new AtomicInteger(1);
	    Boolean ava;
	    Boolean soft;	    
        
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
            System.out.println("Скачивать ли иконки серверов? (true): ");
            ava = scan.nextBoolean();
            System.out.println("Смотреть ли ядро серверов? (false, т.к. долго ищет.): ");
            soft = scan.nextBoolean();
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
        while(true) {
			executor.submit(() -> {
		        	HostingAddress adres = HostingAddress.generate(mode, timeout);
		        	if(adrs.add(adres)) {
		        		if(adres.fetch()) {
		        			try (BufferedWriter writer1 = new BufferedWriter(new FileWriter(output, true))) {
		        				try {
		        					if(soft) {
		        						JSONObject softwares;
	        			            	softwares = SoftWare(adres.toString());
	        			            	String software = softwares.getString("software");
			        					writer1.write(a.getAndIncrement() + " . " + adres.toString() + "\tИгроки: " + adres.getPinger().getPlayersOnline() + "/" + adres.getPinger().getMaxPlayers() + "\tВерсия: " + adres.getPinger().getGameVersion() + "\tОписание: " + adres.getPinger().getMotd() + "\tЯдро: " + software + "\n");
			        					writer1.flush();
		        					} else if(!soft) {
		        						writer1.write(a.getAndIncrement() + " . " + adres.toString() + "\tИгроки: " + adres.getPinger().getPlayersOnline() + "/" + adres.getPinger().getMaxPlayers() + "\tВерсия: " + adres.getPinger().getGameVersion() + "\tОписание: " + adres.getPinger().getMotd() + "\n");
		        						writer1.flush();
		        					}
		        					if(ava) {
		        			            JSONObject serverInfo;
		        						serverInfo = fetchServerInfo(adres.toString());
		        				        String icon = serverInfo.getString("icon");
		        				        if(icon != null && !icon.isEmpty()) {
		        				        	download(icon, adres.toString().replace(".", String.valueOf("-")).replace(":", String.valueOf("-")) + ".png");
		        				        }
		        					}
		        				} finally {
		        					if (writer1 != null) {
		        						writer1.close();
		        					}
		        				}
		        			}
		        			catch (Throwable throwable2) {
		        			}
		        		}
		        	}
				});
        }
     }
	
    public static JSONObject fetchServerInfo(String ip) throws IOException, JSONException {
        URL url = new URL("https://api.mcsrvstat.us/3/" + ip);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String response = reader.lines().collect(Collectors.joining());
            return new JSONObject(response);
        }
    }
    
    public static JSONObject SoftWare(String ip) throws IOException, JSONException {
        URL url = new URL("https://api.mcsrvstat.us/3/" + ip);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String response = reader.lines().collect(Collectors.joining());
            return new JSONObject(response);
        }
    }

    public static void download(String data, String fileName) {
        try {
            if(data == null || data.isEmpty()) {
                System.out.println("Данные для декодирования отсутствуют.");
                return;
            }
            if(data.startsWith("data:image/png;base64,")) {
                data = data.substring("data:image/png;base64,".length());
            }
            byte[] imageBytes = Base64.getDecoder().decode(data);
            Path imagesDir = Paths.get("images");
            if(!Files.exists(imagesDir)) {
                Files.createDirectories(imagesDir);
            }
            Path imagePath = imagesDir.resolve(fileName);
            Files.write(imagePath, imageBytes);
            System.out.println("Изображение сохранено: " + imagePath.toAbsolutePath());
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка декодирования Base64: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
 