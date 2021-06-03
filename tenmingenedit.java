import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
public class tenmingenedit {
    
    //Calculates if there is 10mins or less left from now to the due date if it is, it will return true if not returns false. Already accounts for time zone and leap years
    public static boolean tenminutegenerator(String dueDateInput[]){

        int[] systemTime= javaTimer();  //Loads the local system time into an array int[] systemTime = {yearsDueDate, monthsDueDate, daysDueDate, hoursDueDate, minutesDueDate};
        
        for (int i =0; i<dueDateInput.length; i++){
            
            String newDate = dueDateInput[i];     
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
            return true; //if it equal to ten minutes return true

        }   else {
            return false;  //else return false
 
        }
        
    }

    }
}
