import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.stage.*;
import javafx.fxml.FXMLLoader;


public class App extends Application {
    private static String task, day, month, year;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        primaryStage.setTitle("Student Reminder App");

        Image icon = new Image("icon.png");
        primaryStage.getIcons().add(icon);

        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
    public static void main(String[] args) throws Exception {
        launch(args);
    }
}
