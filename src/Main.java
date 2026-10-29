import studyflow.model.Priority;
import studyflow.model.Task;
import studyflow.model.TaskStatus;
import studyflow.repository.TaskRepository;

import java.time.LocalDate;

public class Main {

    public static void main(String[] args) {
        TaskRepository repository = new TaskRepository();

        Task collectionsTask = new Task(1, "Изучить Collections", "List, Set и Map", TaskStatus.TODO, Priority.HIGH,
                LocalDate.of(2026, 11, 5));

        Task streamsTask = new Task(2, "Изучить Stream API", "filter, map и sorted", TaskStatus.TODO, Priority.MEDIUM,
                LocalDate.of(2026, 11, 10));

        repository.save(collectionsTask);
        repository.save(streamsTask);

        System.out.println("Все задачи:");
        repository.findAll().forEach(System.out::println);

        System.out.println();
        System.out.println("Задача с id = 1:");
        repository.findById(1L).ifPresent(System.out::println);

        System.out.println();
        System.out.println("Количество задач: " + repository.size());

        repository.deleteById(1L);

        System.out.println();
        System.out.println("После удаления:");
        repository.findAll().forEach(System.out::println);
    }
}