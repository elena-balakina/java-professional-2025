package ru.otus.servlet;

import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import ru.otus.dao.ClientDao;
import ru.otus.services.TemplateProcessor;

public class UsersServlet extends HttpServlet {

    private static final String USERS_PAGE_TEMPLATE = "users.html";

    private final transient TemplateProcessor templateProcessor;
    private final transient ClientDao clientDao;

    public UsersServlet(TemplateProcessor templateProcessor, ClientDao clientDao) {
        this.templateProcessor = templateProcessor;
        this.clientDao = clientDao;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("clients", clientDao.findAllOrderByIdDesc());

        resp.setContentType("text/html;charset=UTF-8");
        resp.getWriter().println(templateProcessor.getPage(USERS_PAGE_TEMPLATE, params));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = req.getParameter("name");
        String street = req.getParameter("street");
        String phone = req.getParameter("phone");

        if (name != null && !name.isBlank() && street != null && !street.isBlank()) {
            clientDao.create(name.trim(), street.trim(), phone == null ? "" : phone);
        }
        resp.sendRedirect(req.getContextPath() + "/users");
    }
}
