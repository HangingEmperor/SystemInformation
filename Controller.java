package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import org.hyperic.sigar.*;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Controller implements Initializable {

    @FXML
    Label labelTitleOperatingSystem;
    @FXML
    Label labelOperatingSystem;
    @FXML
    Label labelTitleArchitecture;
    @FXML
    Label labelArchitecture;
    @FXML
    Label labelTitleCPU;
    @FXML
    Label labelCPU;
    @FXML
    Label labelTitlePhysicalCPU;
    @FXML
    Label labelPhysicalCPU;
    @FXML
    Label labelTitleCoresPerCPU;
    @FXML
    Label labelCoresPerCPU;
    @FXML
    Label labelTitleCacheSize;
    @FXML
    Label labelCacheSize;
    @FXML
    Label labelTitlePrimaryIP;
    @FXML
    Label labelPrimaryIP;
    @FXML
    Label labelTitlePrimaryMAC;
    @FXML
    Label labelPrimaryMAC;
    @FXML
    Label labelTitleHost;
    @FXML
    Label labelHost;

    @FXML
    MenuBar menu;
    @FXML
    Menu menuFile;
    @FXML
    Menu menuHelp;
    @FXML
    MenuItem menuItemSave;
    @FXML
    MenuItem menuItemAbout;

    OperatingSystem os;
    Sigar s;
    NetInterfaceConfig net;
    NetInfo info;

    @FXML
    public void saveInformation() {
        File data = new File("SystemInformationData.txt");

        if (!data.exists()) {
            try {
                data.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        try {
            FileWriter writeData = new FileWriter(data.getName());
            writeData.append("Operating System: " + labelOperatingSystem.getText() + "\n");
            writeData.append("Architecture: " + labelArchitecture.getText() + "\n");
            writeData.append("CPU: " + labelCPU.getText() + "\n");
            writeData.append("Physical CPU: " + labelPhysicalCPU.getText() + "\n");
            writeData.append("Cores per CPU: " + labelCoresPerCPU.getText() + "\n");
            writeData.append("Cache size: " + labelCacheSize.getText() + "\n");
            writeData.append("Primary IP: " + labelPrimaryIP.getText() + "\n");
            writeData.append("Primary MAC: " + labelPrimaryMAC.getText() + "\n");
            writeData.append("HOST: " + labelHost.getText() + "\n");
            writeData.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        JOptionPane.showMessageDialog(null, "Your file has been created in: " + data.getAbsolutePath());
    }

    @FXML
    public void aboutInformation() {
        JOptionPane.showMessageDialog(null, "Creator: Omar Flores Salazar", "About", 1);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        os = OperatingSystem.getInstance();
        s = new Sigar();

        labelOperatingSystem.setText(os.getDescription() + " " + os.getVendorCodeName() + " " + os.getVersion());
        labelArchitecture.setText(os.getMachine());

        try {
            CpuInfo[] cpu = s.getCpuInfoList();
            CpuInfo info = cpu[0];

            labelCPU.setText(info.getVendor() + " " + info.getModel() + " " + info.getMhz() + " MHz");

            if ((info.getTotalCores() != info.getTotalSockets()) || (info.getCoresPerSocket() > info.getTotalCores())) {
                labelPhysicalCPU.setText("" + info.getTotalSockets());
                labelCoresPerCPU.setText("" + info.getCoresPerSocket());
            }

            if (info.getCacheSize() != Sigar.FIELD_NOTIMPL) {
                labelCacheSize.setText("" + info.getCacheSize());
            }

        } catch (SigarException ex) {
            ex.printStackTrace();
        }

        try {
            net = s.getNetInterfaceConfig(null);
            info = s.getNetInfo();
            labelPrimaryIP.setText(net.getAddress());
            labelPrimaryMAC.setText(net.getHwaddr());
            labelHost.setText(info.getHostName());
        } catch (SigarException e) {
            e.printStackTrace();
        }
    }
}
