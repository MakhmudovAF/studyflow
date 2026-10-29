import studyflow.model.Priority;
import studyflow.model.TaskStatus;
import studyflow.repository.TaskRepository;
import studyflow.service.TaskService;

import java.time.LocalDate;

public class Main {

    public static void main(String[] args) {
        TaskService service = new TaskService(new TaskRepository());

        service.createTask("Изучить Collections", "List, Set и Map", Priority.HIGH, LocalDate.now().plusDays(2));
        service.createTask("Изучить Stream API", "filter и sorted", Priority.HIGH, LocalDate.now().plusDays(5));
        service.createTask("Изучить HTTP", "HttpServer и HttpClient", Priority.MEDIUM, LocalDate.now().plusDays(10));
        service.createTask("Старая задача", "Просроченная задача", Priority.LOW, LocalDate.now().minusDays(3));

        service.updateStatus(2L, TaskStatus.IN_PROGRESS);
        service.updateStatus(3L, TaskStatus.DONE);

        System.out.println("Все задачи:");
        service.getAllTasks().forEach(System.out::println);

        System.out.println("\nHIGH:");
        service.getTasksByPriority(Priority.HIGH).forEach(System.out::println);

        System.out.println("\nПо дедлайну:");
        service.getTasksSortedByDeadline().forEach(System.out::println);

        System.out.println("\nПросроченные:");
        service.getOverdueTasks().forEach(System.out::println);

        System.out.println("\nПоиск \"stream\":");
        service.searchByTitle("stream").forEach(System.out::println);

        System.out.println("\nКоличество выполненных: " + service.getCompletedTaskCount());
        System.out.println("Процент выполнения: " + service.getCompletionPercentage());

        System.out.println("\nКоличество по статусам:");
        service.countTasksByStatus().forEach((status, count) -> System.out.println(status + ": " + count));
    }
}