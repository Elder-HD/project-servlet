package com.tictactoe;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;


@WebServlet(name = "InitServlet", value = "/start")
public class InitServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Створення нової сесії
        req.getSession().invalidate();
        HttpSession currentSession = req.getSession(true);

        // Створення ігрового поля поля
        Field field = new Field();

        // Отримання списку значень поля
        List<Sign> data = field.getFieldData();

        // Додавання до сесії параметрів поля (буде потрібно для зберігання стану між запитами)
        currentSession.setAttribute("field", field);
        // і значень поля, відсортованих за індексом (потрібно для промалювання хрестиків і нуликів)
        currentSession.setAttribute("data", data);

        // Перенаправлення запиту на сторінку index.jsp через сервер
        getServletContext().getRequestDispatcher("/index.jsp").forward(req, resp);
    }
}
