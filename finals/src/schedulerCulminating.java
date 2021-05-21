import javafx.application.Application;
import javafx.scene.*;
import javafx.stage.*;
import javafx.fxml.FXMLLoader;
import java.time.LocalDate;

public class schedulerCulminating extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        primaryStage.setTitle("Student Reminder App");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
    
    public static void main(String[] args) throws Exception {
        javaTimer();
        launch(args);
    }
    public static void javaTimer(){
        LocalDate date = LocalDate.now();
        System.out.println(date); 
    }
}
