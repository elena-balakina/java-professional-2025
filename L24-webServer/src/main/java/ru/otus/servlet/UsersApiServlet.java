package ru.otus.servlet;

import com.google.gson.Gson;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.Optional;
import ru.otus.dao.ClientDao;
import ru.otus.model.Client;

public class UsersApiServlet extends HttpServlet {

    private static final int ID_PATH_PARAM_POSITION = 1;

    private final transient ClientDao clientDao;
    private final transient Gson gson;

    public UsersApiServlet(ClientDao clientDao, Gson gson) {
        this.clientDao = clientDao;
        this.gson = gson;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        ServletOutputStream out = response.getOutputStream();

        Optional<Long> id = extractIdFromRequest(request);
        if (id.isEmpty()) {
            out.print(gson.toJson(clientDao.findAllOrderByIdDesc()));
        } else {
            Client c = clientDao.findById(id.get()).orElse(null);
            out.print(gson.toJson(c));
        }
    }

    private Optional<Long> extractIdFromRequest(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.isBlank()) return Optional.empty();
        String[] path = pathInfo.split("/");
        if (path.length <= ID_PATH_PARAM_POSITION || path[ID_PATH_PARAM_POSITION].isBlank()) return Optional.empty();
        try {
            return Optional.of(Long.parseLong(path[ID_PATH_PARAM_POSITION]));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
