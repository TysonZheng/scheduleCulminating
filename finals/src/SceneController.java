import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;

public class AppController {
    
    @FXML
    private Button GENERATE;

    @FXML
    private TextField DATE;

    @FXML
    private TextField MONTH;

    @FXML
    private TextField YEAR;

    @FXML
    private Button EXIT;

    @FXML 
    public void onClickGen(ActionEvent E) throws IOException{
        File file = new File("schedule.csv");
        FileWriter fw = new FileWriter(file, true);

        fw.write(DATE.getText());
        fw.write(",");
        fw.write(MONTH.getText());
        fw.write(",");
        fw.write(YEAR.getText());
        fw.write("\n");
        fw.close();

        DATE.clear();
        MONTH.clear();
        YEAR.clear();
    }

    @FXML
    public void onClickExit(ActionEvent E) throws IOException {
        Platform.exit();
    }

}
