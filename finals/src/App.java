import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.image.Image;
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
import java.time.LocalDateTime;
import java.time.ZoneId;

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
        halfDateAlertGenerator(dueDateInput);
        tenminutegenerator(dueDateInput);

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
    public static void fileReader() throws IOException{
        Scanner fileReader;
        Scanner reader = new Scanner(System.in);
        String filePath = "schedule.csv";
        boolean found = false;
        String foundTask = "", foundTime = "", foundDay = "", foundMonth = "", foundYear ="";
        String searchTask;

        System.out.println("Enter the task you're looking for");
        searchTask = reader.nextLine();

        try {
            fileReader = new Scanner(new File(filePath));
            fileReader.useDelimiter("[:\n-]");

            while (fileReader.hasNext() && !found){
                foundTask = fileReader.next();
                foundTime = fileReader.next();
                foundDay = fileReader.next();
                foundMonth = fileReader.next();
                foundYear = fileReader.next();

                if (foundTask.equals(searchTask)) {
                    found = true;
                }
            }
            if (found) {
                System.out.println("Task Name: " + foundTask + " Due Time: " + foundTime + " Day: " + foundDay + " Month: " + foundMonth + " Year: " + foundYear);
            }
            else {
                System.out.println("TASK NOT FOUND");
            }
        } catch (Exception e) {
            System.out.println("ERROR");
        }
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
    public static boolean tenminutegenerator(int dueDateInput[]){

        int[] systemTime= javaTimer();  //Loads the local system time into an array int[] systemTime = {yearsDueDate, monthsDueDate, daysDueDate, hoursDueDate, minutesDueDate};

        DateTimeFormatter standard = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");  //date formatter

        String systemtimedate = systemTime[0] + "/" + systemTime[1] + "/" + systemTime[2] + " " + systemTime[3] + ":" + systemTime[4];
        LocalDateTime localsystDateTime = LocalDateTime.parse(systemtimedate, standard); //return date time

        String duedatetimedate = dueDateInput[0] + "/" + dueDateInput[1] + "/" + dueDateInput[2] + " " + dueDateInput[3] + ":" + dueDateInput[4];
        LocalDateTime localduedatetimedate = LocalDateTime.parse(systemtimedate, standard); //return  due date time
        
        long systemmilli = localsystDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(); //Take local date time add to zone and convert to epochmilli

        long systemduedate = localduedatetimedate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(); //Take due date time add to zone and convert to epochmilli

        long compare = systemduedate - systemmilli;    //compare the system due date in milliseconds then subtract it with local time. 

        if(compare <= 600000){  //600000 miliseconds = 10 minutes
            return true; //if it equal to ten minutes return true

        } else {
            return false;  //else return false
 
        }
        
    }
    
    public void exerciseNotification() {
        TimerTask repeatedTask = new TimerTask() {
            public void run() {
                //Add Exercise to Task
            }
        };
        Timer timer = new Timer("Timer");
        
        long delay = 1000L;
        long period = 1000L * 60L * 60L * 24L;
        timer.scheduleAtFixedRate(repeatedTask, delay, period);
    }
}
