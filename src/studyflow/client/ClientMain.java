package studyflow.client;

public class ClientMain {
    public static void main(String[] args) {
        StudyFlowClient client = new StudyFlowClient("http://localhost:8080");
        ClientMenu menu = new ClientMenu(client);

        menu.start();
    }
}