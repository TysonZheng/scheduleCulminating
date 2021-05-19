import javafx.application.Application;
import javafx.scene.*;
import javafx.stage.*;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.paint.Color;
import javafx.scene.image.*;

public class App extends Application implements EventHandler<ActionEvent>{

    Stage window;
    Button button;

    public static void main(String[] args) throws Exception {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        
        Group root = new Group();
        Scene scene = new Scene(root, Color.PINK);
        
        Image icon = new Image("icon.png");
        stage.getIcons().add(icon);
        stage.setTitle("Student Reminder App");

        stage.setWidth(1500);
        stage.setHeight(900);

        stage.setScene(scene);
        stage.show();

    }

    @Override
    public void handle(ActionEvent event) {
        // if(event.getSource())
    }
}