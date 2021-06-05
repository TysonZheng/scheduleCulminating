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
                //Converts seconds to integer
                int secondsDueDate = Integer.parseInt(secondsInString);
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
    /*  Method Name: removeTask()
     *
     *  Description: This method removes a specified task from schedule.csv 
     *  based on the user's "Complete" input in the GUI. The method uses two 
     *  files, 'schedule.csv' and a temporary file, 'temp.csv' that stores all 
     *  tasks except the specified task. It goes line by line and stores each 
     *  token separately in their respective variables and if the read task does 
     *  NOT match the specified task, it will string together all the tokens into 
     *  a single variable and add it into 'temp.csv'. Once all lines in 'schedule.csv' 
     *  have been checked, all BUT the specified task will be added to 'temp.csv'. 
     *  'schedule.csv', will be deleted and replaced with 'temp.csv' which is 
     *  renamed to 'schedule.csv'
     * 
     *  @author: Kyle
     * 
     *  @param searchTask - a string variable holding the name of the task 
     *  that is specified to be removed
     * 
     * 
     *  @param filePath - string variable storing the file name, "schedule.csv"
     * 
     *  @param tempFile - string variable storing the temporary file name, "temp.csv"
     * 
     *  @returns - edited 'schedule.csv'
     *  
     */
    public static void removeTask(String searchTask) throws IOException {
        /* Defining Variables */

        //Initialize empty scanner 
        Scanner fileReader; 
        //File names
        String filePath = "schedule.csv";
        String tempFile = "temp.csv";
        //Empty variables
        String foundTask = "", foundDay = "", foundMonth = "", foundYear = "", foundHour = "", foundMinute = "";
        String foundInformation = "";
        //Defines new files
        File oldFile = new File(filePath);
        File newFile = new File(tempFile);
        try {
            //Defines writers
            FileWriter fw = new FileWriter(tempFile, true); //Writes to 'tempFile'
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);   //PrintWriter using BufferedWriter and FileWriter
            //Initialize new scanner to read file
            fileReader = new Scanner(new File(filePath));
            //Set delimiters
            fileReader.useDelimiter("[:\n-]");

            //While the file reader can find a new entry
            while (fileReader.hasNext()) {
                //Sets the variable values as the tokens found
                foundTask = fileReader.next();
                foundYear = fileReader.next();
                foundMonth = fileReader.next();
                foundDay = fileReader.next();
                foundHour = fileReader.next();
                foundMinute = fileReader.next();
                //As long as the found task DOES NOT match the search task
                if (!foundTask.equals(searchTask)) {
                    //Strings together the tokens inside a string variable
                    foundInformation = foundTask + ":" + foundYear + "-" + foundMonth + "-" + foundDay + "-" + foundHour + ":" + foundMinute;
                    //Writes the data to the csv
                    pw.print(foundInformation);
                    pw.print("\n");
                }
            }
            //closes scanner
            fileReader.close();
            //Flush/Close writer
            pw.flush();
            pw.close();
            //Delete old schedule.csv
            oldFile.delete();
            //Creates a new schedule.csv and puts temp.csv's data inside it
            File dump = new File(filePath);
            newFile.renameTo(dump);

        //Checks for exception    
        } catch (Exception e) {
            System.out.println("ERROR");
        }
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
        // systemTime = {Year, month, day, hour, minute}
        int[] systemTime = javaTimer();
        long minuteTotal = totalMinutes(dueDateInput);
        long halfAlertTimer = minuteTotal / 2;
        long halfAlertYears = (halfAlertTimer / 525600);
        long halfAlertMonth = halfAlertTimer / 43800 - (halfAlertYears * 12);
        long halfAlertDay = halfAlertTimer / 1440 - (halfAlertMonth * 30 + (halfAlertYears * 12) * 30);
        long halfAlertHour = halfAlertTimer / (60)
                - ((halfAlertDay + halfAlertMonth * 30 + (halfAlertYears * 12) * 30) * 24);
        long halfAlertMinute = halfAlertTimer
                - ((halfAlertHour + (halfAlertDay + halfAlertMonth * 30 + (halfAlertYears * 12) * 30) * 24) * 60);
        // Returned values
        int returnedYear = systemTime[0] + (int) halfAlertYears;
        int returnedMonth = systemTime[1] + (int) halfAlertMonth;
        int returnedDay = systemTime[2] + (int) halfAlertDay;
        int returnedHour = systemTime[3] + (int) halfAlertHour;
        int returnedMinute = systemTime[4] + (int) halfAlertMinute;
        if (returnedMinute > 60) {
            returnedHour += returnedMinute / 60;
            returnedMinute = returnedMinute % 60;
        }
        if (returnedHour > 24) {
            returnedDay += returnedHour / 24;
            returnedHour = returnedHour % 24;
        }
        if (returnedDay > 30) {
            returnedMonth += returnedDay / 30;
            returnedDay = returnedDay % 30;
        }
        if (returnedMonth > 12) {
            returnedYear += returnedMonth / 12;
            returnedMonth = returnedMonth % 12;
        }
        // Returns as Task Name:Year-Month-Day-Hour:Minute
        String returnedArrayDate = taskName + ":" + String.valueOf((returnedYear)) + "-"
                + String.valueOf((returnedMonth)) + "-" + String.valueOf((returnedDay)) + "-"
                + String.valueOf((returnedHour)) + ":" + String.valueOf((returnedMinute));
        return returnedArrayDate;
    }

    public static String[] halfDateAlertGenerator(String[] dueDateCSV) {
        String[] halfTimeArray = new String[0];
        for (int i = 0; i < dueDateCSV.length; i++) {
            // Calculations
            String newDate = dueDateCSV[i];
            String[] taskArray = splitsString(newDate);
            int[] dueDateInteger = integerDueDate(taskArray);
            String halfDue = halfAlertCalculations(dueDateInteger, taskArray[0]);
            halfTimeArray = Arrays.copyOf(halfTimeArray, halfTimeArray.length + 1);
            halfTimeArray[halfTimeArray.length - 1] = halfDue;
        }
        return halfTimeArray;
    }

    public static int[] integerDueDate(String[] stringDue) {
        int year = Integer.parseInt(stringDue[1]);
        int month = Integer.parseInt(stringDue[2]);
        int day = Integer.parseInt(stringDue[3]);
        int hour = Integer.parseInt(stringDue[4]);
        int minute = Integer.parseInt(stringDue[5]);
        int[] dueDateFormatted = { year, month, day, hour, minute };
        return dueDateFormatted;
    }

    public static long totalMinutes(int[] dueDateInput) {
        int[] systemTime = javaTimer();
        long minuteTotal = 0;
        if ((dueDateInput[0] > systemTime[0])
                && ((((dueDateInput[0] - systemTime[0]) * 12 - systemTime[1]) + dueDateInput[1]) > 12)) {
            minuteTotal += ((((dueDateInput[0] - systemTime[0]) * 12 - systemTime[1]) + (dueDateInput[1] - 1)) * 43800);
        } else if (dueDateInput[1] > systemTime[1]) {
            minuteTotal += (((dueDateInput[1] - 1 - systemTime[1])) * 43800);
        } else if (dueDateInput[0] > systemTime[0]
                && (((dueDateInput[0] - systemTime[0]) * 12 - systemTime[1]) + dueDateInput[1]) > 1) {
            minuteTotal += (((12 - systemTime[1]) + dueDateInput[1]) * 43800);
        }
        if (dueDateInput[2] > systemTime[2] && ((dueDateInput[1] - systemTime[1]) == 1)) {
            minuteTotal += (((dueDateInput[2] - systemTime[2]) + 29) * 1440);
        } else if (dueDateInput[2] > systemTime[2]) {
            minuteTotal += (((dueDateInput[2] - systemTime[2] - 1)) * 1440);
        } else if (dueDateInput[2] == systemTime[2]) {
            minuteTotal += 0;
        } else {
            minuteTotal += (((30 - systemTime[2]) + dueDateInput[2] - 1) * 1440);
        }
        if (dueDateInput[3] > systemTime[3] && ((dueDateInput[2] - systemTime[2]) > 1)) {
            minuteTotal += ((dueDateInput[3] - systemTime[3] + 23) * 60);
        } else if (dueDateInput[3] > systemTime[3]) {
            minuteTotal += ((dueDateInput[3] - systemTime[3] - 1) * 60);
        } else if (dueDateInput[3] == systemTime[3]) {
            minuteTotal += 0;
        } else {
            minuteTotal += (((23 - systemTime[3]) + dueDateInput[3]) * 60);
        }
        if ((dueDateInput[4] > systemTime[4]) && ((dueDateInput[3] - systemTime[3]) > 1)) {
            minuteTotal += (dueDateInput[4] - systemTime[4] + 60);
        } else if (dueDateInput[4] < systemTime[4] && (dueDateInput[3] > systemTime[3])) {
            minuteTotal += (((60 - systemTime[4]) + dueDateInput[4]));
        } else if (dueDateInput[4] > systemTime[4]) {
            minuteTotal += (dueDateInput[4] - systemTime[4]);
        }
        return minuteTotal;
    }

    public static String[] splitsString(String dueDate) {
        String[] initialSplit = dueDate.split("-"); // [1]= Month, [2] = Day
        String[] taskNameSplit = initialSplit[0].split(":"); // [0] = Task name, [1] = Year
        String[] timeSplit = initialSplit[3].split(":");// [0] = Hour, [1] = Minute
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
    public static String[] javaTimerString() {
        int[] systemTime = javaTimer();
        String currentYear = String.valueOf(systemTime[0]);
        String currentMonth = String.valueOf(systemTime[1]);
        String currentDay = String.valueOf(systemTime[2]);
        String currentHour = String.valueOf(systemTime[3]);
        String currentMinute = String.valueOf(systemTime[4]);
        String[] timerAsString = { currentYear, currentMonth, currentDay, currentHour, currentMinute };
        return timerAsString;
    }

    public static void compareMethods(String[] dueDateCSV) throws IOException {
        int alertSentHalf = 0;
        int alertSentDue = 0;
        String filePath = "";
        String message = "";
        // int alertSentTen =0;
        for (int i = 0; i < dueDateCSV.length; i++) {
            // Calculations
            String newDate = dueDateCSV[i];
            String[] taskNameSplit = splitsString(newDate);
            String taskHalfDue = fileSearcher(taskNameSplit[0]);
            String[] currentTime = javaTimerString();
            // boolean tenMinuteAlert = tenminutegenerator(newDate);
            // Returns as Task Name:Year-Month-Day-Hour:Minute
            String currentDate = taskNameSplit[0] + ":" + currentTime[0] + "-" + currentTime[1] + "-" + currentTime[2]
                    + "-" + currentTime[3] + ":" + currentTime[4];
            if (currentDate.equals(taskHalfDue)) {
                alertSentHalf += 1;
                filePath = "onHalfDue.csv";
                message = taskNameSplit[0]
                        + " is at its half point meaning you only have half the time left. Make sure to start if you haven't! ";
                fileWritingDisplay(filePath, message);
            }
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
        if (alertSentHalf == 0) {
            filePath = "onHalfDue.csv";
            message = "Nothing is at its half point. Keep working";
            fileWritingDisplay(filePath, message);
        }
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

    public static void fileWritingDisplay(String filePath, String message) throws IOException {
        File filePathDisplay = new File(filePath);
        FileWriter fw = new FileWriter(filePathDisplay);
        fw.write(message);
        fw.close();
    }
}