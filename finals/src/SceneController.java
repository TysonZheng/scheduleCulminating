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

    public static void main() throws IOException{
        Timer timer = new Timer();
        timer.schedule(new TimerTask(){
            public void run(){
                //Gets the second every second
                LocalTime time = LocalTime.now();
                DateTimeFormatter second= DateTimeFormatter.ofPattern("ss");
                String secondsInString = time.format(second);
                int secondsDueDate = Integer.parseInt(secondsInString);
                //Only gets the date when new minute ()
                if (secondsDueDate%60 == 0){
                    javaTimer();
                }
            }  
        }, 0, 1000);
        //fileInput(), fileOutput() and all alerts will happen when announcement will be submitted
        fileInput();
        fileOutput();
        //Needs input dueDateInput as an int[] as {Year, Month, Day, Hour, Minute} of the due date. 
        //halfDateAlertGenerator(dueDateInput);
    }

    public static int[] javaTimer(){
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
        int[] systemTime = {yearsDueDate, monthsDueDate, daysDueDate, hoursDueDate, minutesDueDate};
        return systemTime;
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
    public static void fileOutput() throws IOException{
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
            File scheduleFile = new File("schedule.csv");
            scheduleFile.createNewFile();
        }        
    }

    public static int[] halfDateAlertGenerator(int dueDateInput[]){
        //systemTime = {Year, month, day, hour, minute}
        int[] systemTime = javaTimer();
        //Calculations
        long minuteTotal = (((((dueDateInput[1]+((dueDateInput[0] - systemTime[0])*12)) - systemTime[1])*30))*43200);
        if(dueDateInput[2]>systemTime[1]){
            minuteTotal += ((dueDateInput[2] - systemTime[2])*1440);
        }
        else{
            minuteTotal += (((30-systemTime[2])+dueDateInput[2])*1440);
        }
        
        if (dueDateInput[3] > systemTime[3]){
            minuteTotal += ((dueDateInput[3] - systemTime[3])*60);
        }
        else{
            minuteTotal += (((24-systemTime[3]) + dueDateInput[3])*60); 
        }
        
        if(dueDateInput[4]> minuteTotal){
            minuteTotal += (dueDateInput[4] - minuteTotal);
        }
        else{
            minuteTotal += (((60 - systemTime[4]) + dueDateInput[4]-60));
        }
        long halfAlertTimer=minuteTotal/2;
        long halfAlertYears= (halfAlertTimer/525600);
        long halfAlertMonth = halfAlertTimer/ 43800 - (halfAlertYears*12); 
        long halfAlertDay = halfAlertTimer/1440 - (halfAlertMonth*30);
        long halfAlertHour = halfAlertTimer/24 - (halfAlertDay*24);
        long halfAlertMinute = halfAlertTimer- (halfAlertHour*60);
        //Returned values
        int returnedYear = systemTime[0] + (int)halfAlertYears;
        int returnedMonth = systemTime[1] + (int)halfAlertMonth;
        int returnedDay = systemTime[2] + (int)halfAlertDay;
        int returnedHour = systemTime[3] + (int)halfAlertHour;
        int returnedMinute = systemTime[4] + (int)halfAlertMinute;
        if (returnedMinute>=60){
            returnedMinute = returnedMinute%60;
            returnedHour = returnedMinute/60;
        }
        //Returns as {Year, Month, Day, Hour, Minute}
        int[] returnedArrayDate = {returnedYear, returnedMonth, returnedDay, returnedHour, returnedMinute};
        return returnedArrayDate;
    }

    public static int[]  breakNotification(){
        int[] systemTimer = javaTimer();
        int breakTimeMinute = systemTimer[4]+25;
        int breakTimeHour = 0;
        int breakTimeDay = 0;
        int breakTimeMonth = 0;
        int breakTimeYear = 0;
        if (breakTimeMinute >= 60){
            breakTimeHour = systemTimer[3] + 1;
            breakTimeMinute = breakTimeMinute%60;
        }
        else{
            breakTimeHour = systemTimer[3];
        }
        if (breakTimeHour >=24){
            breakTimeDay = systemTimer[2]+1;
            breakTimeHour= systemTimer[3]%24;
        }
        else{
            breakTimeDay = systemTimer[2];
        }
        if (breakTimeDay>30){
            breakTimeDay = breakTimeDay%30;
            breakTimeMonth = systemTimer[1]+1;
        }
        else{ 
            breakTimeDay = systemTimer[2];

        }
        if (breakTimeMonth>12){
            breakTimeMonth = breakTimeMonth%12;
            breakTimeYear = systemTimer[0]+1;
        }
        else{ 
            breakTimeYear = systemTimer[0];
        }
        int[] breakTimeDate = {breakTimeYear, breakTimeMonth, breakTimeDay, breakTimeHour, breakTimeMinute};
        return breakTimeDate;

    }
    public static void exerciseNotification(){

    }
}
