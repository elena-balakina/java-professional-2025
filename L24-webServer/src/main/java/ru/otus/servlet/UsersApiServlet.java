package ru.otus.servlet;

import com.google.gson.Gson;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import ru.otus.dao.UserDao;
import ru.otus.model.User;

@SuppressWarnings({"java:S1989"})
public class UsersApiServlet extends HttpServlet {

    private static final int ID_PATH_PARAM_POSITION = 1;

    private final transient UserDao userDao;
    private final transient Gson gson;

    public UsersApiServlet(UserDao userDao, Gson gson) {
        this.userDao = userDao;
        this.gson = gson;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        ServletOutputStream out = response.getOutputStream();

        Optional<Long> id = extractIdFromRequest(request);
        if (id.isEmpty()) {
            out.print(gson.toJson(userDao.findAllOrderByIdDesc()));
        } else {
            User user = userDao.findById(id.get()).orElse(null);
            out.print(gson.toJson(user));
        }
    }

    private Optional<Long> extractIdFromRequest(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.isBlank()) {
            return Optional.empty();
        }
        String[] path = pathInfo.split("/");
        if (path.length <= ID_PATH_PARAM_POSITION || path[ID_PATH_PARAM_POSITION].isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(Long.parseLong(path[ID_PATH_PARAM_POSITION]));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
