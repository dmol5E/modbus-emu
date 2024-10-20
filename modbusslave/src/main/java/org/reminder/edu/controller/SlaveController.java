package org.reminder.edu.controller;

import java.net.URL;
import java.util.Enumeration;
import java.util.ResourceBundle;

import org.reminder.edu.modbusslave.ApplicationManager;
import org.reminder.edu.modbusslave.MessageRenderer;
import org.reminder.edu.modbusslave.comm.DataRegisterSensor;

import com.fazecast.jSerialComm.SerialPort;

import gnu.io.CommPortIdentifier;
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

    private ApplicationManager model;
    private MessageRenderer messageRenderer;

    public SlaveController() {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        final ObservableList<String> portNamesItems = portNames.getItems();
        SerialPort[] ports = SerialPort.getCommPorts();
        for(SerialPort serialPort: ports) {
                portNamesItems.add(serialPort.getSystemPortName());
        }

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

        this.messageRenderer = new TextAreaAdapter(logArea);

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

        model.setPortName(portNames.getValue());
        model.setBaudRate(baudRate.getValue());
        model.setDataBits(dataBits.getValue());
        model.setParity(parity.getValue());
        model.setStopBits(stopBits.getValue());
        model.setFlowControl(flowControl.getValue());

        model.startModbusListener();
    }

    @FXML
    private void handleCloseConnection(ActionEvent event) {
        model.stopModbusListener();
    }

    public void setApplicationManager(ApplicationManager model) {
        this.model = model;
        model.setRenderer(messageRenderer);
        registersTable.getItems().addAll(model.getSensors());
        digOutsTable.getItems().addAll(
                DigitalOutRow.generateDigitalOutRow(model.getMappers()));
                final ObservableList<String> parityItems = parity.getItems();
        parityItems.addAll(model.getParityValues());
        parity.getSelectionModel().select(0);
    }
}
