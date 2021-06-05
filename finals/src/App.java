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
import java.util.Date;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Calendar;
import java.time.ZoneId;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;

//Class 
public class App extends Application {

    /*
    Starts java fx
    */
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
     * Method name: main() Description: The place where specific items are called
     * according to a timer tasks which will delay and cycle tasks. There are 2
     * separate timer tasks in the main method, one for exercise which needs to run
     * on a 24 hour cycle and the other for the javaTimer and checking alerts
     * happening every new minute.
     * 
     * @author: Tyson, Kyle, Morgan, Patrick
     * 
     * @
     */
    /*
    Method name: main()
    Description: The place where specific items are called according to a timer tasks which will delay and cycle tasks. 
                 There are 2 separate timer tasks in the main method, one for exercise which needs to run on a 24 hour cycle and the other for the javaTimer and checking alerts happening every new minute.
    @author: Tyson, Kyle, Morgan, Patrick 
    */
    public static void main(String[] args) throws Exception {
        //File paths used with reusable methods
        String scheduleFilePath = "schedule.csv"; 
        String halfFilePath = "halfalerts.csv";
        String dueDateFilePath = "dueDates.csv";
        //Launches the application
        launch(args);
        //Timer that runs the tasks in main every second for checks
        Timer timer = new Timer();
        timer.schedule(new TimerTask(){
            //Runs the code within the timer
            public void run(){
                //Finds the length of CSV files of file paths
                int dueLines = countLines(scheduleFilePath);
                int halfLines = countLines(halfFilePath);
                //Local time for the seconds
                LocalTime time = LocalTime.now(); 
                //Formats for the seconds
                DateTimeFormatter second= DateTimeFormatter.ofPattern("ss"); 
                String secondsInString = time.format(second); 
                //Declares the array to store the tasks in CSV
                String[] dueDateInput = new String[dueLines];
                //Trys 
                try {
                    //Rewrites the schedule CSV
                    scheduleWriter(scheduleFilePath, dueDateFilePath);
                    //Gets the tasks from schedule.csv with fileReader 
                    dueDateInput = fileReader(scheduleFilePath);
                    //Checks if alerts are supposed to go off
                    compareMethods(dueDateInput);
                //Catch  
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //Only if there is a change in the schedule CSV 
                if (dueLines != halfLines){
                    //Trys
                    try {
                        //Writes the halfalerts.csv 
                        halfDateWriter(dueDateInput);
                    //Catch
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                
            }  
        //Runs every second
        }, 0, 1000);
        //removeTask();
        //Other task that runs on 24 hours 
        timer = new Timer();
        timer.schedule(new TimerTask(){
            //Runs
            public void run(){
                //Trys
                try {
                    //Writes daily exercise into the scheduler
                    exerciseWriter(scheduleFilePath);
                //Catch
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } 
        //Runs 24 hour cycles 
        }, 0, 24*60*60*1000);
        
    }
    /*
    Method Name: javaTimer()
    Description: A method to get the local time of the computer and formats them into integers to be used in calculations
                 and other methods that require the time in comparisons. 
    @author: Tyson 
    @return: systemTime (An int[] which holds the integer values of the date)
             systemTime = {Year, Month, Day, Hour, Minute};
    */
    public static int[] javaTimer(){
        //Local time
        LocalTime time = LocalTime.now();
        //Sets format for minute
        DateTimeFormatter minutes = DateTimeFormatter.ofPattern("mm");
        //Gets minutes of local time
        String minuteInString = time.format(minutes);
        //Converts to integer
        int minutesDueDate = Integer.parseInt(minuteInString);
        //Sets format for hour
        DateTimeFormatter hour = DateTimeFormatter.ofPattern("HH");
        //Gets hours of local time
        String hourInString = time.format(hour);
        //Converts to integer
        int hoursDueDate = Integer.parseInt(hourInString);
        //Local date
        LocalDate date = LocalDate.now();
        //Sets format for day
        DateTimeFormatter day = DateTimeFormatter.ofPattern("dd");
        //Gets days of local time
        String dayInString = date.format(day);
        //Converts to integer
        int daysDueDate = Integer.parseInt(dayInString);
        //Rounds 31 days to 30 
        if (daysDueDate >30){
            daysDueDate = 30;
        }
        //Sets format for month
        DateTimeFormatter month = DateTimeFormatter.ofPattern("MM");
        //Gets months of local time 
        String monthInString = date.format(month);
        //Converts to integer
        int monthsDueDate = Integer.parseInt(monthInString);
        //Sets format for year
        DateTimeFormatter year = DateTimeFormatter.ofPattern("yyyy");
        //Gets the year
        String yearsInString = date.format(year);
        //Converts to integer
        int yearsDueDate = Integer.parseInt(yearsInString);
        //Creates array to store the times
        int[] systemTime = {yearsDueDate, monthsDueDate, daysDueDate, hoursDueDate, minutesDueDate};
        //returns time
        return systemTime;
    }

    /*
     * Method Name: fileSearcher() 
     * Description: A method that searches for a
     * specific task stored in a csv. It does this by comparing the task name you're
     * looking for with every task name stored in the csv file. It will take each
     * token for that task and store them in their own respective variables. For
     * example, if you want to find a task named "Math Test", it will find the
     * matching task and it's dates. The token, "Math Test" will be stored in its
     * respective variable.
     * 
     * @param searchTask - The string variable that stores the specific task the
     * user is looking for.
     * 
     * @returns foundInformation - A string variable that combines and stores each
     * variable holding the found csv information.
     * 
     * @author: Kyle
     */
    /* Method Name: fileSearcher()
     * Description: A method that searches for a specific task stored in a csv. 
     *              It does this by comparing the task name you're looking for with every task name stored in the csv file. 
     *              It will take each token for that task and store them in their own respective variables.
     *              For example, if you want to find a task named "Math Test", it will find the matching task and it's dates. The token, "Math Test" will be stored in its respective variable.
     * 
     * @param searchTask - The string variable that stores the specific task the user is looking for.
     * @returns foundInformation - A string variable that combines and stores each variable holding the found csv information. 
     * @author: Kyle
     */
    public static String fileSearcher(String searchTask) throws IOException{
        //Initializing Scanner
        Scanner fileReader;
        Scanner reader = new Scanner(System.in);
        //filePath for search
        String filePath = "halfalerts.csv";
        //Sets boolean as false
        boolean found = false;
        //Initializes variables 
        String foundTask = "", foundDay = "", foundMonth = "", foundYear ="", foundHour = "", foundMinute = "";
        String foundInformation = "";
        //Trys
        try {
            //new fileReader
            fileReader = new Scanner(new File(filePath));
            //Delimiter to separate
            fileReader.useDelimiter("[:\n-]");
            //Loops while there is a next line and the task has yet to be found 
            while (fileReader.hasNext() && !found){
                //Stores the values in CSV
                foundTask = fileReader.next();
                foundYear = fileReader.next();
                foundMonth = fileReader.next();
                foundDay = fileReader.next();
                foundHour = fileReader.next();
                foundMinute = fileReader.next();
                //Checks if task equals 
                if (foundTask.equals(searchTask)) {
                    found = true;
                }
            }
            //If the loop stops
            if (found) {
                //Stores as a string in the same format as other dates
                foundInformation = foundTask+":"+foundYear+"-"+foundMonth+"-"+foundDay+"-"+foundHour+":"+foundMinute;
            }
        //Catch 
        } catch (Exception e) {
            System.out.println("ERROR");
        }
        //Close reader
        reader.close();
        //Returns string 
        return foundInformation;
    }

    public static int countLines(String file) {
        int lines = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            while (reader.readLine() != null)
                lines++;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    public static String[] fileReader(String file) throws IOException {
        String line = "";
        int numOfLines = countLines(file);
        String task[] = new String[numOfLines];
        int counter = 0;
        try {
            BufferedReader csvReader = new BufferedReader(new FileReader(file));
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
        try (PrintWriter writer = new PrintWriter(filePath)) {
            for (int i = 0; i < arr.length; i++) {
                StringBuilder sb = new StringBuilder();
                sb.append(arr[i]);
                sb.append("\n");
                writer.write(sb.toString());
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void exerciseWriter(String destinationFile) throws IOException {
        String[] currentTasks = fileReader(destinationFile);
        String[] stringTimer = javaTimerString();
        String addExercise = "Daily exercise:" + stringTimer[0] + "-" + stringTimer[1] + "-" + stringTimer[2] + "-"+ "23:59";
        File filePath = new File("schedule.csv");
        FileWriter fw = new FileWriter(filePath, true);

        boolean add = true;
        for (int i = 0; i < currentTasks.length; i++) {
            if (addExercise.equals(currentTasks[i])) {
                add = false;
            }
        }
        if (add == true) {
            System.out.println(
                    "You do not have exercise listed today. Daily Exercise task has been added to your schedule");
            fw.write(addExercise);
            fw.write("\n");
            fw.close();
        }
    }

    public static void scheduleWriter(String destinationFile, String addElementFile) throws IOException {
        String[] currentTasks = fileReader(destinationFile); // Elements from the destination file
        String[] toBeAdded = fileReader(addElementFile); // Elements from the file you're adding
        File filePath = new File(destinationFile); // The file you're writing to
        FileWriter fw = new FileWriter(filePath, true); // Will write to schedule.csv
        boolean add;

        try {
            for (int e = 0; e < toBeAdded.length; e++) {
                add = true;
                for (int i = 0; i < currentTasks.length; i++) {
                    if (toBeAdded[e].equals(currentTasks[i])) {
                        add = false;
                    }
                }
                if (add == true) {
                    System.out.println(toBeAdded[e] + " has no duplicates. It will be added to schedule.csv");
                    fw.append(toBeAdded[e]);
                    fw.append("\n");
                }
            }
            fw.close();
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    public static boolean[] compareDate(String[] taskCSV) {
        boolean[] pastDue = new boolean[taskCSV.length];
        for (int i = 0; i < taskCSV.length; i++) {
            // Calculations
            String newDate = taskCSV[i];
            String[] taskArray = splitsString(newDate);
            int[] taskDateInt = integerDueDate(taskArray);
            boolean pastTaskDate = validatingTaskTime(taskDateInt);
            pastDue[i] = pastTaskDate;
        }
        return pastDue;
    }

    public static boolean validatingTaskTime(int[] intConversion) {

        Calendar c = Calendar.getInstance();

        c.set(Calendar.YEAR, intConversion[0]);
        c.set(Calendar.MONTH, intConversion[1] - 1);
        c.set(Calendar.DATE, intConversion[2]);
        c.set(Calendar.HOUR_OF_DAY, intConversion[3]);
        c.set(Calendar.MINUTE, intConversion[4]);
        c.set(Calendar.SECOND, 0);

        Date taskDueDate = c.getTime();

        Date currentDate = new Date();
        boolean taskCheck = taskDueDate.before(currentDate);
        return taskCheck;

    }

    public static void removeTask(String searchTask) throws IOException {
        Scanner fileReader;
        Scanner reader = new Scanner(System.in);
        String filePath = "schedule.csv";
        String tempFile = "temp.csv";
        String foundTask = "", foundDay = "", foundMonth = "", foundYear = "", foundHour = "", foundMinute = "";
        String foundInformation = "";
        File oldFile = new File(filePath);
        File newFile = new File(tempFile);
        try {
            FileWriter fw = new FileWriter(tempFile, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);
            fileReader = new Scanner(new File(filePath));
            fileReader.useDelimiter("[:\n-]");

            while (fileReader.hasNext()) {
                foundTask = fileReader.next();
                foundYear = fileReader.next();
                foundMonth = fileReader.next();
                foundDay = fileReader.next();
                foundHour = fileReader.next();
                foundMinute = fileReader.next();
                if (!foundTask.equals(searchTask)) {
                    foundInformation = foundTask + ":" + foundYear + "-" + foundMonth + "-" + foundDay + "-" + foundHour
                            + ":" + foundMinute;
                    System.out.println(foundInformation);
                    pw.print(foundInformation);
                    pw.print("\n");
                }
            }
            fileReader.close();
            pw.flush();
            pw.close();
            oldFile.delete();
            File dump = new File(filePath);
            newFile.renameTo(dump);
        } catch (Exception e) {

        }
        reader.close();
    }
    /*
    Method name: halfAlertCalculations
    Description: Calculates the amount of total minutes between current time and the time of the due date. It will be divded and sorted into the half date but adding to the current date
                 The returned values are checked for logic and are returned as a string with format
    @author: Tyson 
    @param: dueDateInput (int[] of the due date), taskName (Name of the task being passed)
    @return: returnedArrayDate (String of the half date)
    */

    public static String halfAlertCalculations(int[] dueDateInput, String taskName) {
        //Gets local time
        int[] systemTime = javaTimer();
        //Gets the total minutes
        long minuteTotal = totalMinutes(dueDateInput);
        //Divides minutes by half
        long halfAlertTimer = minuteTotal / 2;
        //Calculates the years
        long halfAlertYears = (halfAlertTimer / 525600);
        //Calculates the month
        long halfAlertMonth = halfAlertTimer / 43800 - (halfAlertYears * 12);
        //Calculates the day
        long halfAlertDay = halfAlertTimer / 1440 - (halfAlertMonth * 30 + (halfAlertYears * 12) * 30);
        //Calculates hours
        long halfAlertHour = halfAlertTimer / (60)- ((halfAlertDay + halfAlertMonth * 30 + (halfAlertYears * 12) * 30) * 24);
        //Calculates minutes
        long halfAlertMinute = halfAlertTimer - ((halfAlertHour + (halfAlertDay + halfAlertMonth * 30 + (halfAlertYears * 12) * 30) * 24) * 60);
        //Gets the half date by adding calculated values
        int returnedYear = systemTime[0] + (int) halfAlertYears;
        int returnedMonth = systemTime[1] + (int) halfAlertMonth;
        int returnedDay = systemTime[2] + (int) halfAlertDay;
        int returnedHour = systemTime[3] + (int) halfAlertHour;
        int returnedMinute = systemTime[4] + (int) halfAlertMinute;
        //Rounds and logic checks
        //If minute over 60
        if (returnedMinute > 60) {
            //Adds hour
            returnedHour += returnedMinute / 60;
            //Gets minute
            returnedMinute = returnedMinute % 60;
        }
        //If hour over 24
        if (returnedHour > 24) {
            //Adds day
            returnedDay += returnedHour / 24;
            //Gets hour
            returnedHour = returnedHour % 24;
        }
        //If day over 30
        if (returnedDay > 30) {
            //Adds month
            returnedMonth += returnedDay / 30;
            //Gets day
            returnedDay = returnedDay % 30;
        }
        //If month over 12
        if (returnedMonth > 12) {
            //Add year
            returnedYear += returnedMonth / 12;
            //Gets months
            returnedMonth = returnedMonth % 12;
        }
        // Returns as Task Name:Year-Month-Day-Hour:Minute in format
        String returnedArrayDate = taskName + ":" + String.valueOf((returnedYear)) + "-"
                + String.valueOf((returnedMonth)) + "-" + String.valueOf((returnedDay)) + "-"
                + String.valueOf((returnedHour)) + ":" + String.valueOf((returnedMinute));
        return returnedArrayDate;
    }
    /*
    Method Name: halfDateAlertGenerator()
    Description: Gets the value of half due date and turns into String values to be stored in a String array.
                 The returned value will be used in writing the halfalerts.csv
    @author: Tyson
    @param: dueDueCSV (String[] with the due dates)
    @return: halfTimeArray (The string array with all the half times)
    */
    public static String[] halfDateAlertGenerator(String[] dueDateCSV) {
        //Initialize halfTimeArray
        String[] halfTimeArray = new String[0];
        //For loop to look through each task 
        for (int i = 0; i < dueDateCSV.length; i++) {
            //Gets one task 
            String newDate = dueDateCSV[i];
            //Splits the task
            String[] taskArray = splitsString(newDate);
            //Converts to integers
            int[] dueDateInteger = integerDueDate(taskArray);
            //Calls halfAlertCalculations
            String halfDue = halfAlertCalculations(dueDateInteger, taskArray[0]);
            //Adds the String to string array
            halfTimeArray = Arrays.copyOf(halfTimeArray, halfTimeArray.length + 1);
            halfTimeArray[halfTimeArray.length - 1] = halfDue;
        }
        return halfTimeArray;
    }
    /*
    Method name: integerDueDate()
    Description: Turns string array of due dates to integer
    @author: Tyson
    @param: stringDue (String array of due date)
    @returns: dueDateFromatted(int[] converted date)

    */
    public static int[] integerDueDate(String[] stringDue) {
        //Converts to integer
        int year = Integer.parseInt(stringDue[1]);
        int month = Integer.parseInt(stringDue[2]);
        int day = Integer.parseInt(stringDue[3]);
        int hour = Integer.parseInt(stringDue[4]);
        int minute = Integer.parseInt(stringDue[5]);
        //Returns in int[]
        int[] dueDateFormatted = { year, month, day, hour, minute };
        return dueDateFormatted;
    }
    /*
    Method Name: totalMinutes
    Description: Calculates the total minutes in between due date and the current time. Will return an integer of the total minutes
    @author: Tyson 
    @param: dueDateInput(The due date as int[])
    @return: minuteTotal (Long - returns the total minutes as calculated)
    */
    public static long totalMinutes(int[] dueDateInput) {
        //Local time
        int[] systemTime = javaTimer();
        //Initializes minuteTotal
        long minuteTotal = 0;
        //If year of due is larger and months greater than a year
        if ((dueDateInput[0] > systemTime[0]) && ((((dueDateInput[0] - systemTime[0]) * 12 - systemTime[1]) + dueDateInput[1]) > 12)) {
            //Gets the minutes of the year, month -1 
            minuteTotal += ((((dueDateInput[0] - systemTime[0]) * 12 - systemTime[1]) + (dueDateInput[1] - 1)) * 43800);
        //If year greater and month less than a full year
        } else if (dueDateInput[0] > systemTime[0] && (((dueDateInput[0] - systemTime[0]) * 12 - systemTime[1]) + dueDateInput[1]) > 1) {
            //Gets the month in minutes
            minuteTotal += (((12 - systemTime[1]) + dueDateInput[1]) * 43800);
        }
        //If month is just greater
        else if (dueDateInput[1] > systemTime[1]) {
            //Gets the values for month in minutes
            minuteTotal += (((dueDateInput[1] - 1 - systemTime[1])) * 43800);
        } 
        //If day is greater and there is a difference of one month
        if (dueDateInput[2] > systemTime[2] && ((dueDateInput[1] - systemTime[1]) == 1)) {
            //Gets the days -1 in minutes for month
            minuteTotal += (((dueDateInput[2] - systemTime[2]) + 29) * 1440);
        //If day is greater
        } else if (dueDateInput[2] > systemTime[2]) {
            //Gets the months -1 in minutes
            minuteTotal += (((dueDateInput[2] - systemTime[2] - 1)) * 1440);
        //If same day
        } else if (dueDateInput[2] == systemTime[2]) {
            //Nothing
            minuteTotal += 0;
        //Else
        } else {
            //Gets the minutes in days
            minuteTotal += (((30 - systemTime[2]) + dueDateInput[2] - 1) * 1440);
        }
        //If hour greater and day greater than 1
        if (dueDateInput[3] > systemTime[3] && ((dueDateInput[2] - systemTime[2]) > 1)) {
             
            minuteTotal += ((dueDateInput[3] - systemTime[3] + 23) * 60);
        //If hour greater
        } else if (dueDateInput[3] > systemTime[3]) {
            minuteTotal += ((dueDateInput[3] - systemTime[3] - 1) * 60);
        //If hour the same
        } else if (dueDateInput[3] == systemTime[3]) {
            minuteTotal += 0;
        //Else
        } else {
            minuteTotal += (((23 - systemTime[3]) + dueDateInput[3]) * 60);
        }
        //If minutes greater and hours above 1
        if ((dueDateInput[4] > systemTime[4]) && ((dueDateInput[3] - systemTime[3]) > 1)) {
            minuteTotal += (dueDateInput[4] - systemTime[4] + 60);
        //If minutes less and hours greater
        } else if (dueDateInput[4] < systemTime[4] && (dueDateInput[3] > systemTime[3])) {
            minuteTotal += (((60 - systemTime[4]) + dueDateInput[4]));
        //If minutes greater
        } else if (dueDateInput[4] > systemTime[4]) {
            minuteTotal += (dueDateInput[4] - systemTime[4]);
        }
        //Returns minutes
        return minuteTotal;
    }
    /*
    Method Name: splitsString()
    Description: Gets the values in the string to be in an array
    @author: Tyson 
    @param: dueDate (String of due date)
    @return: returnDate (Date in array)
    */
    public static String[] splitsString(String dueDate) {
        String[] initialSplit = dueDate.split("-"); // [1]= Month, [2] = Day
        String[] taskNameSplit = initialSplit[0].split(":"); // [0] = Task name, [1] = Year
        String[] timeSplit = initialSplit[3].split(":");// [0] = Hour, [1] = Minute
        //Stores in string array
        String[] returnDate = { taskNameSplit[0], taskNameSplit[1], initialSplit[1], initialSplit[2], timeSplit[0],
                timeSplit[1] };
        return returnDate;
    }

    // Calculates if there is 10mins or less left from now to the due date if it is,
    // it will return true if not returns false. Already accounts for time zone and
    // leap years
    /*
     * public static boolean tenminutegenerator(String dueDateInput){ boolean
     * returnStatement = true; int[] systemTime= javaTimer(); //Loads the local
     * system time into an array int[] systemTime = {yearsDueDate, monthsDueDate,
     * daysDueDate, hoursDueDate, minutesDueDate}; String newDate = dueDateInput;
     * String[] initialSplit = newDate.split("-"); //[1]= Month, [2] = Day String[]
     * taskNameSplit = initialSplit[0].split(":"); //[0] = Task name, [1] = Year
     * String[] timeSplit = initialSplit[3].split(":");//[0] = Hour, [1] = Minute
     * long year = Long.parseLong(taskNameSplit[1]); long month =
     * Long.parseLong(initialSplit[1]); long day = Long.parseLong(initialSplit[2]);
     * long hour = Long.parseLong(timeSplit[0]); long minute =
     * Long.parseLong(timeSplit[1]); DateTimeFormatter standard =
     * DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"); //date formatter String
     * systemtimedate = systemTime[0] + "/" + systemTime[1] + "/" + systemTime[2] +
     * " " + systemTime[3] + ":" + systemTime[4]; LocalDateTime localsystDateTime =
     * LocalDateTime.parse(systemtimedate, standard); //return date time String
     * duedatetimedate = year + "/" + month + "/" + day + " " + hour + ":" + minute;
     * LocalDateTime localduedatetimedate = LocalDateTime.parse(duedatetimedate,
     * standard); //return due date time on the csv file long systemmilli =
     * localsystDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
     * //Take local date time add to zone and convert to epochmilli long
     * systemduedate =
     * localduedatetimedate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(
     * ); //Take due date time add to zone and convert to epochmilli long compare =
     * systemduedate - systemmilli; //compare the system due date in milliseconds
     * then subtract it with local time. if(compare <= 600000){ //600000 miliseconds
     * = 10 minutes. If the mili seconds are less than= 600000 returnStatement =
     * true; //if it equal to ten minutes return true } else { returnStatement=
     * false; //else return false } return returnStatement; }
     */

    /*
    Method name: javaTimerString()
    Description: Turns local time to string
    @author: Tyson
    @return: timerAsString (String array of local time)
    */
    public static String[] javaTimerString() {
        //Local tie 
        int[] systemTime = javaTimer();
        //Current time as string 
        String currentYear = String.valueOf(systemTime[0]);
        String currentMonth = String.valueOf(systemTime[1]);
        String currentDay = String.valueOf(systemTime[2]);
        String currentHour = String.valueOf(systemTime[3]);
        String currentMinute = String.valueOf(systemTime[4]);
        String[] timerAsString = { currentYear, currentMonth, currentDay, currentHour, currentMinute };
        return timerAsString;
    }
    /*
    Method name: compareMethods()
    Description: Checks if alerts should be heading off
    @author: Tyson 
    @param: dueDateCSV (String[] of the due dates )
    */
    public static void compareMethods(String[] dueDateCSV) throws IOException {
        //Initialize 
        int alertSentHalf = 0;
        int alertSentDue = 0;
        String filePath = "";
        String message = "";
        // int alertSentTen =0;
        for (int i = 0; i < dueDateCSV.length; i++) {
            // Calculations
            String newDate = dueDateCSV[i];
            //Gets the values
            String[] taskNameSplit = splitsString(newDate);
            //Searchers for task 
            String taskHalfDue = fileSearcher(taskNameSplit[0]);
            String[] currentTime = javaTimerString();
            // boolean tenMinuteAlert = tenminutegenerator(newDate);
            // Returns as Task Name:Year-Month-Day-Hour:Minute
            String currentDate = taskNameSplit[0] + ":" + currentTime[0] + "-" + currentTime[1] + "-" + currentTime[2]
                    + "-" + currentTime[3] + ":" + currentTime[4];
            //Alerts with checks 
            if (currentDate.equals(taskHalfDue)) {
                alertSentHalf += 1;
                filePath = "onHalfDue.csv";
                message = taskNameSplit[0]
                        + " is at its half point meaning you only have half the time left. Make sure to start if you haven't! ";
                fileWritingDisplay(filePath, message);
            }
            //Alerts with checks 
            if (currentDate.equals(newDate)) {
                filePath = "onDueDate.csv";
                message = taskNameSplit[0] + " is due right now. Be sure to hand get that finished! ";
                fileWritingDisplay(filePath, message);

            }
            /*
             * if (tenMinuteAlert ==true){ alertSentTen +=1; File filePath = new
             * File("onTenMinute.csv"); FileWriter fw = new FileWriter(filePath);
             * fw.write(taskNameSplit[0] +
             * " is due in ten minutes! Should get ready to submit! "); fw.close(); }
             */
        }
        //Message for when without display
        if (alertSentHalf == 0) {
            filePath = "onHalfDue.csv";
            message = "Nothing is at its half point. Keep working";
            fileWritingDisplay(filePath, message);
        }
        //Message for when without display
        if (alertSentDue == 0) {
            filePath = "onDueDate.csv";
            message = "Looking Good! Nothing is due right now";
            fileWritingDisplay(filePath, message);
        }
        /*
         * if (alertSentTen ==0){ File filePathTen = new File("onTenMinute.csv");
         * FileWriter fw3 = new FileWriter(filePathTen);
         * fw3.write("Looking Good! Nothing due in ten minutes!"); fw3.close(); }
         */
    }
    /*
    Method Name: fileWritingDisplay()
    Description: Writes the alerts
    @author: Tyson 
    @param: filePath (String of file), message (Message to be displayed)

    */
    public static void fileWritingDisplay(String filePath, String message) throws IOException {
        //File writer
        File filePathDisplay = new File(filePath);
        FileWriter fw = new FileWriter(filePathDisplay);
        //Writes the message for display
        fw.write(message);
        fw.close();
    }
}