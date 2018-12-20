package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.hyperic.sigar.OperatingSystem;
import org.hyperic.sigar.Sigar;

import java.net.URL;
import java.util.ResourceBundle;


public class Controller implements Initializable {

    @FXML
    Label labelTitleOperatingSystem;
    @FXML
    Label labelOperatingSystem;
    @FXML
    Label labelTitleArchitecture;
    @FXML
    Label labelArchitecture;

    OperatingSystem os;
    Sigar s;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        os = OperatingSystem.getInstance();
        s = new Sigar();

        labelOperatingSystem.setText(os.getDescription() + " " + os.getVendorCodeName() + " " + os.getVersion());


        System.out.println(os.getMachine());
        System.out.println(os.getDataModel());
        System.out.println(os.getArch());
    }
}
