import javafx.application.Application;
import javafx.scene.*;
import javafx.stage.*;
import javafx.fxml.FXMLLoader;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

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
        Timer timer = new Timer();
        timer.schedule(new TimerTask(){
            public void run(){
                LocalTime time = LocalTime.now();
                LocalDate date = LocalDate.now();
                DateTimeFormatter displayFormat = DateTimeFormatter.ofPattern("HH:mm");
                DateTimeFormatter seconds = DateTimeFormatter.ofPattern("ss");
                String formattedDate = time.format(displayFormat);
                String secondIString = time.format(seconds);
                int forNewMinute = Integer.parseInt(secondIString);
                if ((forNewMinute%60) ==0){
                    System.out.println(date);
                    System.out.println(formattedDate);
                }
            }  
        }, 0, 1000);
    }
   
}
