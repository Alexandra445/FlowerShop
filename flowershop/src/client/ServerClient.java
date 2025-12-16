package client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import server.Flower;
import server.Order;
import server.Bouquet;
import server.Client;
import server.Administrator;
import server.OrderStatus;

import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * Клиент для обращения к серверу (Tomcat)
 * GUI приложение использует этот класс для отправки запросов к серверу
 */
public class ServerClient {
    private static final String SERVER_URL = "http://localhost:8080/flowershop";
    private static final Gson gson = new GsonBuilder().create();

    // ========== Работа с цветами ==========

    public List<Flower> getAllFlowers() {
        try {
            String json = sendGetRequest("/flowers");
            Flower[] flowers = gson.fromJson(json, Flower[].class);
            return Arrays.asList(flowers);
        } catch (Exception e) {
            e.printStackTrace();
            return java.util.Collections.emptyList();
        }
    }

    public boolean addFlower(String name, int typeId, int quantity, BigDecimal price) {
        try {
            FlowerRequest request = new FlowerRequest();
            request.name = name;
            request.typeId = typeId;
            request.quantity = quantity;
            request.price = price;
            
            String json = sendPostRequest("/flowers", gson.toJson(request));
            return json.contains("success");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateFlower(int id, String name, int quantity, BigDecimal price) {
        try {
            FlowerRequest request = new FlowerRequest();
            request.name = name;
            request.quantity = quantity;
            request.price = price;
            
            String json = sendPutRequest("/flowers?id=" + id, gson.toJson(request));
            return json.contains("success");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteFlower(int id) {
        try {
            String json = sendDeleteRequest("/flowers?id=" + id);
            return json.contains("success");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ========== Работа с букетами ==========

    public List<Bouquet> getAllBouquets() {
        try {
            String json = sendGetRequest("/bouquets");
            Bouquet[] bouquets = gson.fromJson(json, Bouquet[].class);
            return Arrays.asList(bouquets);
        } catch (Exception e) {
            e.printStackTrace();
            return java.util.Collections.emptyList();
        }
    }

    public boolean addBouquet(String name, String description, BigDecimal price) {
        try {
            BouquetRequest request = new BouquetRequest();
            request.name = name;
            request.description = description;
            request.price = price;
            
            String json = sendPostRequest("/bouquets", gson.toJson(request));
            return json.contains("success");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateBouquet(int id, String name, String description, BigDecimal price) {
        try {
            BouquetRequest request = new BouquetRequest();
            request.name = name;
            request.description = description;
            request.price = price;
            
            String json = sendPutRequest("/bouquets?id=" + id, gson.toJson(request));
            return json.contains("success");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteBouquet(int id) {
        try {
            String json = sendDeleteRequest("/bouquets?id=" + id);
            return json.contains("success");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ========== Работа с заказами ==========

    public List<Order> getAllOrders() {
        try {
            String json = sendGetRequest("/orders");
            Order[] orders = gson.fromJson(json, Order[].class);
            return Arrays.asList(orders);
        } catch (Exception e) {
            e.printStackTrace();
            return java.util.Collections.emptyList();
        }
    }

    public List<Order> getOrdersByClient(int clientId) {
        try {
            String json = sendGetRequest("/orders?clientId=" + clientId);
            Order[] orders = gson.fromJson(json, Order[].class);
            return Arrays.asList(orders);
        } catch (Exception e) {
            e.printStackTrace();
            return java.util.Collections.emptyList();
        }
    }

    public boolean addOrder(int clientId, Integer bouquetId, Integer flowerId, int bouquetCount,
                           int flowerCount, int statusId, Integer adminId, BigDecimal totalPrice,
                           String deliveryTime) {
        try {
            OrderRequest request = new OrderRequest();
            request.clientId = clientId;
            request.bouquetId = bouquetId;
            request.flowerId = flowerId;
            request.bouquetCount = bouquetCount;
            request.flowerCount = flowerCount;
            request.statusId = statusId;
            request.adminId = adminId;
            request.totalPrice = totalPrice;
            request.deliveryTime = deliveryTime;
            
            String json = sendPostRequest("/orders", gson.toJson(request));
            return json.contains("success");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateOrderStatus(int orderId, int statusId) {
        try {
            StatusRequest request = new StatusRequest();
            request.orderId = orderId;
            request.statusId = statusId;
            
            String json = sendPutRequest("/orders", gson.toJson(request));
            return json.contains("success");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ========== Работа со статусами заказов ==========

    public List<OrderStatus> getAllOrderStatuses() {
        try {
            String json = sendGetRequest("/order-statuses");
            OrderStatus[] statuses = gson.fromJson(json, OrderStatus[].class);
            return Arrays.asList(statuses);
        } catch (Exception e) {
            e.printStackTrace();
            return java.util.Collections.emptyList();
        }
    }

    public int getStatusIdByName(String name) {
        try {
            List<OrderStatus> statuses = getAllOrderStatuses();
            for (OrderStatus status : statuses) {
                if (status.getName().equals(name)) {
                    return status.getId();
                }
            }
            return -1;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    // ========== Работа с клиентами ==========

    public List<Client> getAllClients() {
        try {
            String json = sendGetRequest("/clients");
            Client[] clients = gson.fromJson(json, Client[].class);
            return Arrays.asList(clients);
        } catch (Exception e) {
            e.printStackTrace();
            return java.util.Collections.emptyList();
        }
    }

    public boolean addClient(String fullName, String phone, String login, String password) {
        try {
            ClientRequest request = new ClientRequest();
            request.fullName = fullName;
            request.phone = phone;
            request.login = login;
            request.password = password;
            
            String json = sendPostRequest("/clients", gson.toJson(request));
            return json.contains("success");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ========== Работа с администраторами ==========

    public List<Administrator> getAllAdmins() {
        try {
            String json = sendGetRequest("/admins");
            Administrator[] admins = gson.fromJson(json, Administrator[].class);
            return Arrays.asList(admins);
        } catch (Exception e) {
            e.printStackTrace();
            return java.util.Collections.emptyList();
        }
    }

    public boolean addAdmin(String fullName, String phone, String login, String password) {
        try {
            AdminRequest request = new AdminRequest();
            request.fullName = fullName;
            request.phone = phone;
            request.login = login;
            request.password = password;
            
            String json = sendPostRequest("/admins", gson.toJson(request));
            return json.contains("success");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ========== Авторизация ==========

    public LoginResponse login(String login, String password) {
        try {
            LoginRequest request = new LoginRequest();
            request.login = login;
            request.password = password;
            
            String json = sendPostRequest("/auth", gson.toJson(request));
            return gson.fromJson(json, LoginResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            return new LoginResponse(false, null, -1);
        }
    }

    // ========== HTTP методы ==========

    private String sendGetRequest(String path) throws IOException {
        URL url = URI.create(SERVER_URL + path).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            return readResponse(conn);
        } else {
            throw new IOException("HTTP error code: " + responseCode);
        }
    }

    private String sendPostRequest(String path, String json) throws IOException {
        URL url = URI.create(SERVER_URL + path).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setDoOutput(true);
        
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = json.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        
        int responseCode = conn.getResponseCode();
        return readResponse(conn);
    }

    private String sendPutRequest(String path, String json) throws IOException {
        URL url = URI.create(SERVER_URL + path).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("PUT");
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setDoOutput(true);
        
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = json.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        
        return readResponse(conn);
    }

    private String sendDeleteRequest(String path) throws IOException {
        URL url = URI.create(SERVER_URL + path).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("DELETE");
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        
        return readResponse(conn);
    }

    private String readResponse(HttpURLConnection conn) throws IOException {
        InputStream inputStream = conn.getResponseCode() >= 400 
            ? conn.getErrorStream() 
            : conn.getInputStream();
        
        if (inputStream == null) {
            return "";
        }
        
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return response.toString();
        }
    }

    // ========== Внутренние классы для запросов ==========

    private static class FlowerRequest {
        public String name;
        public int typeId;
        public int quantity;
        public BigDecimal price;
    }

    private static class BouquetRequest {
        public String name;
        public String description;
        public BigDecimal price;
    }

    private static class OrderRequest {
        public int clientId;
        public Integer bouquetId;
        public Integer flowerId;
        public int bouquetCount;
        public int flowerCount;
        public int statusId;
        public Integer adminId;
        public BigDecimal totalPrice;
        public String deliveryTime;
    }

    private static class StatusRequest {
        public int orderId;
        public int statusId;
    }

    private static class ClientRequest {
        public String fullName;
        public String phone;
        public String login;
        public String password;
    }

    private static class AdminRequest {
        public String fullName;
        public String phone;
        public String login;
        public String password;
    }

    private static class LoginRequest {
        public String login;
        public String password;
    }

    public static class LoginResponse {
        public boolean success;
        public String userType;
        public int clientId;

        public LoginResponse(boolean success, String userType, int clientId) {
            this.success = success;
            this.userType = userType;
            this.clientId = clientId;
        }
    }
}

