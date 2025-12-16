package server;

import api.AdminService;
import api.ClientService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Сервлет для авторизации на сервере (Tomcat)
 */
public class AuthServlet extends HttpServlet {
    private final AdminService adminService = new AdminService();
    private final ClientService clientService = new ClientService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        PrintWriter out = resp.getWriter();

        try {
            String json = JsonHelper.readJsonFromRequest(req.getReader());
            LoginRequest request = JsonHelper.fromJson(json, LoginRequest.class);

            // Проверяем администратора
            if (adminService.login(request.login, request.password)) {
                out.print("{\"success\":true,\"userType\":\"admin\"}");
                resp.setStatus(HttpServletResponse.SC_OK);
                return;
            }

            // Проверяем клиента
            Client client = clientService.login(request.login, request.password);
            if (client != null) {
                out.print("{\"success\":true,\"userType\":\"client\",\"clientId\":" + client.getId() + "}");
                resp.setStatus(HttpServletResponse.SC_OK);
                return;
            }

            // Неверный логин или пароль
            out.print("{\"success\":false,\"error\":\"Неверный логин или пароль\"}");
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } catch (Exception e) {
            e.printStackTrace();
            out.print(JsonHelper.errorJson("Ошибка сервера: " + e.getMessage()));
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private static class LoginRequest {
        public String login;
        public String password;
    }
}

