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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.time.ZoneId;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;

public class App extends Application {

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
        
        //fileOutput();
        //Needs input dueDateInput as an int[] as {Year, Month, Day, Hour, Minute} of the due date. 
        String[] dueDateInput = fileReader();
        
        fileWriter(dueDateInput);
        //tenminutegenerator();
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
    public static String fileSearcher(String searchTask) throws IOException{
        Scanner fileReader;
        Scanner reader = new Scanner(System.in);
        String filePath = "halfalerts.csv";
        boolean found = false;
        String foundTask = "", foundDay = "", foundMonth = "", foundYear ="", foundHour = "", foundMinute = "";
        String foundInformation = "";

        try {
            fileReader = new Scanner(new File(filePath));
            fileReader.useDelimiter("[:\n-]");

            while (fileReader.hasNext() && !found){
                foundTask = fileReader.next();
                foundDay = fileReader.next();
                foundMonth = fileReader.next();
                foundYear = fileReader.next();
                foundHour = fileReader.next();
                foundMinute = fileReader.next();

                if (foundTask.equals(searchTask)) {
                    found = true;
                }
            }
            if (found) {
                System.out.println("The Task " + foundTask + " is due on: " + foundYear + "-" + foundMonth + "-" + foundDay + " at " + foundHour + ":" + foundMinute);
                foundInformation = foundTask+":"+foundYear+"-"+foundMonth+"-"+foundDay+"-"+foundHour+":"+foundMinute;
                
                //Returns as Task Name:Year-Month-Day-Hour:Minute
            }
            else {
                System.out.println("TASK NOT FOUND. MAKE SURE YOU INPUTTED THE RIGHT TASK NAME AND THERE ARE ENTRIES IN THE FILE");
                
            }
        } catch (Exception e) {
            System.out.println("ERROR");
        }
        reader.close();
        return foundInformation;
    }
    public static int countLines() {

        int lines = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader("schedule.csv"))) {
            while (reader.readLine() != null) lines++;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
  
    }
    public static String[] fileReader() throws IOException {
        String line = "";
        int numOfLines = countLines();
        String task[] = new String[numOfLines];
        int counter = 0;
        try {
            BufferedReader csvReader = new BufferedReader(new FileReader("schedule.csv"));
            while ((line = csvReader.readLine()) != null) {
                task[counter] = line;
                counter++;
            }
            csvReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return task;
    }
    public static void fileWriter(String[] dueDateCSV) throws IOException {
        String[] arr = halfDateAlertGenerator(dueDateCSV);
        File filePath = new File("halfalerts.csv");
        FileWriter fw = new FileWriter(filePath, true);
        try {
            for (int i = 0; i < arr.length; i++) {
                System.out.println(arr[i]);
                fw.append(arr[i]);
                fw.append("\n");
                System.out.println("Element " + i + " added to csv");
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
        fw.close();
    }

    public static String halfAlertCalculations(int[]dueDateInput, String taskName){
        //systemTime = {Year, month, day, hour, minute}
        int[] systemTime = javaTimer();
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
        //Returns as Task Name:Year-Month-Day-Hour:Minute
        String returnedArrayDate = taskName +":" + String.valueOf((returnedYear)) + "-"+String.valueOf((returnedMonth))+"-"+String.valueOf((returnedDay))+"-"+String.valueOf((returnedHour))+":"+String.valueOf((returnedMinute));
        return returnedArrayDate;
    }
    public static String[] halfDateAlertGenerator(String[] dueDateCSV){
        String[] halfTimeArray = new String[0];
        for (int i =0; i<dueDateCSV.length; i++){
            //Calculations
            String newDate = dueDateCSV[i];
            String[] initialSplit = newDate.split("-"); //[1]= Month, [2] = Day 
            String[] taskNameSplit = initialSplit[0].split(":"); //[0] = Task name, [1] = Year
            String[] timeSplit = initialSplit[3].split(":");//[0] = Hour, [1] = Minute
            int year = (int) Long.parseLong(taskNameSplit[1]);
            int month = (int) Long.parseLong(initialSplit[1]);
            int day = (int)Long.parseLong(initialSplit[2]);
            int hour = (int)Long.parseLong(timeSplit[0]);
            int minute = (int)Long.parseLong(timeSplit[1]);
            int[] dueDateFormatted = {year,month,day,hour,minute};
            String halfDue = halfAlertCalculations(dueDateFormatted, taskNameSplit[0]);
            halfTimeArray = Arrays.copyOf(halfTimeArray, halfTimeArray.length + 1);
            halfTimeArray[halfTimeArray.length - 1] = halfDue;    
        }
        return halfTimeArray;
        
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
        
        long delay = 0;
        long period = 1000 * 60 * 60 * 24;
        timer.scheduleAtFixedRate(repeatedTask, delay, period);
    }

    public static void breakNotification(){
        TimerTask repeatedTask = new TimerTask() {
            public void run() {
                //Displays the notication
            }
        };
        Timer timer = new Timer("Timer");
        
        long delay = 25*60*1000;
        long period = 30*60*1000;
        timer.scheduleAtFixedRate(repeatedTask, delay, period);

    }
    public static void compareMethods(){
        
    }
}
