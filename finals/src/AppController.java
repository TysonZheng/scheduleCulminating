import java.io.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;


public class AppController implements Initializable {
    @FXML
    private Button GENERATE;

    @FXML
    private TextField TASK;

    @FXML
    private TextField DATE;

    @FXML
    private TextField MONTH;

    @FXML
    private TextField YEAR;

    @FXML
    private TextField HOUR;

    @FXML
    private TextField MINUTE;

    @FXML
    private Button EXIT;

    @FXML
    private TextArea tarea;

    @FXML 
    private Button refresh;

    @FXML 
    private TextArea upcoming;

    @FXML
    private TextArea tenM;  

    @FXML
    private Label TENL;

    @FXML
    private Label HL;

    @FXML
    private Label DD;

    @FXML
    private TextArea halfTime;

    @FXML 
    private Button rrefresh;

    @FXML
    private Button complete;

    @FXML 
    private TextField tcompleted;

    @FXML
    public void onClickGen(ActionEvent E) throws IOException {
        File file = new File("schedule.csv");
        FileWriter fw = new FileWriter(file);
        if(TASK==null) {
            TASK.setPromptText("Invalid");
        }
        String[] newTaskDue = {TASK.getText(),YEAR.getText(),MONTH.getText(),DATE.getText(),HOUR.getText(),MINUTE.getText()};
        App complete = new App();
        int[] integerForTask = complete.integerDueDate(newTaskDue);
        boolean pastTask = complete.validatingTaskTime(integerForTask);
        if (pastTask ==true){
            fw.write(TASK.getText());
            fw.write(":");
            fw.write(YEAR.getText());
            fw.write("-");
            fw.write(MONTH.getText());
            fw.write("-");
            fw.write(DATE.getText());
            fw.write("-");
            fw.write(HOUR.getText());
            fw.write(":");
            fw.write(MINUTE.getText());
            fw.write("\n");
            fw.close();
        }
        else{
            TASK.setPromptText("Invalid");
            YEAR.setPromptText("Invalid");
            MONTH.setPromptText("Invalid");
            DATE.setPromptText("Invalid");
            HOUR.setPromptText("Invalid");
            MINUTE.setPromptText("Invalid");

        }
        YEAR.clear();
        MONTH.clear();
        DATE.clear();
        HOUR.clear();
        MINUTE.clear();
        TASK.clear();
    }

    @FXML
    public void onClickComplete(ActionEvent E) throws IOException{
        String getcomplete = tcompleted.getText();
        tcompleted.clear();
        
        App complete = new App();
        complete.removeTask(getcomplete);
    }

    @FXML
    public void onClickExit(ActionEvent E) throws IOException {
        Platform.exit();
    }

    @FXML
    public void onClickDisplay(ActionEvent E) throws IOException {
        Path fileN = Path.of("schedule.csv");
        String text = Files.readString(fileN);
        tarea.appendText(text);
    }

    @FXML 
    public void onCLickRefresh(ActionEvent E) throws IOException{
        halfTime.clear();
        upcoming.clear();
        tenM.clear();
        
        Path fileOHD = Path.of("onHalfDue.csv");
        Path fileODD = Path.of("onDueDate.csv");
        // Path fileOTM = Path.of("onTenMinute.csv");

        try {
            String textOHD = Files.readString(fileOHD);
            halfTime.appendText(textOHD);
            String textODD = Files.readString(fileODD);
            upcoming.appendText(textODD);
            // String textOTM = Files.readString(fileOTM);
            // tenM.appendText(textOTM);
        } catch (IOException e) {   
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Path fileOHD = Path.of("onHalfDue.csv");
        Path fileODD = Path.of("onDueDate.csv");
        // Path fileOTM = Path.of("onTenMinute.csv");

        try {
            String textOHD = Files.readString(fileOHD);
            halfTime.appendText(textOHD);
            String textODD = Files.readString(fileODD);
            upcoming.appendText(textODD);
            // String textOTM = Files.readString(fileOTM);
            // tenM.appendText(textOTM);
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }
}
