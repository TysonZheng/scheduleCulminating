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
        //Needs input of the due date of a task
        //dataDateInput[] = {Year: Month: Day: Hour: Minute} 
        javaTimer();
        timeAlerts(dueDateInput);
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
    public static void timeAlerts(int[] dueDateInput){
        //dataDateInput[] = {Year: Month: Day: Hour: Minute}
        Timer internalClock = new Timer();
        internalClock.schedule(new TimerTask(){
            public void run(){
                LocalTime time = LocalTime.now();
                DateTimeFormatter minutes = DateTimeFormatter.ofPattern("mm");
                String minuteInString = time.format(minutes);
                int minutesDueDate = Integer.parseInt(minuteInString);
                DateTimeFormatter hour = DateTimeFormatter.ofPattern("HH");
                String hourInString = time.format(hour);
                int hoursDueDate = Integer.parseInt(hourInString);
                LocalDate date = LocalDate.now();
                DateTimeFormatter day = DateTimeFormatter.ofPattern("dd");
                String dayInString = date.format(day);
                int daysDueDate = Integer.parseInt(dayInString);
                DateTimeFormatter month = DateTimeFormatter.ofPattern("MM");
                String monthInString = date.format(month);
                int monthsDueDate = Integer.parseInt(monthInString);
                DateTimeFormatter year = DateTimeFormatter.ofPattern("yyyy");
                String yearsInString = date.format(year);
                int yearsDueDate = Integer.parseInt(yearsInString);

            }  
        }, 0, 60*1000);
        
    }  
}
