package ru.otus.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import ru.otus.dao.UserDao;
import ru.otus.services.TemplateProcessor;

@SuppressWarnings({"java:S1989"})
public class UsersServlet extends HttpServlet {

    private static final String USERS_PAGE_TEMPLATE = "users.html";

    private final transient UserDao userDao;
    private final transient TemplateProcessor templateProcessor;

    public UsersServlet(TemplateProcessor templateProcessor, UserDao userDao) {
        this.templateProcessor = templateProcessor;
        this.userDao = userDao;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("users", userDao.findAllOrderByIdDesc());

        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println(templateProcessor.getPage(USERS_PAGE_TEMPLATE, params));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = req.getParameter("name");
        String login = req.getParameter("login");
        String password = req.getParameter("password");

        if (name != null
                && login != null
                && password != null
                && !name.isBlank()
                && !login.isBlank()
                && !password.isBlank()) {
            userDao.create(name.trim(), login.trim(), password);
        }
        resp.sendRedirect(req.getContextPath() + "/users");
    }
}
