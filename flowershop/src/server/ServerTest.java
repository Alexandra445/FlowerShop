package server;

import api.FlowerService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class ServerTest {
    public static void main(String[] args) {
        FlowerService service = new FlowerService();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\nКоманды: list | add | exit");
            System.out.print("> ");
            String cmd = sc.nextLine().trim();
            if (cmd.equalsIgnoreCase("exit")) break;

            if (cmd.equalsIgnoreCase("list")) {
                List<Flower> list = service.getAll();
                if (list.isEmpty()) System.out.println("Нет цветов.");
                else list.forEach(System.out::println);
            } else if (cmd.toLowerCase().startsWith("add")) {
                // Пример ввода: add Роза,1,50,120.00
                try {
                    String[] parts = cmd.substring(3).trim().split(",");
                    String name = parts[0].trim();
                    Integer typeId = parts[1].trim().isEmpty() ? null : Integer.parseInt(parts[1].trim());
                    int qty = Integer.parseInt(parts[2].trim());
                    BigDecimal price = new BigDecimal(parts[3].trim());
                    boolean ok = service.add(name, typeId, qty, price);
                    System.out.println(ok ? "Добавлено." : "Ошибка при добавлении.");
                } catch (Exception ex) {
                    System.out.println("Неверный формат. Пример: add Роза,1,50,120.00");
                }
            } else {
                System.out.println("Неизвестная команда.");
            }
        }

        sc.close();
        System.out.println("Выход.");
    }
}
