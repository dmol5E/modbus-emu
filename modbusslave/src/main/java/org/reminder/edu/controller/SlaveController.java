package org.reminder.edu.controller;

import java.net.URL;
import java.util.ResourceBundle;

import org.reminder.edu.modbusslave.ModBusSecondary;
import org.reminder.edu.modbusslave.comm.DataRegisterSensor;
import org.reminder.edu.modbusslave.logging.LogAppenderManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fazecast.jSerialComm.SerialPort;
import com.google.inject.Inject;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;

public class SlaveController implements Initializable {

    private static final Logger logger = LoggerFactory.getLogger(SlaveController.class);

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
    private TableView<DataRegisterSensor> registersTable;

    @FXML
    private TableColumn<DataRegisterSensor, Integer> registerNumberCol;

    @FXML
    private TableColumn<DataRegisterSensor, Integer> registerValueCol;

    @FXML
    private TableColumn<DataRegisterSensor, String> commentCol;

    @FXML
    private TableView<DigitalOutRow> digOutsTable;

    @FXML
    private TableColumn<DigitalOutRow, Integer> digOutAdressCol;

    @FXML
    private TableColumn<DigitalOutRow, Integer> digOutValueCol;

    @FXML
    private TableColumn<DigitalOutRow, String> digOutTargetCol;

    @FXML
    private TableColumn<DigitalOutRow, String> digOutCommentCol;

    @FXML
    private TextArea logArea;

    private final ModBusSecondary model;

    @Inject
    public SlaveController(ModBusSecondary manager) {
        this.model = manager;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        registersTable.getItems().addAll(model.getSensors());
        digOutsTable.getItems().addAll(
                DigitalOutRow.generateDigitalOutRow(model.getMappers()));
        final ObservableList<String> parityItems = parity.getItems();
        parityItems.addAll(model.getParityValues());
        parity.getSelectionModel().select(0);

        final ObservableList<String> portNamesItems = portNames.getItems();
        SerialPort[] ports = SerialPort.getCommPorts();
        for (SerialPort serialPort : ports) {
            portNamesItems.add(serialPort.getSystemPortName());
        }
        portNames.setValue(portNames.getItems().get(0));

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

        final ObservableList<String> stopBitsItems = stopBits.getItems();
        stopBitsItems.add("1");
        stopBitsItems.add("1.5");
        stopBitsItems.add("2");
        stopBits.getSelectionModel().select(0);

        final ObservableList<String> flowControlItems = flowControl.getItems();
        flowControlItems.add("None");
        // flowControlItems.add("xon/xoff out");
        flowControlItems.add("xon/xoff in");
        flowControlItems.add("rts/cts in");
        // flowControlItems.add("rts/cts out");
        flowControl.getSelectionModel().select(0);

        // Register the TextArea with the logging system to display log messages
        LogAppenderManager.registerTextArea(logArea);

        registerNumberCol.setCellValueFactory(
                cellData -> cellData.getValue().getRegisterNumber().asObject());
        registerValueCol.setCellValueFactory(
                cellData -> cellData.getValue().getRegisterValue().asObject());
        commentCol.setCellValueFactory(
                cellData -> cellData.getValue().getComment());

        digOutAdressCol.setCellValueFactory(
                cellData -> cellData.getValue().getDigOutAdress().asObject());
        digOutCommentCol.setCellValueFactory(
                cellData -> cellData.getValue().getDigOutComment());
        digOutTargetCol.setCellValueFactory(
                cellData -> cellData.getValue().getDigOutTarget());
        digOutValueCol.setCellValueFactory(
                cellData -> cellData.getValue().getDigOutValue().asObject());
    }

    @FXML
    private void handleOpenConnection(ActionEvent event) {
        this.logArea.clear();
        logger.info("Opening Modbus connection");

        model.setPortName(portNames.getValue());
        model.setBaudRate(baudRate.getValue());
        model.setDataBits(dataBits.getValue());
        model.setParity(parity.getValue());
        model.setStopBits(stopBits.getValue());
        model.setFlowControl(flowControl.getValue());

        model.startModbusListener();
        logger.info("Modbus connection opened on port: {}", portNames.getValue());
    }

    @FXML
    private void handleCloseConnection(ActionEvent event) {
        logger.info("Closing Modbus connection");
        model.stopModbusListener();
        logger.info("Modbus connection closed");
    }

    public void cleanup() {
        LogAppenderManager.unregisterTextArea();
    }
}
