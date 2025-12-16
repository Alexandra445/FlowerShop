package server;

import api.OrderStatusService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Сервлет для работы со статусами заказов на сервере (Tomcat)
 */
public class OrderStatusServlet extends HttpServlet {
    private final OrderStatusService statusService = new OrderStatusService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        PrintWriter out = resp.getWriter();

        try {
            java.util.List<OrderStatus> statuses = statusService.getAll();
            out.print(JsonHelper.toJson(statuses));
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            e.printStackTrace();
            out.print(JsonHelper.errorJson("Ошибка сервера: " + e.getMessage()));
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}

