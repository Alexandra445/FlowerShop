package server;

import api.AdminService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Сервлет для работы с администраторами на сервере (Tomcat)
 */
public class AdminServlet extends HttpServlet {
    private final AdminService adminService = new AdminService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        PrintWriter out = resp.getWriter();

        try {
            java.util.List<Administrator> admins = adminService.getAll();
            out.print(JsonHelper.toJson(admins));
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
            AdminRequest request = JsonHelper.fromJson(json, AdminRequest.class);

            boolean success = adminService.add(request.fullName, request.phone, request.login, request.password);
            
            if (success) {
                out.print(JsonHelper.successJson("Администратор успешно добавлен"));
                resp.setStatus(HttpServletResponse.SC_CREATED);
            } else {
                out.print(JsonHelper.errorJson("Ошибка при добавлении администратора"));
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.print(JsonHelper.errorJson("Ошибка сервера: " + e.getMessage()));
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private static class AdminRequest {
        public String fullName;
        public String phone;
        public String login;
        public String password;
    }
}

