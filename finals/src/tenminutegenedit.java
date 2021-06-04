import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
public class tenminutegenedit {
    
//Calculates if there is 10mins or less left from now to the due date if it is, it will return true if not returns false. Already accounts for time zone and leap years
public static boolean tenminutegenerator(String dueDateInput[]){

    int[] systemTime= javaTimer();  //Loads the local system time into an array int[] systemTime = {yearsDueDate, monthsDueDate, daysDueDate, hoursDueDate, minutesDueDate};
    
    for (int i =0; i<dueDateInput.length; i++){
        
        String newDate = dueDateInput[i];     
        String[] taskArray = splitsString(newDate);
        int year = Integer.parseInt(taskArray[1]);
        int month = Integer.parseInt(taskArray[2]);
        int day = Integer.parseInt(taskArray[3]);
        int hour = Integer.parseInt(taskArray[4]);
        int minute = Integer.parseInt(taskArray[5]);

        Instant duedate = Instant.now().atZone(ZoneOffset.UTC)  //Forces an instant to exist to use atZone
        .withYear(year)  
        .withMonth(month) 
        .withDay(day) 
        .withHour(hour)
        .withMinute(minute)
        .toInstant();

        long systemmilli = duedate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(); //Take local date time add to zone and convert to epochmilli

        long systemduedate = newDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(); //Take due date time add to zone and convert to epochmilli

        long compare = systemduedate - systemmilli;    //compare the system due date in milliseconds then subtract it with local time. 

        if(compare <= 600000){  //600000 miliseconds = 10 minutes. If the mili seconds are less than= 600000 
        return true; //if it equal to ten minutes return true

    }   else {
        return false;  //else return false

    }
    
}

}
    
}
