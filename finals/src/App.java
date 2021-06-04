/*
Due date: 2021/06/04
Date: 2021/06/03
Members: Kyle, Tyson, Morgan, Patrick
Teacher: Mr. Ho 
Description: Culminating project. Made a scheduling application with a display, alerts and functionality. It is meant to be able to run throughout periods of time and may be closed. 
             The user is able to input duedates into a textfield that will be addded/display when the button is pressed. 
             There is a centralized Java timer that gets the local time that the alerts use. Once there are alerts, they will be displayed in a box along the side. 
*/

//Importing Java Packages
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
import java.io.PrintWriter;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.time.ZoneId;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;

//Class 
public class App extends Application {

    //
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        primaryStage.setTitle("Student Reminder App");

        Image icon = new Image("icon.png");
        primaryStage.getIcons().add(icon);

        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
    /*
    Method name: main()
    Description: The place where specific items are called according to a timer tasks which will delay and cycle tasks. 
                 There are 2 separate timer tasks in the main method, one for exercise which needs to run on a 24 hour cycle and the other for the javaTimer and checking alerts happening every new minute.
    @author: Tyson, Kyle, Morgan, Patrick 
    @
    */
    public static void main(String[] args) throws Exception {
        //Launches the application
        launch(args);
        //Calling the fileReader{}
        String[] dueDateInput = fileReader();
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
                    try {
                        compareMethods(dueDateInput);
                        
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                int changeInLines = countLinesSchedule();
                int halfLines = countLinesHalf();
                if (changeInLines != halfLines){
                    halfDateAlertGenerator(dueDateInput);
                }
            }  
        }, 0, 1000);
        //fileInput(), fileOutput() and all alerts will happen when announcement will be submitted
        //fileOutput(); 
        halfDateWriter(dueDateInput);
        csvDuplicator();
        timer = new Timer();
        timer.schedule(new TimerTask(){
            public void run(){
                try {
                    exerciseWriter();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }  
        }, 0, 24*60*1000);
        
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
        if (daysDueDate >30){
            daysDueDate = 30;
        }
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
            }
        } catch (Exception e) {
            System.out.println("ERROR");
        }
        reader.close();
        return foundInformation;
    }
    public static int countLinesSchedule() {
        int lines = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader("schedule.csv"))) {
            while (reader.readLine() != null) lines++;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
  
    }
    public static int countLinesHalf() {
        int lines = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader("halfalerts.csv"))) {
            while (reader.readLine() != null) lines++;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
  
    }
    public static String[] fileReader() throws IOException {
        String line = "";
        int numOfLines = countLinesSchedule();
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
    public static void halfDateWriter(String[] dueDateCSV) throws IOException {
        String[] arr = halfDateAlertGenerator(dueDateCSV);
        File filePath = new File("halfalerts.csv");
        try (PrintWriter writer = new PrintWriter(filePath)){
            for (int i = 0; i < arr.length; i++) {
                StringBuilder sb = new StringBuilder();
                System.out.println(arr[i]);
                sb.append(arr[i]);
                sb.append("\n");
                writer.write(sb.toString());
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
    public static void exerciseWriter() throws IOException{
        int[] systemTime = javaTimer() ;
        String currentYear = String.valueOf(systemTime[0]);
        String currentMonth = String.valueOf(systemTime[1]);
        String currentDay = String.valueOf(systemTime[2]);
        String addExercise = "Daily exercise:"+currentYear+"-"+currentMonth+"-"+currentDay+"-" +"23:59";
        File filePath = new File("schedule.csv");
        FileWriter fw = new FileWriter(filePath, true);

        fw.write(addExercise);
        fw.write("\n");
        fw.close();
    }
    public static void csvDuplicator() throws IOException {
        String[] arr = fileReader();
        File filePath = new File("dueDates.csv");
        try (PrintWriter writer = new PrintWriter(filePath)){
            for (int i = 0; i < arr.length; i++) {
                StringBuilder sb = new StringBuilder();
                sb.append(arr[i]);
                sb.append("\n");
                writer.write(sb.toString());
            }
            System.out.println("CSV COPIED");
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
    public static String halfAlertCalculations(int[]dueDateInput, String taskName){
        //systemTime = {Year, month, day, hour, minute}
        int[] systemTime = javaTimer();
        long minuteTotal = 0;
        if ((dueDateInput[0] > systemTime[0]) && ((((dueDateInput[0] - systemTime[0])*12 - systemTime[1])+dueDateInput[1])>12)){
            minuteTotal += ((((dueDateInput[0] - systemTime[0])*12 - systemTime[1])+(dueDateInput[1]-1))*43800);
        }
        else if (dueDateInput[1]>systemTime[1]){ 
            minuteTotal+= (((dueDateInput[1]-1 -systemTime[1]))*43800);
        }
        else if (dueDateInput[0]> systemTime[0] && (((dueDateInput[0] - systemTime[0])*12 - systemTime[1])+dueDateInput[1])>1){
            minuteTotal += (((12- systemTime[1])+dueDateInput[1])*43800);
        }
        if(dueDateInput[2]>systemTime[2]&&((dueDateInput[1]-systemTime[1])==1)){
            minuteTotal += (((dueDateInput[2] - systemTime[2])+29)*1440);
        }  
        else if(dueDateInput[2]>systemTime[2]){
            minuteTotal += (((dueDateInput[2] - systemTime[2] -1))*1440);
        } 
        else if(dueDateInput[2]==systemTime[2]){
            minuteTotal +=0;
        }        
        else{
            minuteTotal += (((30-systemTime[2])+dueDateInput[2]-1)*1440);
        }    
        if (dueDateInput[3] > systemTime[3] && ((dueDateInput[2]-systemTime[2])>1)){
            minuteTotal += ((dueDateInput[3] - systemTime[3]+23)*60);
        } 
        else if (dueDateInput[3]>systemTime[3]){
            minuteTotal+=((dueDateInput[3] - systemTime[3])*60);
        }
        else if (dueDateInput[3] == systemTime[3]){
            minuteTotal+=0;
        }
        else{
            minuteTotal += (((23-systemTime[3]) + dueDateInput[3])*60); 
        }         
        if((dueDateInput[4]> systemTime[4]) &&((dueDateInput[3]-systemTime[3])>1)){
            minuteTotal += (dueDateInput[4] - systemTime[4]+60);
        }
        else if(dueDateInput[4]>systemTime[4]) {
            minuteTotal += (dueDateInput[4] - systemTime[4]);
        }
        else{
            minuteTotal += (((60 - systemTime[4]) + dueDateInput[4]));
        }
        long halfAlertTimer=minuteTotal/2;
        long halfAlertYears= (halfAlertTimer/525600);
        long halfAlertMonth = halfAlertTimer/ 43800 - (halfAlertYears*12); 
        long halfAlertDay = halfAlertTimer/1440 - (halfAlertMonth*30+(halfAlertYears*12)*30);
        long halfAlertHour = halfAlertTimer/(60) - ((halfAlertDay+halfAlertMonth*30+(halfAlertYears*12)*30)*24);
        long halfAlertMinute = halfAlertTimer- ((halfAlertHour+(halfAlertDay+halfAlertMonth*30+(halfAlertYears*12)*30)*24)*60);
        //Returned values
        int returnedYear = systemTime[0] + (int)halfAlertYears;
        int returnedMonth = systemTime[1] + (int)halfAlertMonth;
        int returnedDay = systemTime[2] + (int)halfAlertDay;
        int returnedHour = systemTime[3] + (int)halfAlertHour;
        int returnedMinute = systemTime[4] + (int)halfAlertMinute;

        if (returnedMinute>60){
            returnedHour += returnedMinute/60;
            returnedMinute = returnedMinute%60;
        }
        if(returnedHour>24){
            returnedDay += returnedHour/24;
            returnedHour = returnedHour%24;
        }
        if(returnedDay >30){
            returnedMonth += returnedDay/30;
            returnedDay = returnedDay%30;     
        }
        if(returnedMonth > 12){
            returnedYear += returnedMonth/12;
            returnedMonth = returnedMonth%12;
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
    //Calculates if there is 10mins or less left from now to the due date if it is, it will return true if not returns false. Already accounts for time zone and leap years
    public static boolean tenminutegenerator(String dueDateInput){
        boolean returnStatement = true;
        int[] systemTime= javaTimer();  //Loads the local system time into an array int[] systemTime = {yearsDueDate, monthsDueDate, daysDueDate, hoursDueDate, minutesDueDate};
        String newDate = dueDateInput;     
        String[] initialSplit = newDate.split("-"); //[1]= Month, [2] = Day 
        String[] taskNameSplit = initialSplit[0].split(":"); //[0] = Task name, [1] = Year
        String[] timeSplit = initialSplit[3].split(":");//[0] = Hour, [1] = Minute
        long year = Long.parseLong(taskNameSplit[1]);
        long month = Long.parseLong(initialSplit[1]);
        long day = Long.parseLong(initialSplit[2]);
        long hour = Long.parseLong(timeSplit[0]);
        long minute = Long.parseLong(timeSplit[1]);
        DateTimeFormatter standard = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");  //date formatter
        String systemtimedate = systemTime[0] + "/" + systemTime[1] + "/" + systemTime[2] + " " + systemTime[3] + ":" + systemTime[4];
        LocalDateTime localsystDateTime = LocalDateTime.parse(systemtimedate, standard); //return date time
        String duedatetimedate = year + "/" + month + "/" + day + " " + hour + ":" + minute;
        LocalDateTime localduedatetimedate = LocalDateTime.parse(duedatetimedate, standard); //return  due date time on the csv file
        long systemmilli = localsystDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(); //Take local date time add to zone and convert to epochmilli
        long systemduedate = localduedatetimedate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(); //Take due date time add to zone and convert to epochmilli
        long compare = systemduedate - systemmilli;    //compare the system due date in milliseconds then subtract it with local time. 
        if(compare <= 600000){  //600000 miliseconds = 10 minutes. If the mili seconds are less than= 600000 
            returnStatement = true; //if it equal to ten minutes return true
        } 
        else {
            returnStatement= false;  //else return false
        }
        return returnStatement;
    }
    
    public static void compareMethods(String[]dueDateCSV) throws IOException {
        int[] systemTime = javaTimer();
        
        int alertSentHalf =0;
        int alertSentDue =0;
        int alertSentTen =0;
        for (int i =0; i<dueDateCSV.length; i++){
            //Calculations
            String newDate = dueDateCSV[i];
            String[] initialSplit = newDate.split("-"); //[1]= Month, [2] = Day 
            String[] taskNameSplit = initialSplit[0].split(":"); //[0] = Task name, [1] = Year
            String taskHalfDue= fileSearcher(taskNameSplit[0]);
            boolean tenMinuteAlert = tenminutegenerator(newDate);
            //Returns as Task Name:Year-Month-Day-Hour:Minute
            String currentYear = String.valueOf(systemTime[0]);
            String currentMonth = String.valueOf(systemTime[1]);
            String currentDay = String.valueOf(systemTime[2]);
            String currentHour = String.valueOf(systemTime[3]);
            String currentMinute = String.valueOf(systemTime[4]);
            String currentDate = initialSplit[0]+":"+currentYear+"-"+currentMonth+"-"+currentDay+"-"+currentHour+":"+currentMinute;
            if (currentDate.equals(taskHalfDue)){
                alertSentHalf +=1;
                File filePath = new File("onHalfDue.csv");
                FileWriter fw = new FileWriter(filePath, true);
                fw.write(taskNameSplit[0] +" at half mark! Be sure to work on it! ");
                fw.close();
            }
            if (currentDate.equals(newDate)){
                alertSentDue +=1;
                File filePath = new File("onDueDate.csv");
                FileWriter fw = new FileWriter(filePath, true);
                fw.write(taskNameSplit[0] + " is Due right now! Hand it in so you are not late! ");
                fw.close();
            }
            if (tenMinuteAlert ==true){  
                alertSentTen +=1;
                File filePath = new File("onTenMinute.csv");
                FileWriter fw = new FileWriter(filePath);
                fw.write(taskNameSplit[0] + " is due in ten minutes! Should get ready to submit! ");
                fw.close();
            }  
        } 
        if (alertSentHalf == 0){
            File filePathHalf = new File("onHalfDue.csv");
            FileWriter fw1 = new FileWriter(filePathHalf);
            fw1.write("Looking Good! Keep working");
            fw1.close();
        }
        if (alertSentDue ==0){
            File filePathDue = new File("onDueDate.csv");
            FileWriter fw2 = new FileWriter(filePathDue);
            fw2.write("Looking Good! No Task Due!");
            fw2.close();
        }
        if (alertSentTen ==0){
            File filePathTen = new File("onTenMinute.csv");
            FileWriter fw3 = new FileWriter(filePathTen);
            fw3.write("Looking Good! Nothing due in ten minutes!");
            fw3.close();
        }
    }
}