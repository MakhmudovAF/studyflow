package studyflow.http;

import com.sun.net.httpserver.HttpExchange;
import studyflow.exception.TaskNotFoundException;
import studyflow.exception.ValidationException;
import studyflow.util.JsonUtil;

import java.io.IOException;

public final class HttpExceptionHandler {

    private HttpExceptionHandler() {
    }

    public static void handle(HttpExchange exchange, Exception exception) throws IOException {
        if (exception instanceof TaskNotFoundException) {
            HttpResponseUtil.sendJson(
                    exchange,
                    404,
                    JsonUtil.errorToJson(exception.getMessage())
            );
            return;
        }

        if (exception instanceof ValidationException) {
            HttpResponseUtil.sendJson(
                    exchange,
                    400,
                    JsonUtil.errorToJson(exception.getMessage())
            );
            return;
        }

        exception.printStackTrace();

        HttpResponseUtil.sendJson(
                exchange,
                500,
                JsonUtil.errorToJson("Внутренняя ошибка сервера")
        );
    }
}