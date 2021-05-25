import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
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

public class SceneController {
    
    private Stage stage;
    private Scene scene;
    private Parent root;
    private static String task, day, month, year;

    public void switchScene1(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("main.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchScene2(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("due.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(){
        Timer timer = new Timer();
        timer.schedule(new TimerTask(){
            public void run(){
                javaTimer();
                fileInput();
                fileOutput();
            }  
        }, 0, 1000);
        //Needs input dueDateInput as an int[] as {Year, Month, Day, Hour, Minute} of the due date. 
        halfDateAlertGenerator(dueDateInput);
    }

    public static void javaTimer(){
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

    public static int[] halfDateAlertGenerator(int dueDateInput[]){
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
        System.out.println("Minute:" +minutesDueDate);
        System.out.println("Hour: " +hoursDueDate);
        System.out.println("Day:"+daysDueDate);
        System.out.println("Month:" +monthsDueDate);
        System.out.println("Year: "+yearsDueDate);
        //Calculations
        long minuteTotal = (((((dueDateInput[1]+((dueDateInput[0] - yearsDueDate)*12)) - monthsDueDate)*30))*43200);
        if(dueDateInput[2]>monthsDueDate){
            minuteTotal += ((dueDateInput[2] - daysDueDate)*1440);
        }
        else{
            minuteTotal += (((30-daysDueDate)+dueDateInput[2])*1440);
        }
        
        if (dueDateInput[3] > hoursDueDate){
            minuteTotal += ((dueDateInput[3] - hoursDueDate)*60);
        }
        else{
            minuteTotal += (((24-hoursDueDate) + dueDateInput[3])*60); 
        }
        
        if(dueDateInput[4]> minuteTotal){
            minuteTotal += (dueDateInput[4] - minuteTotal);
        }
        else{
            minuteTotal += (((60 - minutesDueDate) + dueDateInput[4]-60));
        }
        long halfAlertTimer=minuteTotal/2;
        long halfAlertYears= (halfAlertTimer/525600);
        long halfAlertMonth = halfAlertTimer/ 43800 - (halfAlertYears*12); 
        long halfAlertDay = halfAlertTimer/1440 - (halfAlertMonth*30);
        long halfAlertHour = halfAlertTimer/24 - (halfAlertDay*24);
        long halfAlertMinute = halfAlertTimer- (halfAlertHour*60);
        //Returned values
        int returnedYear = yearsDueDate + (int)halfAlertYears;
        int returnedMonth = monthsDueDate + (int)halfAlertMonth;
        int returnedDay = daysDueDate + (int)halfAlertDay;
        int returnedHour = hoursDueDate + (int)halfAlertHour;
        int returnedMinute = minutesDueDate + (int)halfAlertMinute;
        if (returnedMinute>=60){
            returnedMinute = returnedMinute%60;
            returnedHour = returnedMinute/60;
        }
        //Returns as {Year, Month, Day, Hour, Minute}
        int[] returnedArrayDate = {returnedYear, returnedMonth, returnedDay, returnedHour, returnedMinute};
        return returnedArrayDate;
    }
}
