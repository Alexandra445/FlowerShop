package server;

import api.BouquetService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;

/**
 * Сервлет для работы с букетами на сервере (Tomcat)
 */
public class BouquetServlet extends HttpServlet {
    private final BouquetService bouquetService = new BouquetService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        PrintWriter out = resp.getWriter();

        try {
            java.util.List<Bouquet> bouquets = bouquetService.getAll();
            out.print(JsonHelper.toJson(bouquets));
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
            BouquetRequest request = JsonHelper.fromJson(json, BouquetRequest.class);

            boolean success = bouquetService.add(request.name, request.description, request.price);
            
            if (success) {
                out.print(JsonHelper.successJson("Букет успешно добавлен"));
                resp.setStatus(HttpServletResponse.SC_CREATED);
            } else {
                out.print(JsonHelper.errorJson("Ошибка при добавлении букета"));
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
                out.print(JsonHelper.errorJson("ID букета обязателен"));
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            int id = Integer.parseInt(idStr);
            String json = JsonHelper.readJsonFromRequest(req.getReader());
            BouquetRequest request = JsonHelper.fromJson(json, BouquetRequest.class);

            boolean success = bouquetService.update(id, request.name, request.description, request.price);
            
            if (success) {
                out.print(JsonHelper.successJson("Букет успешно обновлен"));
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                out.print(JsonHelper.errorJson("Ошибка при обновлении букета"));
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
                out.print(JsonHelper.errorJson("ID букета обязателен"));
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            int id = Integer.parseInt(idStr);
            String result = bouquetService.delete(id);
            
            if ("success".equals(result)) {
                out.print(JsonHelper.successJson("Букет успешно удален"));
                resp.setStatus(HttpServletResponse.SC_OK);
            } else if ("used_in_orders".equals(result)) {
                out.print(JsonHelper.errorJson("Невозможно удалить букет: он используется в заказах"));
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
            } else {
                out.print(JsonHelper.errorJson("Ошибка при удалении букета"));
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.print(JsonHelper.errorJson("Ошибка сервера: " + e.getMessage()));
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private static class BouquetRequest {
        public String name;
        public String description;
        public BigDecimal price;
    }
}

