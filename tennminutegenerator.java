import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
public class tennminutegenerator {
    
    //Calculates if there is 10mins or less left from now to the due date if it is, it will return true if not returns false
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
}
