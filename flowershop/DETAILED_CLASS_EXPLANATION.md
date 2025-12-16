# ДЕТАЛЬНОЕ ОБЪЯСНЕНИЕ ВСЕХ КЛАССОВ И ЛОГИКИ ПРОГРАММЫ

## 📋 ОГЛАВЛЕНИЕ

1. [Структура проекта](#структура-проекта)
2. [Пакет server - Модели данных](#пакет-server---модели-данных)
3. [Пакет server - Утилиты](#пакет-server---утилиты)
4. [Пакет api - Бизнес-логика](#пакет-api---бизнес-логика)
5. [Пакет server - Сервлеты](#пакет-server---сервлеты)
6. [Пакет client - HTTP клиент](#пакет-client---http-клиент)
7. [Пакет gui - Пользовательский интерфейс](#пакет-gui---пользовательский-интерфейс)
8. [Конфигурация web.xml](#конфигурация-webxml)

---

## СТРУКТУРА ПРОЕКТА

```
flowershop/
├── src/
│   ├── server/        # Серверная часть (модели, сервлеты, утилиты)
│   ├── api/           # Бизнес-логика (сервисы для работы с БД)
│   ├── client/        # HTTP клиент
│   └── gui/           # Пользовательский интерфейс (Swing)
└── webapp/
    └── WEB-INF/
        └── web.xml    # Конфигурация сервлетов для Tomcat
```

---

## ПАКЕТ SERVER - МОДЕЛИ ДАННЫХ

Модели данных (POJO классы) представляют сущности из базы данных в виде Java объектов.

### 1. Flower.java - Модель цветка

**Назначение:** Представляет цветок в системе.

**Поля:**
- `int id` - уникальный идентификатор
- `String name` - название цветка (например, "Роза")
- `int typeId` - ID типа цветка
- `int quantity` - количество на складе
- `BigDecimal price` - цена за единицу (используется BigDecimal для точности денежных расчетов)

**Конструктор:**
```java
public Flower(int id, String name, int typeId, int quantity, BigDecimal price)
```
Создает объект цветка со всеми параметрами.

**Методы:**
- Геттеры: `getId()`, `getName()`, `getTypeId()`, `getQuantity()`, `getPrice()`
- Сеттеры: `setQuantity()`, `setPrice()` (только для количества и цены, остальное неизменяемо)

**Пример использования:**
```java
// Создание объекта цветка
Flower rose = new Flower(1, "Роза", 1, 50, new BigDecimal("150.00"));

// Получение данных
String name = rose.getName();        // "Роза"
int stock = rose.getQuantity();      // 50
BigDecimal price = rose.getPrice();  // 150.00

// Обновление цены
rose.setPrice(new BigDecimal("160.00"));
```

**Как используется:** Создается в сервисах (api.FlowerService) при чтении данных из БД через ResultSet, передается через JSON между клиентом и сервером, отображается в GUI таблицах.

---

### 2. Bouquet.java - Модель букета

**Назначение:** Представляет букет в системе.

**Поля:**
- `int id` - уникальный идентификатор
- `String name` - название букета
- `String description` - описание букета
- `BigDecimal price` - цена букета

**Конструктор:**
```java
public Bouquet(int id, String name, String description, BigDecimal price)
```

**Методы:**
- Геттеры: `getId()`, `getName()`, `getDescription()`, `getPrice()`
- Нет сеттеров (immutable объект)

**Пример использования:**
```java
Bouquet wedding = new Bouquet(1, "Свадебный букет", "Белые розы и лилии", new BigDecimal("5000.00"));
String desc = wedding.getDescription(); // "Белые розы и лилии"
```

**Как используется:** Аналогично Flower - создается в BouquetService, передается через JSON, отображается в GUI.

---

### 3. Client.java - Модель клиента

**Назначение:** Представляет клиента магазина.

**Поля:**
- `int id` - ID клиента
- `String fullName` - полное имя клиента
- `String phone` - телефон
- `String login` - логин для входа
- `String password` - пароль (в реальном приложении должен быть захеширован!)

**Конструкторы:**
```java
public Client() {}  // Пустой конструктор для десериализации JSON
public Client(int id, String fullName, String phone, String login, String password)
```

**Методы:**
- Геттеры и сеттеры для всех полей
- `toString()` - строковое представление

**Пример использования:**
```java
Client client = new Client(5, "Иван Иванов", "89161234567", "ivan", "password123");
int clientId = client.getId(); // 5
String name = client.getFullName(); // "Иван Иванов"
```

**Как используется:** При авторизации создается объект клиента, ID передается в ClientMainFrame для дальнейшей работы.

---

### 4. Administrator.java - Модель администратора

**Назначение:** Представляет администратора системы.

**Структура:** Полностью аналогична Client.java (те же поля и методы).

**Разница:** Используется другая таблица в БД (`administrators` вместо `clients`).

---

### 5. Order.java - Модель заказа

**Назначение:** Представляет заказ в системе.

**Поля:**

*Основные данные:*
- `int id` - ID заказа
- `int clientId` - ID клиента, который сделал заказ
- `Integer bouquetId` - ID букета (может быть null, если заказываются цветы)
- `Integer flowerId` - ID цветка (может быть null, если заказывается букет)
- `int bouquetCount` - количество букетов
- `int flowerCount` - количество цветов
- `int statusId` - ID статуса заказа
- `Integer administratorId` - ID администратора, обработавшего заказ (может быть null)
- `BigDecimal totalPrice` - общая стоимость заказа
- `Timestamp orderDate` - дата и время создания заказа
- `Timestamp deliveryTime` - дата и время доставки

*Дополнительные поля для отображения (получаются через JOIN):*
- `String bouquetName` - название букета (из таблицы bouquets)
- `String flowerName` - название цветка (из таблицы flowers)
- `String statusName` - название статуса (из таблицы order_statuses)
- `String clientName` - имя клиента (из таблицы clients)
- `String clientPhone` - телефон клиента (из таблицы clients)

**Конструктор:**
```java
public Order(int id, int clientId, Integer bouquetId, Integer flowerId,
             int bouquetCount, int flowerCount, int statusId, Integer administratorId,
             BigDecimal totalPrice, Timestamp orderDate, Timestamp deliveryTime,
             String bouquetName, String flowerName, String statusName,
             String clientName, String clientPhone)
```
**Важно:** Все 15 параметров! Порядок важен!

**Методы:**
- Только геттеры (нет сеттеров - immutable объект)

**Пример использования:**
```java
Order order = new Order(
    1,              // id
    5,              // clientId
    null,           // bouquetId (заказываются цветы)
    3,              // flowerId (ID цветка)
    0,              // bouquetCount
    10,             // flowerCount (10 цветов)
    1,              // statusId
    null,           // administratorId
    new BigDecimal("1500.00"),  // totalPrice
    Timestamp.valueOf("2024-01-15 10:00:00"),  // orderDate
    Timestamp.valueOf("2024-01-16 14:00:00"),  // deliveryTime
    null,           // bouquetName
    "Роза",         // flowerName
    "В обработке",  // statusName
    "Иван Иванов",  // clientName
    "89161234567"   // clientPhone
);

String flower = order.getFlowerName(); // "Роза"
BigDecimal price = order.getTotalPrice(); // 1500.00
```

**Как используется:** Создается в OrderService.getAll() при выполнении сложного JOIN запроса, передается через JSON, отображается в таблицах заказов.

**Особенность:** Использует Integer (не int) для bouquetId, flowerId, administratorId, потому что они могут быть NULL в БД.

---

### 6. OrderStatus.java - Модель статуса заказа

**Назначение:** Представляет статус заказа (например, "В обработке", "Доставлен").

**Поля:**
- `int id` - ID статуса
- `String name` - название статуса

**Методы:**
- Геттеры и сеттеры
- `toString()` - строковое представление

**Пример использования:**
```java
OrderStatus status = new OrderStatus(1, "ожидает подтверждения");
String statusName = status.getName(); // "ожидает подтверждения"
```

---

## ПАКЕТ SERVER - УТИЛИТЫ

### 1. Database.java - Подключение к базе данных

**Назначение:** Единственная точка подключения к PostgreSQL.

**Константы:**
```java
private static final String URL = "jdbc:postgresql://localhost:5432/flowerstore";
private static final String USER = "postgres";
private static final String PASSWORD = "postgres123";
```

**Статический блок инициализации:**
```java
static {
    try {
        Class.forName("org.postgresql.Driver");
    } catch (ClassNotFoundException e) {
        throw new RuntimeException("PostgreSQL Driver not found!", e);
    }
}
```
**Зачем:** Загружает драйвер PostgreSQL в память JVM при первом обращении к классу.

**Метод getConnection():**
```java
public static Connection getConnection() throws SQLException {
    return DriverManager.getConnection(URL, USER, PASSWORD);
}
```

**Как используется:**
```java
// В любом сервисе:
try (Connection c = Database.getConnection();
     PreparedStatement ps = c.prepareStatement(sql)) {
    // работа с БД
}
```

**Важно:** 
- Используется try-with-resources для автоматического закрытия соединения
- Все сервисы используют этот класс для подключения к БД
- URL указывает на локальный PostgreSQL на порту 5432, БД flowerstore

---

### 2. JsonHelper.java - Работа с JSON

**Назначение:** Утилиты для сериализации/десериализации JSON на сервере.

**Gson объект:**
```java
private static final Gson gson = new GsonBuilder()
    .setPrettyPrinting()    // Форматированный вывод
    .serializeNulls()       // Включать null значения
    .create();
```

**Методы:**

**1. toJson(Object obj)** - объект → JSON строка
```java
public static String toJson(Object obj) {
    return gson.toJson(obj);
}
```
**Пример:**
```java
Flower flower = new Flower(1, "Роза", 1, 50, new BigDecimal("150.00"));
String json = JsonHelper.toJson(flower);
// Результат: {"id":1,"name":"Роза","typeId":1,"quantity":50,"price":150.00}
```

**2. fromJson(String json, Class<T> clazz)** - JSON строка → объект
```java
public static <T> T fromJson(String json, Class<T> clazz) {
    return gson.fromJson(json, clazz);
}
```
**Пример:**
```java
String json = "{\"name\":\"Роза\",\"typeId\":1,\"quantity\":50,\"price\":150.00}";
FlowerRequest request = JsonHelper.fromJson(json, FlowerRequest.class);
```

**3. readJsonFromRequest(BufferedReader reader)** - читает JSON из HTTP запроса
```java
public static String readJsonFromRequest(BufferedReader reader) throws IOException {
    StringBuilder json = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null) {
        json.append(line);
    }
    return json.toString();
}
```
**Зачем:** HTTP запрос передает JSON построчно, нужно собрать все строки в одну.

**4. errorJson(String message)** - создает JSON ответ с ошибкой
```java
public static String errorJson(String message) {
    return "{\"error\":\"" + message.replace("\"", "\\\"") + "\"}";
}
```
**Пример:** `{"error":"Цветок не найден"}`

**5. successJson(String message)** - создает JSON ответ об успехе
```java
public static String successJson(String message) {
    return "{\"success\":\"" + message.replace("\"", "\\\"") + "\"}";
}
```
**Пример:** `{"success":"Цветок успешно добавлен"}`

**Как используется:** Используется во всех сервлетах для преобразования объектов в JSON и обратно.

---

## ПАКЕТ API - БИЗНЕС-ЛОГИКА

Сервисы содержат бизнес-логику работы с данными. Все SQL запросы выполняются здесь.

### 1. FlowerService.java - Работа с цветами

**Назначение:** Вся логика работы с цветами в БД.

**Поля:**
```java
// Нет полей - все методы статические по сути (но не static для удобства создания экземпляра)
```

**Методы:**

#### getAll() - Получить все цветы

```java
public List<Flower> getAll() {
    List<Flower> list = new ArrayList<>();
    String sql = "SELECT id, name, type_id, quantity, price FROM flowers ORDER BY id";
    
    try (Connection c = Database.getConnection();
         PreparedStatement ps = c.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        
        while (rs.next()) {
            list.add(new Flower(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getInt("type_id"),
                rs.getInt("quantity"),
                rs.getBigDecimal("price")
            ));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return list;
}
```

**Что делает:**
1. Создает SQL запрос для выборки всех цветов
2. Получает Connection через Database.getConnection()
3. Выполняет запрос
4. Проходит по результатам (ResultSet) и создает объекты Flower
5. Возвращает список цветов

**Используется:** FlowerServlet.doGet() вызывает этот метод для получения списка цветов.

---

#### add() - Добавить цветок

```java
public boolean add(String name, int typeId, int quantity, BigDecimal price) {
    String sql = "INSERT INTO flowers (name, type_id, quantity, price, remaining_quantity, price_per_unit) VALUES (?, ?, ?, ?, ?, ?)";
    try (Connection c = Database.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
        
        ps.setString(1, name);
        ps.setInt(2, typeId);
        ps.setInt(3, quantity);
        ps.setBigDecimal(4, price);
        ps.setInt(5, quantity);        // remaining_quantity = quantity при создании
        ps.setBigDecimal(6, price);    // price_per_unit = price
        return ps.executeUpdate() == 1;  // Возвращает true, если добавлена 1 строка
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
```

**Что делает:**
1. Создает INSERT запрос с параметрами (?)
2. Устанавливает параметры через PreparedStatement (защита от SQL-инъекций)
3. Выполняет executeUpdate() - возвращает количество измененных строк
4. Возвращает true, если добавлена 1 строка

**Используется:** FlowerServlet.doPost() вызывает этот метод при добавлении нового цветка.

---

#### update() - Обновить цветок

```java
public boolean update(int id, String name, int quantity, BigDecimal price) {
    String sql = "UPDATE flowers SET name=?, quantity=?, price=? WHERE id=?";
    try (Connection c = Database.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
        
        ps.setString(1, name);
        ps.setInt(2, quantity);
        ps.setBigDecimal(3, price);
        ps.setInt(4, id);
        return ps.executeUpdate() == 1;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
```

**Что делает:** Обновляет название, количество и цену цветка по ID.

---

#### delete() - Удалить цветок

```java
public String delete(int id) {
    // Проверяем, используется ли цветок в заказах
    if (isUsedInOrders(id)) {
        return "used_in_orders";
    }
    
    String sql = "DELETE FROM flowers WHERE id=?";
    try (Connection c = Database.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
        ps.setInt(1, id);
        if (ps.executeUpdate() == 1) {
            return "success";
        } else {
            return "error";
        }
    } catch (SQLException e) {
        e.printStackTrace();
        if (e.getMessage() != null && e.getMessage().contains("нарушает ограничение внешнего ключа")) {
            return "used_in_orders";
        }
        return "error";
    }
}
```

**Что делает:**
1. Проверяет через isUsedInOrders(), используется ли цветок в заказах
2. Если используется - возвращает "used_in_orders" (нельзя удалить)
3. Если не используется - удаляет и возвращает "success"
4. При ошибке возвращает "error"

**isUsedInOrders():**
```java
public boolean isUsedInOrders(int id) {
    String sql = "SELECT COUNT(*) FROM orders WHERE flower_id = ?";
    try (Connection c = Database.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
        ps.setInt(1, id);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) > 0;  // Если COUNT > 0, значит используется
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return false;
}
```

**Бизнес-правило:** Нельзя удалить цветок, если он используется в заказах (целостность данных).

---

#### reduceQuantity() - Уменьшить количество на складе

```java
public boolean reduceQuantity(int flowerId, int amount) {
    String sql = "UPDATE flowers SET quantity = quantity - ? WHERE id = ? AND quantity >= ?";
    try (Connection c = Database.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
        ps.setInt(1, amount);
        ps.setInt(2, flowerId);
        ps.setInt(3, amount);  // Проверка: quantity >= amount
        return ps.executeUpdate() == 1;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
```

**Что делает:** Уменьшает количество цветов на складе при создании заказа.

**Защита:** `WHERE quantity >= ?` - не позволяет уменьшить, если недостаточно товара.

**Используется:** OrderService.add() вызывает этот метод после создания заказа на цветы.

---

### 2. OrderService.java - Работа с заказами

**Назначение:** Вся логика работы с заказами.

**Поля:**
```java
private final FlowerService flowerService = new FlowerService();
private final BouquetService bouquetService = new BouquetService();
```

**Методы:**

#### getAll() - Получить все заказы (для администратора)

```java
public List<Order> getAll() {
    List<Order> list = new ArrayList<>();
    String sql = "SELECT o.id, o.client_id, o.bouquet_id, o.flower_id, o.status_id, o.order_date, " +
            "o.delivery_time, o.bouquet_count, o.flower_count, o.administrator_id, o.total_price, " +
            "b.name AS bouquet_name, f.name AS flower_name, s.name AS status_name, c.full_name AS client_name, " +
            "c.phone AS client_phone " +
            "FROM orders o " +
            "LEFT JOIN bouquets b ON o.bouquet_id = b.id " +
            "LEFT JOIN flowers f ON o.flower_id = f.id " +
            "LEFT JOIN order_statuses s ON o.status_id = s.id " +
            "LEFT JOIN clients c ON o.client_id = c.id " +
            "ORDER BY o.id";
    // ... выполнение запроса и создание объектов Order
}
```

**Что делает:**
- Выполняет сложный SQL запрос с JOIN для получения связанных данных
- LEFT JOIN означает, что если связи нет (например, bouquet_id = NULL), строка все равно вернется
- Получает не только ID, но и названия (bouquet_name, flower_name, status_name, client_name)
- Создает объекты Order со всеми 15 параметрами

**Почему JOIN:** В таблице orders хранятся только ID (bouquet_id, flower_id, status_id). Для отображения нужны названия, поэтому делается JOIN с соответствующими таблицами.

---

#### getAllByClient(int clientId) - Получить заказы конкретного клиента

```java
public List<Order> getAllByClient(int clientId) {
    // Аналогично getAll(), но добавляется:
    // WHERE o.client_id = ?
    // ps.setInt(1, clientId);
}
```

**Что делает:** Возвращает только заказы указанного клиента.

---

#### add() - Создать заказ

```java
public boolean add(int clientId, Integer bouquetId, Integer flowerId, int bouquetCount,
                   int flowerCount, int statusId, Integer adminId, BigDecimal totalPrice,
                   Timestamp deliveryTime) {
    String sql = "INSERT INTO orders (...) VALUES (...) RETURNING id, bouquet_count, flower_count, flower_id";
    // ... много проверок и логирования
    // ... установка параметров
    
    ResultSet rs = ps.executeQuery();  // executeQuery, а не executeUpdate, потому что RETURNING
    
    if (rs.next()) {
        added = true;
    }
    
    // Уменьшаем количество цветов на складе
    if (flowerId != null && flowerCount > 0) {
        flowerService.reduceQuantity(flowerId, flowerCount);
    }
    
    return added;
}
```

**Что делает:**
1. Выполняет много проверок (bouquetCount и flowerCount не могут быть оба <= 0)
2. Вставляет заказ в БД
3. Использует RETURNING для получения данных вставленной строки
4. Если заказываются цветы - уменьшает их количество на складе через flowerService.reduceQuantity()

**Бизнес-правило:** При создании заказа на цветы автоматически уменьшается их количество на складе.

---

#### updateStatus() - Изменить статус заказа

```java
public void updateStatus(int orderId, int statusId) {
    String sql = "UPDATE orders SET status_id = ? WHERE id = ?";
    // ... выполнение UPDATE
}
```

**Что делает:** Обновляет статус заказа (например, с "В обработке" на "Доставлен").

---

### 3. ClientService.java - Работа с клиентами

**Методы:**

#### getAll() - Получить всех клиентов
```java
public List<Client> getAll() {
    String sql = "SELECT id, full_name, phone, login, password FROM clients ORDER BY id";
    // ... стандартная логика выборки
}
```

#### add() - Добавить клиента
```java
public boolean add(String fullName, String phone, String login, String password) {
    String sql = "INSERT INTO clients (full_name, phone, login, password) VALUES (?, ?, ?, ?)";
    // ... стандартная логика вставки
}
```

#### login() - Авторизация клиента
```java
public Client login(String login, String password) {
    String sql = "SELECT * FROM clients WHERE login = ? AND password = ?";
    // ... поиск клиента
    if (rs.next()) {
        return new Client(...);  // Возвращает объект Client
    }
    return null;  // Если не найден
}
```

**Что делает:** Проверяет логин и пароль, возвращает объект Client, если найден, или null.

---

### 4. AdminService.java - Работа с администраторами

**Аналогично ClientService**, но:
- Работает с таблицей `administrators`
- Метод `login()` возвращает `boolean` (не объект)

```java
public boolean login(String login, String password) {
    String sql = "SELECT * FROM administrators WHERE login = ? AND password = ?";
    // ...
    return rs.next();  // true, если найден
}
```

---

### 5. BouquetService.java - Работа с букетами

**Аналогично FlowerService:**
- `getAll()` - получить все букеты
- `add()` - добавить букет
- `update()` - обновить букет
- `delete()` - удалить букет (с проверкой использования в заказах)
- `isUsedInOrders()` - проверить использование в заказах

---

## ПАКЕТ SERVER - СЕРВЛЕТЫ

Сервлеты - это классы, которые обрабатывают HTTP запросы от клиента.

**Что такое сервлет:**
- Наследуется от `HttpServlet`
- Обрабатывает HTTP методы: GET, POST, PUT, DELETE
- Получает запрос через `HttpServletRequest`
- Отправляет ответ через `HttpServletResponse`

### 1. FlowerServlet.java - Обработка запросов по цветам

**Назначение:** Обрабатывает HTTP запросы на `/flowers`

**Поля:**
```java
private final FlowerService flowerService = new FlowerService();
```

---

#### doGet() - Получить все цветы

```java
@Override
protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
    resp.setContentType("application/json; charset=UTF-8");
    PrintWriter out = resp.getWriter();
    
    try {
        java.util.List<Flower> flowers = flowerService.getAll();
        out.print(JsonHelper.toJson(flowers));  // Преобразуем список в JSON
        resp.setStatus(HttpServletResponse.SC_OK);  // HTTP 200
    } catch (Exception e) {
        e.printStackTrace();
        out.print(JsonHelper.errorJson("Ошибка сервера: " + e.getMessage()));
        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);  // HTTP 500
    }
}
```

**Что делает:**
1. Устанавливает Content-Type: application/json (чтобы клиент знал, что это JSON)
2. Получает список цветов через flowerService.getAll()
3. Преобразует список в JSON через JsonHelper.toJson()
4. Отправляет JSON в ответ
5. Устанавливает HTTP статус 200 (OK)

**HTTP запрос от клиента:**
```
GET http://localhost:8080/flowershop/flowers
```

**HTTP ответ сервера:**
```
Status: 200 OK
Content-Type: application/json

[{"id":1,"name":"Роза","typeId":1,"quantity":50,"price":150.00}, ...]
```

---

#### doPost() - Добавить цветок

```java
@Override
protected void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
    resp.setContentType("application/json; charset=UTF-8");
    PrintWriter out = resp.getWriter();
    
    try {
        // 1. Читаем JSON из тела запроса
        String json = JsonHelper.readJsonFromRequest(req.getReader());
        
        // 2. Парсим JSON в объект FlowerRequest
        FlowerRequest request = JsonHelper.fromJson(json, FlowerRequest.class);
        
        // 3. Валидация
        if (request.name == null || request.name.trim().isEmpty()) {
            out.print(JsonHelper.errorJson("Название цветка обязательно"));
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);  // HTTP 400
            return;
        }
        
        // 4. Вызываем сервис
        boolean success = flowerService.add(request.name, request.typeId, 
                                           request.quantity, request.price);
        
        // 5. Формируем ответ
        if (success) {
            out.print(JsonHelper.successJson("Цветок успешно добавлен"));
            resp.setStatus(HttpServletResponse.SC_CREATED);  // HTTP 201
        } else {
            out.print(JsonHelper.errorJson("Ошибка при добавлении цветка"));
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);  // HTTP 500
        }
    } catch (Exception e) {
        e.printStackTrace();
        out.print(JsonHelper.errorJson("Ошибка сервера: " + e.getMessage()));
        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
}
```

**HTTP запрос от клиента:**
```
POST http://localhost:8080/flowershop/flowers
Content-Type: application/json

{"name":"Роза","typeId":1,"quantity":50,"price":150.00}
```

**HTTP ответ сервера (успех):**
```
Status: 201 Created
Content-Type: application/json

{"success":"Цветок успешно добавлен"}
```

**Внутренний класс FlowerRequest:**
```java
private static class FlowerRequest {
    public String name;
    public int typeId;
    public int quantity;
    public BigDecimal price;
}
```
Используется только для десериализации JSON - не используется нигде больше.

---

#### doPut() - Обновить цветок

```java
@Override
protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
    // 1. Получаем ID из параметров запроса: /flowers?id=5
    String idStr = req.getParameter("id");
    int id = Integer.parseInt(idStr);
    
    // 2. Читаем JSON из тела запроса
    String json = JsonHelper.readJsonFromRequest(req.getReader());
    FlowerRequest request = JsonHelper.fromJson(json, FlowerRequest.class);
    
    // 3. Вызываем сервис
    boolean success = flowerService.update(id, request.name, request.quantity, request.price);
    
    // 4. Формируем ответ
}
```

**HTTP запрос:**
```
PUT http://localhost:8080/flowershop/flowers?id=5
Content-Type: application/json

{"name":"Тюльпан","quantity":30,"price":80.00}
```

---

#### doDelete() - Удалить цветок

```java
@Override
protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
    String idStr = req.getParameter("id");
    int id = Integer.parseInt(idStr);
    
    String result = flowerService.delete(id);
    
    if ("success".equals(result)) {
        out.print(JsonHelper.successJson("Цветок успешно удален"));
        resp.setStatus(HttpServletResponse.SC_OK);  // HTTP 200
    } else if ("used_in_orders".equals(result)) {
        out.print(JsonHelper.errorJson("Невозможно удалить цветок: он используется в заказах"));
        resp.setStatus(HttpServletResponse.SC_CONFLICT);  // HTTP 409
    } else {
        out.print(JsonHelper.errorJson("Ошибка при удалении цветка"));
        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);  // HTTP 500
    }
}
```

**HTTP запрос:**
```
DELETE http://localhost:8080/flowershop/flowers?id=5
```

---

### 2. OrderServlet.java - Обработка запросов по заказам

**Назначение:** Обрабатывает HTTP запросы на `/orders`

#### doGet() - Получить заказы

```java
@Override
protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
    String clientIdStr = req.getParameter("clientId");
    
    if (clientIdStr != null) {
        // Заказы конкретного клиента
        int clientId = Integer.parseInt(clientIdStr);
        java.util.List<Order> orders = orderService.getAllByClient(clientId);
        out.print(JsonHelper.toJson(orders));
    } else {
        // Все заказы (для администратора)
        java.util.List<Order> orders = orderService.getAll();
        out.print(JsonHelper.toJson(orders));
    }
}
```

**HTTP запросы:**
- Все заказы: `GET /orders`
- Заказы клиента: `GET /orders?clientId=5`

---

#### doPost() - Создать заказ

```java
@Override
protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
    String json = JsonHelper.readJsonFromRequest(req.getReader());
    OrderRequest request = JsonHelper.fromJson(json, OrderRequest.class);
    
    boolean success = orderService.add(
        request.clientId,
        request.bouquetId,
        request.flowerId,
        request.bouquetCount,
        request.flowerCount,
        request.statusId,
        request.adminId,
        request.totalPrice,
        Timestamp.valueOf(request.deliveryTime)  // Преобразуем строку в Timestamp
    );
    
    if (success) {
        out.print(JsonHelper.successJson("Заказ успешно создан"));
        resp.setStatus(HttpServletResponse.SC_CREATED);  // HTTP 201
    }
}
```

---

#### doPut() - Изменить статус заказа

```java
@Override
protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
    String json = JsonHelper.readJsonFromRequest(req.getReader());
    StatusRequest request = JsonHelper.fromJson(json, StatusRequest.class);
    
    orderService.updateStatus(request.orderId, request.statusId);
    out.print(JsonHelper.successJson("Статус заказа обновлен"));
    resp.setStatus(HttpServletResponse.SC_OK);
}
```

---

### 3. AuthServlet.java - Авторизация

**Назначение:** Обрабатывает запросы на `/auth`

#### doPost() - Авторизация

```java
@Override
protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
    String json = JsonHelper.readJsonFromRequest(req.getReader());
    LoginRequest request = JsonHelper.fromJson(json, LoginRequest.class);
    
    // 1. Проверяем администратора
    if (adminService.login(request.login, request.password)) {
        out.print("{\"success\":true,\"userType\":\"admin\"}");
        resp.setStatus(HttpServletResponse.SC_OK);
        return;
    }
    
    // 2. Проверяем клиента
    Client client = clientService.login(request.login, request.password);
    if (client != null) {
        out.print("{\"success\":true,\"userType\":\"client\",\"clientId\":" + client.getId() + "}");
        resp.setStatus(HttpServletResponse.SC_OK);
        return;
    }
    
    // 3. Неверный логин или пароль
    out.print("{\"success\":false,\"error\":\"Неверный логин или пароль\"}");
    resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // HTTP 401
}
```

**Логика:**
1. Сначала проверяет, является ли пользователь администратором
2. Если нет - проверяет, является ли клиентом
3. Если ни то, ни другое - возвращает ошибку

**HTTP запрос:**
```
POST http://localhost:8080/flowershop/auth
Content-Type: application/json

{"login":"admin","password":"12345"}
```

**HTTP ответ (успех для админа):**
```json
{"success":true,"userType":"admin"}
```

**HTTP ответ (успех для клиента):**
```json
{"success":true,"userType":"client","clientId":5}
```

---

### 4. BouquetServlet.java, ClientServlet.java, AdminServlet.java, OrderStatusServlet.java

**Аналогично FlowerServlet**, но работают со своими сущностями (букеты, клиенты, админы, статусы).

---

## ПАКЕТ CLIENT - HTTP КЛИЕНТ

### ServerClient.java - HTTP клиент для общения с сервером

**Назначение:** Единственная точка взаимодействия GUI с сервером через HTTP.

**Константы:**
```java
private static final String SERVER_URL = "http://localhost:8080/flowershop";
private static final Gson gson = new GsonBuilder().create();
```

---

#### Методы для работы с цветами

**getAllFlowers()** - получить все цветы
```java
public List<Flower> getAllFlowers() {
    try {
        String json = sendGetRequest("/flowers");  // GET запрос
        Flower[] flowers = gson.fromJson(json, Flower[].class);  // JSON → массив Flower[]
        return Arrays.asList(flowers);  // массив → список
    } catch (Exception e) {
        e.printStackTrace();
        return java.util.Collections.emptyList();  // При ошибке - пустой список
    }
}
```

**addFlower()** - добавить цветок
```java
public boolean addFlower(String name, int typeId, int quantity, BigDecimal price) {
    try {
        // 1. Создаем объект запроса
        FlowerRequest request = new FlowerRequest();
        request.name = name;
        request.typeId = typeId;
        request.quantity = quantity;
        request.price = price;
        
        // 2. Преобразуем в JSON
        String json = gson.toJson(request);
        
        // 3. Отправляем POST запрос
        String responseJson = sendPostRequest("/flowers", json);
        
        // 4. Проверяем наличие слова "success" в ответе
        return responseJson.contains("success");
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}
```

**updateFlower()** - обновить цветок
```java
public boolean updateFlower(int id, String name, int quantity, BigDecimal price) {
    // Аналогично addFlower, но:
    // sendPutRequest("/flowers?id=" + id, json)
}
```

**deleteFlower()** - удалить цветок
```java
public boolean deleteFlower(int id) {
    String json = sendDeleteRequest("/flowers?id=" + id);
    return json.contains("success");
}
```

---

#### HTTP методы (приватные)

**sendGetRequest()** - GET запрос
```java
private String sendGetRequest(String path) throws IOException {
    URL url = URI.create(SERVER_URL + path).toURL();
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("GET");
    conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
    
    int responseCode = conn.getResponseCode();
    if (responseCode == HttpURLConnection.HTTP_OK) {
        return readResponse(conn);  // Читаем ответ
    } else {
        throw new IOException("HTTP error code: " + responseCode);
    }
}
```

**sendPostRequest()** - POST запрос
```java
private String sendPostRequest(String path, String json) throws IOException {
    URL url = URI.create(SERVER_URL + path).toURL();
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("POST");
    conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
    conn.setDoOutput(true);  // Разрешаем запись в тело запроса
    
    // Записываем JSON в тело запроса
    try (OutputStream os = conn.getOutputStream()) {
        byte[] input = json.getBytes(StandardCharsets.UTF_8);
        os.write(input, 0, input.length);
    }
    
    return readResponse(conn);  // Читаем ответ
}
```

**sendPutRequest()** - PUT запрос (аналогично POST)

**sendDeleteRequest()** - DELETE запрос (аналогично GET)

**readResponse()** - читать ответ от сервера
```java
private String readResponse(HttpURLConnection conn) throws IOException {
    // Выбираем правильный поток (inputStream для успеха, errorStream для ошибок)
    InputStream inputStream = conn.getResponseCode() >= 400 
        ? conn.getErrorStream() 
        : conn.getInputStream();
    
    if (inputStream == null) {
        return "";
    }
    
    // Читаем построчно и собираем в одну строку
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
```

---

#### Методы для работы с заказами

**getAllOrders()** - получить все заказы
```java
public List<Order> getAllOrders() {
    String json = sendGetRequest("/orders");
    Order[] orders = gson.fromJson(json, Order[].class);
    return Arrays.asList(orders);
}
```

**getOrdersByClient(int clientId)** - получить заказы клиента
```java
public List<Order> getOrdersByClient(int clientId) {
    String json = sendGetRequest("/orders?clientId=" + clientId);
    Order[] orders = gson.fromJson(json, Order[].class);
    return Arrays.asList(orders);
}
```

**addOrder()** - создать заказ
```java
public boolean addOrder(int clientId, Integer bouquetId, Integer flowerId, 
                       int bouquetCount, int flowerCount, int statusId, 
                       Integer adminId, BigDecimal totalPrice, String deliveryTime) {
    OrderRequest request = new OrderRequest();
    request.clientId = clientId;
    request.bouquetId = bouquetId;
    // ... заполняем все поля
    request.deliveryTime = deliveryTime;
    
    String json = sendPostRequest("/orders", gson.toJson(request));
    return json.contains("success");
}
```

---

#### login() - Авторизация

```java
public LoginResponse login(String login, String password) {
    try {
        LoginRequest request = new LoginRequest();
        request.login = login;
        request.password = password;
        
        String json = sendPostRequest("/auth", gson.toJson(request));
        return gson.fromJson(json, LoginResponse.class);  // JSON → LoginResponse
    } catch (Exception e) {
        e.printStackTrace();
        return new LoginResponse(false, null, -1);  // При ошибке - неуспешный ответ
    }
}
```

**LoginResponse класс:**
```java
public static class LoginResponse {
    public boolean success;
    public String userType;    // "admin" или "client"
    public int clientId;        // ID клиента (если userType = "client")
    
    public LoginResponse(boolean success, String userType, int clientId) {
        this.success = success;
        this.userType = userType;
        this.clientId = clientId;
    }
}
```

---

## ПАКЕТ GUI - ПОЛЬЗОВАТЕЛЬСКИЙ ИНТЕРФЕЙС

GUI классы используют Java Swing для создания интерфейса.

### 1. LoginFrame.java - Окно авторизации

**Назначение:** Первое окно, которое видит пользователь. Точка входа в приложение.

**Поля:**
```java
private JTextField loginField;        // Поле для ввода логина
private JPasswordField passwordField; // Поле для ввода пароля
private final ServerClient serverClient = new ServerClient();
```

**Конструктор:**
```java
public LoginFrame() {
    setTitle("Авторизация - FlowerShop");
    setSize(400, 250);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    // Создает интерфейс:
    // - Заголовок "FlowerShop"
    // - Поле "Логин"
    // - Поле "Пароль"
    // - Кнопка "Войти"
    
    loginButton.addActionListener(e -> onLogin());  // При нажатии вызывает onLogin()
}
```

**Метод onLogin()** - обработка нажатия кнопки "Войти"
```java
private void onLogin() {
    String login = loginField.getText();
    String password = new String(passwordField.getPassword());
    
    // Отправляем запрос к серверу
    ServerClient.LoginResponse response = serverClient.login(login, password);
    
    if (response.success) {
        if ("admin".equals(response.userType)) {
            // Администратор - открываем AdminMainFrame
            JOptionPane.showMessageDialog(this, "Вход выполнен как администратор");
            new AdminMainFrame().setVisible(true);
            dispose();  // Закрываем окно входа
        } else if ("client".equals(response.userType)) {
            // Клиент - открываем ClientMainFrame с ID клиента
            Client client = new Client(response.clientId, "", "", login, "");
            JOptionPane.showMessageDialog(this, "Вход выполнен как клиент");
            new ClientMainFrame(client).setVisible(true);
            dispose();
        }
    } else {
        // Неверный логин/пароль
        JOptionPane.showMessageDialog(this, "Неверный логин или пароль");
    }
}
```

**main()** - точка входа в приложение
```java
public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
}
```
**Зачем invokeLater:** Swing требует, чтобы GUI создавался в Event Dispatch Thread.

---

### 2. AdminMainFrame.java - Главное окно администратора

**Назначение:** Главное окно для администратора после входа.

**Конструктор:**
```java
public AdminMainFrame() {
    setTitle("Администратор - FlowerShop");
    setSize(900, 600);
    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);  // Не закрывать сразу
    
    // Обработка закрытия окна - выход в окно авторизации
    addWindowListener(new java.awt.event.WindowAdapter() {
        @Override
        public void windowClosing(java.awt.event.WindowEvent windowEvent) {
            exitToLogin();  // При закрытии - показать окно входа
        }
    });
    
    // Создает вкладки
    JTabbedPane tabs = new JTabbedPane();
    tabs.addTab("Цветы", new FlowerPanel());
    tabs.addTab("Букеты", new BouquetPanel());
    tabs.addTab("Заказы", new AdminOrdersPanel());
    
    // Кнопка "Выход"
    JButton exitButton = new JButton("Выход");
    exitButton.addActionListener(e -> exitToLogin());
}
```

**exitToLogin()** - выход в окно авторизации
```java
private void exitToLogin() {
    int confirm = JOptionPane.showConfirmDialog(
        this,
        "Вы уверены, что хотите выйти?",
        "Выход",
        JOptionPane.YES_NO_OPTION
    );
    
    if (confirm == JOptionPane.YES_OPTION) {
        dispose();  // Закрываем это окно
        new LoginFrame().setVisible(true);  // Открываем окно входа
    }
}
```

---

### 3. ClientMainFrame.java - Главное окно клиента

**Назначение:** Главное окно для клиента после входа.

**Конструктор:**
```java
public ClientMainFrame(Client client) {
    setTitle("Клиент - FlowerShop");
    int clientId = client.getId();  // Получаем ID клиента
    
    // Создает вкладки
    JTabbedPane tabs = new JTabbedPane();
    tabs.addTab("Каталог", new ClientCatalogPanel(clientId));  // Передаем ID
    tabs.addTab("Мои заказы", new ClientOrdersPanel(clientId));  // Передаем ID
}
```

**Важно:** ID клиента передается в панели, чтобы они знали, для какого клиента загружать данные.

---

### 4. FlowerPanel.java - Панель управления цветами (админ)

**Назначение:** Позволяет администратору управлять цветами (просмотр, добавление, редактирование, удаление).

**Поля:**
```java
private final ServerClient serverClient = new ServerClient();
private final DefaultTableModel model = new DefaultTableModel(
    new String[]{"Название", "Количество", "Цена"}, 0
) {
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;  // Таблица только для чтения
    }
};
```

**Конструктор:**
```java
public FlowerPanel() {
    // Создает таблицу
    JTable table = new JTable(model);
    
    // Создает кнопки
    JButton addButton = new JButton("Добавить");
    JButton editButton = new JButton("Изменить");
    JButton deleteButton = new JButton("Удалить");
    JButton refreshButton = new JButton("Обновить");
    
    // Привязывает действия
    addButton.addActionListener(e -> addFlower());
    editButton.addActionListener(e -> editFlower(table));
    deleteButton.addActionListener(e -> deleteFlower(table));
    refreshButton.addActionListener(e -> refresh());
    
    // Загружает данные
    refresh();
}
```

**refresh()** - обновить список цветов
```java
private void refresh() {
    model.setRowCount(0);  // Очищаем таблицу
    
    try {
        // Получаем все цветы через ServerClient
        List<Flower> flowers = serverClient.getAllFlowers();
        
        // Заполняем таблицу
        for (Flower f : flowers) {
            model.addRow(new Object[]{
                f.getName() != null ? f.getName() : "",
                f.getQuantity(),
                f.getPrice() != null ? f.getPrice() : BigDecimal.ZERO
            });
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Ошибка при загрузке данных: " + e.getMessage());
        e.printStackTrace();
    }
}
```

**addFlower()** - добавить цветок
```java
private void addFlower() {
    // 1. Показываем диалог ввода названия
    String name = JOptionPane.showInputDialog(this, "Название цветка:");
    if (name == null || name.trim().isEmpty()) {
        return;  // Пользователь отменил
    }
    
    // 2. Показываем диалог ввода количества
    String qtyStr = JOptionPane.showInputDialog(this, "Количество:");
    int quantity = Integer.parseInt(qtyStr.trim());
    
    // 3. Показываем диалог ввода цены
    String priceStr = JOptionPane.showInputDialog(this, "Цена:");
    BigDecimal price = new BigDecimal(priceStr.trim().replace(",", "."));
    
    // 4. Отправляем запрос через ServerClient
    if (serverClient.addFlower(name.trim(), 1, quantity, price)) {
        JOptionPane.showMessageDialog(this, "Цветок успешно добавлен!");
        refresh();  // Обновляем таблицу
    } else {
        JOptionPane.showMessageDialog(this, "Ошибка при добавлении цветка!");
    }
}
```

**editFlower()** - редактировать цветок
```java
private void editFlower(JTable table) {
    int row = table.getSelectedRow();  // Получаем выбранную строку
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Выберите цветок для редактирования!");
        return;
    }
    
    // Получаем ID цветка из списка
    List<Flower> flowers = serverClient.getAllFlowers();
    Flower selectedFlower = flowers.get(row);
    int id = selectedFlower.getId();
    
    // Показываем диалоги с текущими значениями
    String currentName = (String) model.getValueAt(row, 0);
    String name = JOptionPane.showInputDialog(this, "Название цветка:", currentName);
    // ... аналогично для количества и цены
    
    // Отправляем запрос
    if (serverClient.updateFlower(id, name.trim(), quantity, price)) {
        JOptionPane.showMessageDialog(this, "Цветок успешно обновлен!");
        refresh();
    }
}
```

**deleteFlower()** - удалить цветок
```java
private void deleteFlower(JTable table) {
    int row = table.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Выберите цветок для удаления!");
        return;
    }
    
    List<Flower> flowers = serverClient.getAllFlowers();
    Flower selectedFlower = flowers.get(row);
    int id = selectedFlower.getId();
    String name = (String) model.getValueAt(row, 0);
    
    // Подтверждение удаления
    int confirm = JOptionPane.showConfirmDialog(
        this,
        "Вы уверены, что хотите удалить цветок \"" + name + "\"?",
        "Подтверждение удаления",
        JOptionPane.YES_NO_OPTION
    );
    
    if (confirm == JOptionPane.YES_OPTION) {
        if (serverClient.deleteFlower(id)) {
            JOptionPane.showMessageDialog(this, "Цветок успешно удален!");
            refresh();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Ошибка при удалении цветка!\n" +
                "Возможно, цветок используется в заказах.");
        }
    }
}
```

---

### 5. BouquetPanel.java - Панель управления букетами (админ)

**Аналогично FlowerPanel**, но работает с букетами:
- `refresh()` - `serverClient.getAllBouquets()`
- `add()` - `serverClient.addBouquet()`
- `edit()` - `serverClient.updateBouquet()`
- `delete()` - `serverClient.deleteBouquet()`

---

### 6. ClientCatalogPanel.java - Каталог для клиента

**Назначение:** Позволяет клиенту просматривать товары и создавать заказы.

**Поля:**
```java
private final ServerClient serverClient = new ServerClient();
private final int clientId;  // ID клиента для создания заказов
```

**Конструктор:**
```java
public ClientCatalogPanel(int clientId) {
    this.clientId = clientId;
    
    // Создает вкладки
    JTabbedPane tabs = new JTabbedPane();
    tabs.addTab("Цветы", createFlowersPanel());    // Вкладка с цветами
    tabs.addTab("Букеты", createBouquetsPanel());  // Вкладка с букетами
}
```

**createFlowersPanel()** - создает панель с цветами
```java
private JPanel createFlowersPanel() {
    // Создает таблицу с цветами
    flowersModel = new DefaultTableModel(new String[]{"Название", "Количество", "Цена за шт."}, 0);
    flowersTable = new JTable(flowersModel);
    
    // Кнопка "Заказать цветы"
    JButton orderButton = new JButton("Заказать цветы");
    orderButton.addActionListener(e -> orderFlower());
    
    // Кнопка "Обновить"
    JButton refreshButton = new JButton("Обновить");
    refreshButton.addActionListener(e -> refreshFlowers());
    
    return panel;
}
```

**orderFlower()** - создать заказ на цветы
```java
private void orderFlower() {
    int row = flowersTable.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Выберите цветок для заказа!");
        return;
    }
    
    // Получаем выбранный цветок
    List<Flower> flowers = serverClient.getAllFlowers();
    Flower selectedFlower = flowers.get(row);
    int flowerId = selectedFlower.getId();
    Integer availableQty = selectedFlower.getQuantity();
    BigDecimal price = selectedFlower.getPrice();
    
    // Проверяем наличие
    if (availableQty == null || availableQty <= 0) {
        JOptionPane.showMessageDialog(this, "Этот цветок недоступен!");
        return;
    }
    
    // Запрашиваем количество
    String countStr = JOptionPane.showInputDialog(this, 
        "Сколько штук заказать?\nДоступно: " + availableQty + "\nЦена за шт.: " + price,
        "1");
    int count = Integer.parseInt(countStr.trim());
    
    // Проверяем, что не больше доступного
    if (count > availableQty) {
        JOptionPane.showMessageDialog(this, "Недостаточно цветов в наличии! Доступно: " + availableQty);
        return;
    }
    
    // Вычисляем общую стоимость
    BigDecimal totalPrice = price.multiply(BigDecimal.valueOf(count));
    
    // Выбираем время доставки
    Timestamp deliveryTime = selectDeliveryTime();
    if (deliveryTime == null) {
        return;  // Пользователь отменил
    }
    
    // Получаем статус "ожидает подтверждения"
    int statusId = serverClient.getStatusIdByName("ожидает подтверждения");
    if (statusId == -1) {
        statusId = 1;  // По умолчанию
    }
    
    // Создаем заказ
    if (serverClient.addOrder(clientId, null, flowerId, 0, count, statusId, null, 
                             totalPrice, deliveryTime.toString())) {
        JOptionPane.showMessageDialog(this, 
            "Заказ оформлен!\nЦветок: " + flowerName + "\nКоличество: " + count + 
            "\nСумма: " + totalPrice);
        refreshFlowers();
    }
}
```

**selectDeliveryTime()** - выбрать дату и время доставки
```java
private Timestamp selectDeliveryTime() {
    // Создает диалог с выбором даты и времени через JSpinner
    JSpinner dateSpinner = new JSpinner(new SpinnerDateModel(...));
    JSpinner timeSpinner = new JSpinner(new SpinnerDateModel(...));
    
    // Показывает диалог
    int result = JOptionPane.showConfirmDialog(...);
    
    if (result == JOptionPane.OK_OPTION) {
        Date date = (Date) dateSpinner.getValue();
        Date time = (Date) timeSpinner.getValue();
        
        // Объединяет дату и время
        Calendar finalCal = Calendar.getInstance();
        finalCal.set(Calendar.YEAR, dateCal.get(Calendar.YEAR));
        // ... устанавливает все поля
        
        // Проверяет, что время не в прошлом
        if (finalCal.getTimeInMillis() < System.currentTimeMillis()) {
            JOptionPane.showMessageDialog(this, "Время доставки не может быть в прошлом!");
            return null;
        }
        
        return new Timestamp(finalCal.getTimeInMillis());
    }
    
    return null;
}
```

**orderBouquet()** - создать заказ на букет (аналогично orderFlower)

---

### 7. AdminOrdersPanel.java - Панель заказов (админ)

**Назначение:** Позволяет администратору просматривать все заказы и изменять их статусы.

**refresh()** - загрузить все заказы
```java
private void refresh() {
    model.setRowCount(0);
    
    List<Order> orders = serverClient.getAllOrders();
    
    for (Order o : orders) {
        // Определяем, что заказано (букет или цветы)
        String itemName = "";
        int count = 0;
        
        if (o.getBouquetId() != null && o.getBouquetCount() > 0) {
            itemName = o.getBouquetName();
            count = o.getBouquetCount();
        } else if (o.getFlowerId() != null && o.getFlowerCount() > 0) {
            itemName = o.getFlowerName();
            count = o.getFlowerCount();
        }
        
        // Добавляем строку в таблицу
        model.addRow(new Object[]{
            o.getClientName(),
            o.getClientPhone(),
            o.getBouquetId() != null ? itemName : "-",
            o.getFlowerId() != null ? itemName : "-",
            count > 0 ? count : "-",
            o.getTotalPrice(),
            o.getStatusName(),
            o.getOrderDate().toString().substring(0, 16),
            o.getDeliveryTime().toString().substring(0, 16)
        });
    }
}
```

**changeStatus()** - изменить статус заказа
```java
private void changeStatus(JTable table) {
    int row = table.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Выберите заказ!");
        return;
    }
    
    List<Order> orders = serverClient.getAllOrders();
    int orderId = orders.get(row).getId();
    
    // Получаем все возможные статусы
    List<OrderStatus> statuses = serverClient.getAllOrderStatuses();
    
    // Создаем массив названий статусов
    String[] statusNames = new String[statuses.size()];
    for (int i = 0; i < statuses.size(); i++) {
        statusNames[i] = statuses.get(i).getName();
    }
    
    // Показываем диалог выбора статуса
    String selectedStatus = (String) JOptionPane.showInputDialog(
        this,
        "Выберите новый статус заказа:",
        "Изменение статуса",
        JOptionPane.QUESTION_MESSAGE,
        null,
        statusNames,
        statusNames[currentIndex]  // Текущий статус
    );
    
    if (selectedStatus != null) {
        // Находим ID выбранного статуса
        int statusId = -1;
        for (OrderStatus status : statuses) {
            if (status.getName().equals(selectedStatus)) {
                statusId = status.getId();
                break;
            }
        }
        
        // Обновляем статус
        if (serverClient.updateOrderStatus(orderId, statusId)) {
            JOptionPane.showMessageDialog(this, "Статус заказа изменен на: " + selectedStatus);
            refresh();
        }
    }
}
```

---

### 8. ClientOrdersPanel.java - Панель заказов клиента

**Назначение:** Показывает заказы конкретного клиента.

**Аналогично AdminOrdersPanel**, но:
- Использует `serverClient.getOrdersByClient(clientId)` вместо `getAllOrders()`
- Не позволяет изменять статусы (только просмотр)

---

### 9. ClientPanel.java - Панель управления клиентами

**Назначение:** Позволяет администратору просматривать и добавлять клиентов.

**Аналогично FlowerPanel**, но работает с клиентами:
- `refresh()` - `serverClient.getAllClients()`
- `addClient()` - `serverClient.addClient()`

---

## КОНФИГУРАЦИЯ WEB.XML

**Назначение:** Регистрирует сервлеты в Tomcat.

**Структура:**
```xml
<web-app>
    <!-- Для каждого сервлета: -->
    
    <!-- 1. Определение сервлета -->
    <servlet>
        <servlet-name>FlowerServlet</servlet-name>
        <servlet-class>server.FlowerServlet</servlet-class>
    </servlet>
    
    <!-- 2. Привязка URL к сервлету -->
    <servlet-mapping>
        <servlet-name>FlowerServlet</servlet-name>
        <url-pattern>/flowers</url-pattern>
    </servlet-mapping>
</web-app>
```

**Что это делает:**
- Когда Tomcat получает запрос на `/flowers` → вызывает `server.FlowerServlet`
- Когда Tomcat получает запрос на `/orders` → вызывает `server.OrderServlet`
- И так далее

**Все зарегистрированные сервлеты:**
- `/flowers` → `FlowerServlet`
- `/bouquets` → `BouquetServlet`
- `/orders` → `OrderServlet`
- `/clients` → `ClientServlet`
- `/admins` → `AdminServlet`
- `/auth` → `AuthServlet`
- `/order-statuses` → `OrderStatusServlet`

---

## ПОЛНЫЙ ЦИКЛ РАБОТЫ: ПРИМЕР - КЛИЕНТ ЗАКАЗЫВАЕТ ЦВЕТЫ

### Шаг 1: Клиент вводит логин и пароль
```
GUI: LoginFrame
    ↓
Пользователь нажимает "Войти"
    ↓
LoginFrame.onLogin()
    ↓
serverClient.login("client1", "pass123")
```

### Шаг 2: ServerClient отправляет HTTP запрос
```
ServerClient.sendPostRequest("/auth", json)
    ↓
HTTP POST http://localhost:8080/flowershop/auth
Body: {"login":"client1","password":"pass123"}
```

### Шаг 3: Tomcat получает запрос
```
Tomcat (порт 8080)
    ↓
web.xml направляет на /auth
    ↓
AuthServlet.doPost()
```

### Шаг 4: AuthServlet обрабатывает
```
AuthServlet.doPost()
    ↓
Читает JSON: JsonHelper.readJsonFromRequest()
    ↓
Парсит: JsonHelper.fromJson(json, LoginRequest.class)
    ↓
Проверяет: clientService.login("client1", "pass123")
    ↓
    ↓ (в ClientService)
    Database.getConnection()
    ↓
    SELECT * FROM clients WHERE login=? AND password=?
    ↓
    Возвращает Client объект (или null)
    ↓
Формирует JSON ответ: {"success":true,"userType":"client","clientId":5}
    ↓
HTTP Response 200 OK
```

### Шаг 5: Client получает ответ
```
ServerClient получает JSON
    ↓
gson.fromJson(json, LoginResponse.class)
    ↓
Возвращает LoginResponse(success=true, userType="client", clientId=5)
    ↓
LoginFrame.onLogin() получает response
    ↓
Открывает ClientMainFrame(clientId=5)
```

### Шаг 6: Клиент открывает каталог
```
ClientMainFrame(client)
    ↓
Открывает ClientCatalogPanel(clientId=5)
    ↓
ClientCatalogPanel.refreshFlowers()
    ↓
serverClient.getAllFlowers()
    ↓
HTTP GET /flowers
    ↓
FlowerServlet.doGet()
    ↓
flowerService.getAll()
    ↓
    Database.getConnection()
    ↓
    SELECT * FROM flowers
    ↓
    Создает List<Flower>
    ↓
JsonHelper.toJson(flowers)
    ↓
HTTP Response: [{"id":1,"name":"Роза",...}, ...]
    ↓
ClientCatalogPanel заполняет таблицу
```

### Шаг 7: Клиент заказывает цветы
```
Клиент выбирает "Роза" и нажимает "Заказать цветы"
    ↓
ClientCatalogPanel.orderFlower()
    ↓
Показывает диалог: "Сколько штук заказать?"
    ↓
Клиент вводит: 10
    ↓
Выбирает время доставки: selectDeliveryTime()
    ↓
serverClient.addOrder(clientId=5, bouquetId=null, flowerId=1, 
                     bouquetCount=0, flowerCount=10, statusId=1, 
                     adminId=null, totalPrice=1500.00, 
                     deliveryTime="2024-01-16 14:00:00")
    ↓
HTTP POST /orders
Body: {"clientId":5,"flowerId":1,"flowerCount":10,...}
    ↓
OrderServlet.doPost()
    ↓
orderService.add(...)
    ↓
    Database.getConnection()
    ↓
    INSERT INTO orders (...)
    ↓
    flowerService.reduceQuantity(flowerId=1, amount=10)
    ↓
        UPDATE flowers SET quantity = quantity - 10 WHERE id = 1
    ↓
    Возвращает true
    ↓
HTTP Response: {"success":"Заказ успешно создан"}
    ↓
ClientCatalogPanel показывает: "Заказ оформлен!"
    ↓
Обновляет таблицу цветов (refreshFlowers())
```

---

## КЛЮЧЕВЫЕ МОМЕНТЫ ДЛЯ ЗАЩИТЫ

### 1. Архитектура
- **Трехзвенная:** GUI → HTTP → Сервер → БД
- **Разделение ответственности:** каждый слой делает свое дело

### 2. Технологии
- **Swing** - GUI
- **HTTP/JSON** - обмен данными
- **Java Servlets** - обработка HTTP запросов
- **JDBC** - работа с БД
- **PostgreSQL** - хранение данных
- **Tomcat** - контейнер сервлетов
- **Gson** - работа с JSON

### 3. Паттерны
- **MVC:** GUI (View), Сервисы (Model), Сервлеты (Controller)
- **DAO (Data Access Object):** Сервисы инкапсулируют работу с БД
- **DTO (Data Transfer Object):** Модели данных передаются через JSON

### 4. Безопасность
- **PreparedStatement** - защита от SQL-инъекций
- **Валидация** - проверка данных на сервере
- **Try-with-resources** - автоматическое закрытие соединений

### 5. Обработка ошибок
- **Try-catch** блоки везде
- **HTTP статус коды** (200, 201, 400, 401, 500)
- **JSON ответы с описанием ошибок**

---

## ВОПРОСЫ И ОТВЕТЫ ДЛЯ ЗАЩИТЫ

**Q: Как происходит авторизация?**
A: Клиент отправляет логин/пароль через POST /auth. AuthServlet проверяет в БД через AdminService и ClientService. Возвращает JSON с типом пользователя и ID.

**Q: Почему используется JSON?**
A: Универсальный текстовый формат, легко парсить, поддерживается везде. Легче чем XML, понятнее чем бинарные форматы.

**Q: Как работает создание заказа?**
A: ClientCatalogPanel собирает данные → ServerClient.addOrder() → HTTP POST /orders → OrderServlet.doPost() → OrderService.add() → INSERT в БД → reduceQuantity() уменьшает количество на складе.

**Q: Что такое сервлет?**
A: Java класс, который обрабатывает HTTP запросы. Наследуется от HttpServlet, переопределяет doGet(), doPost() и т.д.

**Q: Зачем нужен ServerClient?**
A: Инкапсулирует всю работу с HTTP. GUI не знает про HTTP, JSON, URLs - все это скрыто в ServerClient.

**Q: Как данные передаются между клиентом и сервером?**
A: Через HTTP запросы. Объекты преобразуются в JSON через Gson, отправляются в теле запроса (POST/PUT) или как параметры (GET), на сервере парсятся обратно в объекты.

**Q: Что происходит при удалении цветка, который используется в заказах?**
A: FlowerService.delete() проверяет через isUsedInOrders(). Если используется - возвращает "used_in_orders". FlowerServlet возвращает HTTP 409 (Conflict) с ошибкой.

**Q: Как работает JOIN в OrderService.getAll()?**
A: LEFT JOIN объединяет таблицы orders, bouquets, flowers, order_statuses, clients. Получаем не только ID, но и названия для удобного отображения.

**Q: Зачем нужен web.xml?**
A: Регистрирует сервлеты в Tomcat. Говорит, какой URL обрабатывает какой сервлет.

**Q: Как обеспечивается целостность данных?**
A: Внешние ключи в БД, проверки в сервисах (например, нельзя удалить цветок в заказах), транзакции через Connection.

---

## ЗАКЛЮЧЕНИЕ

Это полноценное клиент-серверное приложение с четким разделением ответственности:

1. **GUI** - только интерфейс, не знает о БД
2. **ServerClient** - только HTTP коммуникация
3. **Сервлеты** - только обработка HTTP запросов
4. **Сервисы** - бизнес-логика и работа с БД
5. **Database** - только подключение
6. **PostgreSQL** - только хранение

Каждый класс имеет четкую роль и ответственность. Код модульный, расширяемый и поддерживаемый.
