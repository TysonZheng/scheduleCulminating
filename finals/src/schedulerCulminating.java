import javafx.application.Application;
import javafx.scene.*;
import javafx.stage.*;
import javafx.fxml.FXMLLoader;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class schedulerCulminating extends Application {
    private static String task, day, month, year;

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
        fileInput();
        fileOutput();
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
    public static void fileInput(){
        
        //SCANNER INPUT IS TEMPORARY SINCE IT WILL BE IMPLEMENTED INTO GUI
        Scanner reader = new Scanner(System.in);
        System.out.println("Enter the task name");
        task = reader.nextLine();
        System.out.println("Enter the Day (Integer)");
        day = reader.nextLine();
        System.out.println("Enter the month (Integer)");
        month = reader.nextLine();
        System.out.println("Enter the year (Integer)");
        year = reader.nextLine();
        reader.close();
    }
    public static void fileOutput(){
        String filePath = "schedule.csv";
        try {
            FileWriter fw = new FileWriter(filePath, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);
            pw.append(task);
            pw.append(":");
            pw.append(day);
            pw.append("-");
            pw.append(month);
            pw.append("-");
            pw.append(year);
            pw.append("\n");
            pw.flush();
            pw.close();

            System.out.println("ELEMENTS ADDED TO FILE");
            
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
}
