package org.reminder.edu.controller;

import java.net.URL;
import java.util.ResourceBundle;

import org.reminder.edu.modbusslave.ModBusSecondary;
import org.reminder.edu.modbuscommon.entity.Sensor;
import org.reminder.edu.modbuscommon.entity.enums.SensorState;
import org.reminder.edu.modbuscommon.entity.enums.SensorType;
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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

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
    private Button btnOpen;

    @FXML
    private Button btnClose;

    @FXML
    private TableView<Sensor> sensorsTable;

    @FXML
    private TableColumn<Sensor, Integer> sensorIdCol;

    @FXML
    private TableColumn<Sensor, SensorType> sensorTypeCol;

    @FXML
    private TableColumn<Sensor, String> sensorNameCol;

    @FXML
    private TableColumn<Sensor, Integer> sensorAddressCol;

    @FXML
    private TableColumn<Sensor, Boolean> sensorEnabledCol;

    @FXML
    private TableColumn<Sensor, SensorState> sensorStateCol;

    @FXML
    private TableColumn<Sensor, String> sensorValueCol;

    @FXML
    private TableColumn<Sensor, Void> sensorActionsCol;

    @FXML
    private TextArea logArea;

    private final ModBusSecondary model;

    @Inject
    public SlaveController(ModBusSecondary manager) {
        this.model = manager;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sensorsTable.getItems().addAll(model.getSensors());

        final ObservableList<String> parityItems = parity.getItems();
        parityItems.addAll(model.getParityValues());
        parity.getSelectionModel().select(0);

        final ObservableList<String> portNamesItems = portNames.getItems();
        SerialPort[] ports = SerialPort.getCommPorts();
        for (SerialPort serialPort : ports) {
            portNamesItems.add(serialPort.getSystemPortName());
        }
        portNamesItems.add("/tmp/ttyV0");
        if (!portNamesItems.isEmpty()) {
            portNames.setValue(portNamesItems.get(0));
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

        LogAppenderManager.registerTextArea(logArea);

        setupSensorTableColumns();
    }

    private void setupSensorTableColumns() {
        sensorIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        sensorTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        sensorNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        sensorAddressCol.setCellValueFactory(new PropertyValueFactory<>("modbusAddress"));

        sensorEnabledCol.setCellValueFactory(new PropertyValueFactory<>("enabled"));
        sensorEnabledCol.setCellFactory(column -> new TableCell<Sensor, Boolean>() {
            private final CheckBox checkBox = new CheckBox();

            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    checkBox.setSelected(item);
                    checkBox.setOnAction(e -> {
                        Sensor sensor = getTableView().getItems().get(getIndex());
                        sensor.setEnabled(checkBox.isSelected());
                        logger.info("Sensor {} enabled: {}", sensor.getName(), checkBox.isSelected());
                    });
                    setGraphic(checkBox);
                }
            }
        });

        sensorStateCol.setCellValueFactory(new PropertyValueFactory<>("state"));

        sensorValueCol.setCellValueFactory(new PropertyValueFactory<>("valueDisplay"));

        sensorActionsCol.setCellFactory(createActionButtonCellFactory());
    }

    private Callback<TableColumn<Sensor, Void>, TableCell<Sensor, Void>> createActionButtonCellFactory() {
        return new Callback<TableColumn<Sensor, Void>, TableCell<Sensor, Void>>() {
            @Override
            public TableCell<Sensor, Void> call(TableColumn<Sensor, Void> param) {
                return new TableCell<Sensor, Void>() {
                    private final Button resetBtn = new Button("Сброс");
                    private final Button alarmBtn = new Button("Тревога");
                    private final Button faultBtn = new Button("Сбой");
                    private final HBox buttons = new HBox(5, resetBtn, alarmBtn, faultBtn);

                    {
                        resetBtn.setOnAction(e -> {
                            Sensor sensor = getTableView().getItems().get(getIndex());
                            sensor.resetToDefault();
                            logger.info("Sensor {} reset to default", sensor.getName());
                        });
                        alarmBtn.setOnAction(e -> {
                            Sensor sensor = getTableView().getItems().get(getIndex());
                            sensor.setState(SensorState.ALARM);
                            logger.info("Sensor {} set to ALARM", sensor.getName());
                        });
                        faultBtn.setOnAction(e -> {
                            Sensor sensor = getTableView().getItems().get(getIndex());
                            sensor.setState(SensorState.FAULT);
                            logger.info("Sensor {} set to FAULT", sensor.getName());
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(buttons);
                        }
                    }
                };
            }
        };
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
