package server;

import api.OrderService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Сервлет для работы с заказами на сервере (Tomcat)
 */
public class OrderServlet extends HttpServlet {
    private final OrderService orderService = new OrderService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        PrintWriter out = resp.getWriter();

        try {
            String clientIdStr = req.getParameter("clientId");
            if (clientIdStr != null) {
                // Получить заказы конкретного клиента
                int clientId = Integer.parseInt(clientIdStr);
                java.util.List<Order> orders = orderService.getAllByClient(clientId);
                out.print(JsonHelper.toJson(orders));
            } else {
                // Получить все заказы
                java.util.List<Order> orders = orderService.getAll();
                out.print(JsonHelper.toJson(orders));
            }
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            e.printStackTrace();
            out.print(JsonHelper.errorJson("Ошибка сервера: " + e.getMessage()));
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        PrintWriter out = resp.getWriter();

        try {
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
                    Timestamp.valueOf(request.deliveryTime)
            );
            
            if (success) {
                out.print(JsonHelper.successJson("Заказ успешно создан"));
                resp.setStatus(HttpServletResponse.SC_CREATED);
            } else {
                out.print(JsonHelper.errorJson("Ошибка при создании заказа"));
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.print(JsonHelper.errorJson("Ошибка сервера: " + e.getMessage()));
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        PrintWriter out = resp.getWriter();

        try {
            String json = JsonHelper.readJsonFromRequest(req.getReader());
            StatusRequest request = JsonHelper.fromJson(json, StatusRequest.class);

            orderService.updateStatus(request.orderId, request.statusId);
            out.print(JsonHelper.successJson("Статус заказа обновлен"));
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            e.printStackTrace();
            out.print(JsonHelper.errorJson("Ошибка сервера: " + e.getMessage()));
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
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
}

