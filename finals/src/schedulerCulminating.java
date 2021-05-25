import javafx.application.Application;
import javafx.scene.*;
import javafx.stage.*;
import javafx.fxml.FXMLLoader;

public class schedulerCulminating extends Application {
    

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        primaryStage.setTitle("Student Reminder App");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
    
    public static void main(String[] args) throws Exception {
        
        launch(args);
    }   
}