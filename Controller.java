package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.StageStyle;
import org.hyperic.sigar.*;

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
    Label labelUptimeMachine;

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
    MenuItem menuItemUSBDevices;
    @FXML
    MenuItem menuItemDiskUsageInfo;
    @FXML
    Label labelPercentageBattery;

    OperatingSystem os;
    Sigar s;
    NetInterfaceConfig net;
    NetInfo info;

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

        labelUptimeMachine.setText(executeTerminal("uptime"));
        labelPercentageBattery.setText(executeTerminal("upower -i $(upower -e | grep BAT) | grep --color=never -E percentage|xargs|" +
                "cut -d\" \" -f2|sed s/%//") + "%");
    }

    public String executeTerminal(String command) {
        if (labelNameSO.getText().equals("Linux")) {
            String[] cmd = {"/bin/bash", "-c", command};
            String information = "";
            String returnInformation = "";
            Process process = null;

            try {
                process = Runtime.getRuntime().exec(cmd);
                BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
                int counter = 0;
                while (true) {
                    if ((information = input.readLine()) == null) break;
                    if (counter >= 1) {
                        returnInformation += "\n" + information + "\n";
                    } else {
                        returnInformation += information;
                    }
                    counter++;
                }
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return returnInformation;
        }
        return "failed";
    }

    @FXML
    public void aboutInformation() {
        Alert dialogAlert = new Alert(Alert.AlertType.INFORMATION);
        dialogAlert.setTitle("About");
        dialogAlert.setHeaderText(null);
        dialogAlert.setContentText("Creator: Omar Flores Salazar");
        dialogAlert.initStyle(StageStyle.UTILITY);
        dialogAlert.showAndWait();
    }

    @FXML
    private void getPCIDevices() {
        if (labelNameSO.getText().equals("Linux")) {
            Alert dialogAlert = new Alert(Alert.AlertType.INFORMATION);
            dialogAlert.setTitle("PCI Devices");
            dialogAlert.setHeaderText(null);
            dialogAlert.setContentText(executeTerminal("lspci"));
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

    @FXML
    private void getDiskUsageInfo() {
        if (labelNameSO.getText().equals("Linux")) {
            Alert dialogAlert = new Alert(Alert.AlertType.INFORMATION);
            dialogAlert.setTitle("Disk Usage Info");
            dialogAlert.setHeaderText(null);
            dialogAlert.setContentText(executeTerminal("df -k"));
            dialogAlert.initStyle(StageStyle.UTILITY);
            dialogAlert.showAndWait();
        }
        if (labelNameSO.getText().equals("Windows")) {
            Alert dialogAlert = new Alert(Alert.AlertType.INFORMATION);
            dialogAlert.setTitle("Disk Usage Info");
            dialogAlert.setHeaderText(null);
            dialogAlert.setContentText("Not avaible in your system.");
            dialogAlert.initStyle(StageStyle.UTILITY);
            dialogAlert.showAndWait();
        }
    }

    @FXML
    private void getUSBDevices() {
        if (labelNameSO.getText().equals("Linux")) {
            Alert dialogAlert = new Alert(Alert.AlertType.INFORMATION);
            dialogAlert.setTitle("USB Devices");
            dialogAlert.setHeaderText(null);
            dialogAlert.setContentText(executeTerminal("lsusb"));
            dialogAlert.initStyle(StageStyle.UTILITY);
            dialogAlert.showAndWait();
        }
        if (labelNameSO.getText().equals("Windows")) {
            Alert dialogAlert = new Alert(Alert.AlertType.INFORMATION);
            dialogAlert.setTitle("USB Devices");
            dialogAlert.setHeaderText(null);
            dialogAlert.setContentText("Not avaible in your system.");
            dialogAlert.initStyle(StageStyle.UTILITY);
            dialogAlert.showAndWait();
        }
    }

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
}

