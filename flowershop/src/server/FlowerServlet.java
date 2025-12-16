package server;

import api.FlowerService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;

/**
 * Сервлет для работы с цветами на сервере (Tomcat)
 * GUI приложение обращается к этому сервлету
 */
public class FlowerServlet extends HttpServlet {
    private final FlowerService flowerService = new FlowerService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        PrintWriter out = resp.getWriter();

        try {
            // Получить все цветы
            java.util.List<Flower> flowers = flowerService.getAll();
            out.print(JsonHelper.toJson(flowers));
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
            FlowerRequest request = JsonHelper.fromJson(json, FlowerRequest.class);

            if (request.name == null || request.name.trim().isEmpty()) {
                out.print(JsonHelper.errorJson("Название цветка обязательно"));
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            boolean success = flowerService.add(request.name, request.typeId, request.quantity, request.price);
            
            if (success) {
                out.print(JsonHelper.successJson("Цветок успешно добавлен"));
                resp.setStatus(HttpServletResponse.SC_CREATED);
            } else {
                out.print(JsonHelper.errorJson("Ошибка при добавлении цветка"));
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
            String idStr = req.getParameter("id");
            if (idStr == null) {
                out.print(JsonHelper.errorJson("ID цветка обязателен"));
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            int id = Integer.parseInt(idStr);
            String json = JsonHelper.readJsonFromRequest(req.getReader());
            FlowerRequest request = JsonHelper.fromJson(json, FlowerRequest.class);

            boolean success = flowerService.update(id, request.name, request.quantity, request.price);
            
            if (success) {
                out.print(JsonHelper.successJson("Цветок успешно обновлен"));
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                out.print(JsonHelper.errorJson("Ошибка при обновлении цветка"));
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.print(JsonHelper.errorJson("Ошибка сервера: " + e.getMessage()));
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        PrintWriter out = resp.getWriter();

        try {
            String idStr = req.getParameter("id");
            if (idStr == null) {
                out.print(JsonHelper.errorJson("ID цветка обязателен"));
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            int id = Integer.parseInt(idStr);
            String result = flowerService.delete(id);
            
            if ("success".equals(result)) {
                out.print(JsonHelper.successJson("Цветок успешно удален"));
                resp.setStatus(HttpServletResponse.SC_OK);
            } else if ("used_in_orders".equals(result)) {
                out.print(JsonHelper.errorJson("Невозможно удалить цветок: он используется в заказах"));
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
            } else {
                out.print(JsonHelper.errorJson("Ошибка при удалении цветка"));
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.print(JsonHelper.errorJson("Ошибка сервера: " + e.getMessage()));
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    // Внутренний класс для десериализации JSON запросов
    private static class FlowerRequest {
        public String name;
        public int typeId;
        public int quantity;
        public BigDecimal price;
    }
}

