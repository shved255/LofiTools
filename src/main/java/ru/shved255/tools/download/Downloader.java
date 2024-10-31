package ru.shved255.tools.download;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.json.JSONException;
import org.json.JSONObject;

public class Downloader {

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
	            	System.out.println("Если будет ошибка, вероятно, что у сервера нет икони.");
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

    public static JSONObject fetchServerInfo(String ip) throws IOException, JSONException {
        URL url = new URL("https://api.mcsrvstat.us/3/" + ip);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String response = reader.lines().collect(Collectors.joining());
            return new JSONObject(response);
        }
    }

    public static void download(String data, String fileName) {
        try {
            if (data == null || data.isEmpty()) {
                System.out.println("Данные для декодирования отсутствуют.");
                return;
            }
            if (data.startsWith("data:image/png;base64,")) {
                data = data.substring("data:image/png;base64,".length());
            }
            byte[] imageBytes = Base64.getDecoder().decode(data);
            Path imagesDir = Paths.get("images");
            if (!Files.exists(imagesDir)) {
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
