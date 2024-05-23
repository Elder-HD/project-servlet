package com.tictactoe;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "LogicServlet", value = "/logic")
public class LogicServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Отримуємо поточну сесію
        HttpSession currentSession = req.getSession();

        // Отримуємо об'єкт ігрового поля з сесії
        Field field = extractField(currentSession);

        // Отримуємо індекс ячейки, на яку відбувся клік
        int index = getSelectedIndex(req);
        Sign currentSign = field.getField().get(index);

        if (currentSign != Sign.EMPTY) {
//            getServletContext().getRequestDispatcher("/index.jsp").forward(req, resp);
            resp.sendRedirect("/index.jsp");
            return;
        }

        // Ставимо хрестик в ячейці, на яку клікнув користувач
        field.getField().put(index, Sign.CROSS);

        // Перевіряємо, чи не переміг хрестик після додавання останнього кліку користувача
        if (checkWin(resp, currentSession, field)) {
            return;
        }

        // Отримуємо порожню ячейку поля
        int emptyFieldIndex = field.getEmptyFieldIndex();

        if (emptyFieldIndex >= 0) {
            field.getField().put(emptyFieldIndex, Sign.NOUGHT);
            // Перевіряємо, чи не переміг хрестик після додавання останнього кліку користувача
            if (checkWin(resp, currentSession, field)) {
                return;
            }
        }
        // Якщо порожньої ячейки нема і ніхто не переміг – це нічия
        else {
            // Додаємо до сесії прапорець, який сигналізує, що відбулася нічия
            currentSession.setAttribute("draw", true);

            // Рахуємо список значків
            List<Sign> data = field.getFieldData();

            // Оновлюємо цей список у сесії
            currentSession.setAttribute("data", data);

            // Шлемо редирект
            resp.sendRedirect("/finish.jsp");
            return;
        }

        // Рахуємо список значків
        List<Sign> data = field.getFieldData();

        // Оновлюємо об'єкт поля і список значків у сесії
        currentSession.setAttribute("data", data);
        currentSession.setAttribute("field", field);

        resp.sendRedirect("/index.jsp");
    }

    private int getSelectedIndex(HttpServletRequest req) {
        String click = req.getParameter("click");
        boolean isNumeric = click.matches("^\\d+$");
        return isNumeric ? Integer.parseInt(click) : 0;
    }

    private Field extractField(HttpSession currentSession) {
        Object fieldAttribute = currentSession.getAttribute("field");
        if (Field.class != fieldAttribute.getClass()) {
            currentSession.invalidate();
            throw new RuntimeException("Session is broken, try one more time");
        }
        return (Field) fieldAttribute;
    }

    private boolean checkWin(HttpServletResponse response, HttpSession currentSession, Field field) throws IOException {
        Sign winner = field.checkWin();
        if (Sign.CROSS == winner || Sign.NOUGHT == winner) {
            // Додаємо прапорець, який показує, що хтось переміг
            currentSession.setAttribute("winner", winner);

            // Рахуємо список значків
            List<Sign> data = field.getFieldData();

            // Оновлюємо цей список у сесії
            currentSession.setAttribute("data", data);

            // Шлемо редирект
            response.sendRedirect("/finish.jsp");
            return true;
        }
        return false;
    }
}
