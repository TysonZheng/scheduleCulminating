import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

public class AppController {
    
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
    public void onClickGen(ActionEvent E) throws IOException{
        File file = new File("schedule.csv");
        FileWriter fw = new FileWriter(file, true);

        fw.write(TASK.getText());
        fw.write(": ");
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

        YEAR.clear();
        MONTH.clear();
        DATE.clear();
        HOUR.clear();
        MINUTE.clear();
        TASK.clear();
    }

    @FXML
    public void onClickExit(ActionEvent E) throws IOException {
        Platform.exit();
    }
    
    @FXML
    public void onClickRefresh(ActionEvent E) throws IOException {
        Path fileN = Path.of("schedule.csv");
        String text = Files.readString(fileN);
            while(text != null) {
                tarea.setText(text + "\n");
            }
            System.out.println(text);
    }

}
