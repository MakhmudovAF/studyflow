package studyflow.exception;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(long id) {
        super("Задача с id " + id + " не найдена");
    }
}