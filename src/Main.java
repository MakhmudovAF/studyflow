import studyflow.model.Priority;
import studyflow.model.Task;
import studyflow.model.TaskStatus;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        Task task = new Task(
                1,
                "Изучить Collections",
                "Разобраться с List, Set и Map",
                TaskStatus.TODO,
                Priority.HIGH,
                LocalDate.of(2026, 11, 5)
        );

        System.out.println(task);
    }
}