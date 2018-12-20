package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.StageStyle;
import org.hyperic.sigar.*;

import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Controller implements Initializable {

    @FXML
    Label labelOperatingSystem;
    @FXML
    Label labelArchitecture;
    @FXML
    Label labelCPU;
    @FXML
    Label labelPhysicalCPU;
    @FXML
    Label labelCoresPerCPU;
    @FXML
    Label labelCacheSize;
    @FXML
    Label labelPrimaryIP;
    @FXML
    Label labelPrimaryMAC;
    @FXML
    Label labelHost;
    @FXML
    Label labelNameSO;

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
    @FXML
    MenuItem menuItemPCIDevices;
    @FXML
    Label labelPercentageBattery;

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
        Alert dialogAlert = new Alert(Alert.AlertType.INFORMATION);
        dialogAlert.setTitle("Message");
        dialogAlert.setHeaderText(null);
        dialogAlert.setContentText("Your file has been created in: " + data.getAbsolutePath());
        dialogAlert.initStyle(StageStyle.UTILITY);
        dialogAlert.showAndWait();
    }

    @FXML
    public void aboutInformation() {
        JOptionPane.showMessageDialog(null, "Creator: Omar Flores Salazar", "About", 1);
    }

    @FXML
    private void getPCIDevices() {
        if (labelNameSO.getText().equals("Linux")) {
            Alert dialogAlert = new Alert(Alert.AlertType.INFORMATION);
            dialogAlert.setTitle("PCI Devices");
            dialogAlert.setHeaderText(null);

            String[] cmd = {"/bin/bash", "-c", "lspci"};
            String information = "";
            String totalInformation = "";
            Process pb = null;
            try {
                pb = Runtime.getRuntime().exec(cmd);
                BufferedReader input = new BufferedReader(new InputStreamReader(pb.getInputStream()));
                while (true) {
                    if ((information = input.readLine()) == null) break;
                    totalInformation += information + "\n";
                }
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            dialogAlert.setContentText(totalInformation);
            dialogAlert.initStyle(StageStyle.UTILITY);
            dialogAlert.showAndWait();
        }
        if (labelNameSO.getText().equals("Windows")) {
            Alert dialogAlert = new Alert(Alert.AlertType.INFORMATION);
            dialogAlert.setTitle("PCI Devices");
            dialogAlert.setHeaderText(null);
            dialogAlert.setContentText("Not avaible in your system.");
            dialogAlert.initStyle(StageStyle.UTILITY);
            dialogAlert.showAndWait();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        os = OperatingSystem.getInstance();
        s = new Sigar();

        labelOperatingSystem.setText(os.getDescription() + " " + os.getVendorCodeName() + " " + os.getVersion());
        labelArchitecture.setText(os.getMachine());

        labelNameSO.setText(os.getName());
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
        this.getLaptopBatteryInPercentage();
    }

    private void getLaptopBatteryInPercentage() {
        String[] cmd = {"/bin/bash", "-c", "upower -i $(upower -e | grep BAT) | grep --color=never -E percentage|xargs|" +
                "cut -d\" \" -f2|sed s/%//"};
        String battery = "";
        Process pb = null;
        try {
            pb = Runtime.getRuntime().exec(cmd);
            BufferedReader input = new BufferedReader(new InputStreamReader(pb.getInputStream()));
            while (true) {
                if ((battery = input.readLine()) == null) break;
                labelPercentageBattery.setText("" + battery + "%");
            }
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
