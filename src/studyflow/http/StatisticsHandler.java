package studyflow.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import studyflow.dto.TaskStatistics;
import studyflow.service.StatisticsService;
import studyflow.util.JsonUtil;

import java.io.IOException;

public class StatisticsHandler implements HttpHandler {

    private final StatisticsService statisticsService;

    public StatisticsHandler(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.getResponseHeaders().set("Allow", "GET");
                HttpResponseUtil.sendJson(exchange, 405, JsonUtil.errorToJson("Метод не поддерживается"));
                return;
            }

            String path = exchange.getRequestURI().getPath();

            if (!"/statistics".equals(path) && !"/statistics/".equals(path)) {
                HttpResponseUtil.sendJson(exchange, 404, JsonUtil.errorToJson("Ресурс не найден"));
                return;
            }

            TaskStatistics statistics = statisticsService.getStatistics();

            HttpResponseUtil.sendJson(exchange, 200, JsonUtil.statisticsToJson(statistics));
        } catch (Exception exception) {
            HttpExceptionHandler.handle(exchange, exception);
        }
    }
}