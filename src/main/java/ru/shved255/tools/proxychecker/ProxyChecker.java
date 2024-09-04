package ru.shved255.tools.proxychecker;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Random;

public class ProxyChecker {

    private static final String TEST_URL = "http://azenv.net/";
    private static List<String> proxyList;
    private static List<String> workingProxies;

    public static void main(String[] args) {
        proxyList = new ArrayList<>();
        workingProxies = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Введите прокси в формате ip:port или 'file' для загрузки из файла:");
        String input = scanner.nextLine();

        if (input.equalsIgnoreCase("file")) {
            System.out.println("Введите путь к файлу с прокси:");
            String filePath = scanner.nextLine();
            loadProxiesFromFile(filePath);
        } else {
            proxyList.add(input);
        }

        checkProxies();
        
        System.out.println("Проверено прокси: " + workingProxies.size());
        if (!workingProxies.isEmpty()) {
            System.out.println("Хотите сохранить работающие прокси в файл? (да/нет)");
            String saveResponse = scanner.nextLine();
            if (saveResponse.equalsIgnoreCase("да")) {
                System.out.println("Введите путь для сохранения:");
                String savePath = scanner.nextLine();
                saveWorkingProxies(savePath);
            }
        }

        scanner.close();
    }

    private static void loadProxiesFromFile(String filePath) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            for (String line : lines) {
                if (!proxyList.contains(line.trim())) {
                    proxyList.add(line.trim());
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка загрузки файла: " + e.getMessage());
        }
    }

    private static void checkProxies() {
        Random random = new Random();
        while (!proxyList.isEmpty()) {
            int index = random.nextInt(proxyList.size());
            String proxy = proxyList.get(index);
            if (testProxy(proxy)) {
                workingProxies.add(proxy);
            }
            proxyList.remove(index);
        }
    }

    private static boolean testProxy(String proxy) {  	
		ExecutorService executor = Executors.newFixedThreadPool(100);
		executor.submit(() -> {
        System.out.println("Проверка прокси: " + proxy);
        try {
            ProcessBuilder pb = new ProcessBuilder("curl", "--proxy", proxy, "-s", TEST_URL);
            Process process = pb.start();
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            System.out.println("Ошибка при проверке прокси: " + e.getMessage());
        }
		return false;
		});
		return false;
    }

    private static void saveWorkingProxies(String filePath) {
        try (PrintWriter writer = new PrintWriter(filePath)) {
            for (String proxy : workingProxies) {
                writer.println(proxy);
            }
            System.out.println("Работающие прокси сохранены в " + filePath);
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении файла: " + e.getMessage());
        }
    }
}

