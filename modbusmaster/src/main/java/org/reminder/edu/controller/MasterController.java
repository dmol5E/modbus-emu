package org.reminder.edu.controller;

import com.fazecast.jSerialComm.SerialPort;
import com.google.inject.Inject;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.reminder.edu.modbuscommon.entity.enums.SensorType;
import org.reminder.edu.modbusmaster.entity.SensorProxy;
import org.reminder.edu.model.MasterModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MasterController implements Initializable {

    private static final Logger logger = LoggerFactory.getLogger(MasterController.class);

    @FXML
    private TextField slaveId;

    @FXML
    private ComboBox<String> portNames;

    @FXML
    private ComboBox<Integer> dataBits;

    @FXML
    private ComboBox<Integer> baudRate;

    @FXML
    private ComboBox<String> parity;

    @FXML
    private ComboBox<String> stopBits;

    @FXML
    private ComboBox<String> flowControl;

    @FXML
    private Button btnOpen;

    @FXML
    private Button btnClose;

    @FXML
    private Button td1;

    @FXML
    private Button td2;

    @FXML
    private Button dd3;

    @FXML
    private Button dd4;

    @FXML
    private Button pk5;

    @FXML
    private Button tk6;

    @FXML
    private Button tk7;

    @FXML
    private Button dv8;

    @FXML
    private Button dv9;

    @FXML
    private Button ds10;

    @FXML
    private Button ds11;

    @FXML
    private Button ds12;

    @FXML
    private Button do13;

    @FXML
    private Button do14;

    @FXML
    private Button do15;

    @FXML
    private TextArea logArea;

    @FXML
    private TextArea td1Value;

    @FXML
    private TextArea td2Value;

    @FXML
    private TextArea dd3Value;

    @FXML
    private TextArea dd4Value;

    @FXML
    private TextArea dv8Value;

    @FXML
    private TextArea dv9Value;

    private final MasterModel model;

    @Inject
    public MasterController(MasterModel model) {
        this.model = model;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        slaveId.setText("1");

        final ObservableList<String> portNamesItems = portNames.getItems();
        SerialPort[] ports = SerialPort.getCommPorts();
        for (SerialPort serialPort : ports) {
            portNamesItems.add(serialPort.getSystemPortName());
        }
        portNamesItems.add("/tmp/ttyV1");

        final ObservableList<Integer> dataBitsItems = dataBits.getItems();
        dataBitsItems.add(4);
        dataBitsItems.add(5);
        dataBitsItems.add(6);
        dataBitsItems.add(7);
        dataBitsItems.add(8);
        dataBits.getSelectionModel().select(4);

        final ObservableList<Integer> baudRateItems = baudRate.getItems();
        baudRateItems.add(2400);
        baudRateItems.add(4800);
        baudRateItems.add(7200);
        baudRateItems.add(9600);
        baudRateItems.add(14400);
        baudRate.getSelectionModel().select(3);

        final ObservableList<String> parityItems = parity.getItems();
        parityItems.add("None");
        parityItems.add("Even");
        parityItems.add("Odd");
        parityItems.add("Space");
        parity.getSelectionModel().select(0);

        final ObservableList<String> stopBitsItems = stopBits.getItems();
        stopBitsItems.add("1");
        stopBitsItems.add("1.5");
        stopBitsItems.add("2");
        stopBits.getSelectionModel().select(0);

        final ObservableList<String> flowControlItems = flowControl.getItems();
        flowControlItems.add("None");
        flowControl.getSelectionModel().select(0);

        Collection<Button> sensorButtons = new ArrayList<Button>(15);
        sensorButtons.add(td1);
        sensorButtons.add(td2);
        sensorButtons.add(dd3);
        sensorButtons.add(dd4);
        sensorButtons.add(pk5);
        sensorButtons.add(tk6);
        sensorButtons.add(tk7);
        sensorButtons.add(dv8);
        sensorButtons.add(dv9);
        sensorButtons.add(ds10);
        sensorButtons.add(ds11);
        sensorButtons.add(ds12);
        sensorButtons.add(do13);
        sensorButtons.add(do14);
        sensorButtons.add(do15);

        Iterator<SensorProxy> proxies = model.getSensors().iterator();
        for (Button button : sensorButtons) {
            if (proxies.hasNext()) {
                proxies.next().setButton(button);
            }
        }

        List<TextArea> valueAreas = List.of(td1Value, td2Value, dd3Value, dd4Value, dv8Value, dv9Value);
        List<SensorProxy> numericProxies = model.getSensors().stream()
            .filter(p -> p.getType() == SensorType.THERMAL ||
                         p.getType() == SensorType.SMOKE ||
                         p.getType() == SensorType.PRESSURE)
            .collect(Collectors.toList());
        for (int i = 0; i < Math.min(valueAreas.size(), numericProxies.size()); i++) {
            valueAreas.get(i).setEditable(false);
            valueAreas.get(i).textProperty().bind(numericProxies.get(i).valueDisplayProperty());
        }
    }

    @FXML
    private void handleOpenConnection(ActionEvent event) {
        logArea.clear();

        if (portNames.getValue() == null) {
            logArea.appendText("Error: No port selected\n");
            return;
        }

        try {
            model.openConnection(
                portNames.getValue(),
                baudRate.getValue(),
                dataBits.getValue(),
                parity.getValue(),
                stopBits.getValue(),
                Integer.parseInt(slaveId.getText())
            );
            logArea.appendText("Connection opened successfully\n");
            logger.info("Connection opened on port {}", portNames.getValue());
        } catch (Exception e) {
            logArea.appendText("Error opening connection: " + e.getMessage() + "\n");
            logger.error("Error opening connection", e);
        }
    }

    @FXML
    private void handleCloseConnection(ActionEvent event) {
        try {
            model.closeConnection();
            logArea.appendText("Connection closed\n");
            logger.info("Connection closed");
        } catch (Exception e) {
            logArea.appendText("Error closing connection: " + e.getMessage() + "\n");
            logger.error("Error closing connection", e);
        }
    }

    @FXML
    private void handleSensorStateRequest(ActionEvent event) {
        if (!model.isOpenConnection()) {
            logArea.appendText("Error: Not connected\n");
            return;
        }

        int updated = 0;
        Collection<SensorProxy> sensors = model.getSensors();
        for (SensorProxy sensor : sensors) {
            try {
                sensor.update();
                updated++;
            } catch (Exception e) {
                logger.error("Error updating sensor {}: {}", sensor.getName(), e.getMessage());
            }
        }
        logArea.appendText("Sensor states updated: " + updated + "/" + sensors.size() + "\n");
    }
}
