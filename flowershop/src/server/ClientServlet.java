package server;

import api.ClientService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Сервлет для работы с клиентами на сервере (Tomcat)
 */
public class ClientServlet extends HttpServlet {
    private final ClientService clientService = new ClientService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        PrintWriter out = resp.getWriter();

        try {
            java.util.List<Client> clients = clientService.getAll();
            out.print(JsonHelper.toJson(clients));
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
            ClientRequest request = JsonHelper.fromJson(json, ClientRequest.class);

            boolean success = clientService.add(request.fullName, request.phone, request.login, request.password);
            
            if (success) {
                out.print(JsonHelper.successJson("Клиент успешно добавлен"));
                resp.setStatus(HttpServletResponse.SC_CREATED);
            } else {
                out.print(JsonHelper.errorJson("Ошибка при добавлении клиента"));
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.print(JsonHelper.errorJson("Ошибка сервера: " + e.getMessage()));
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private static class ClientRequest {
        public String fullName;
        public String phone;
        public String login;
        public String password;
    }
}

