package org.reminder.edu.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.reminder.edu.modbuscommon.entity.Sensor;
import org.reminder.edu.modbuscommon.entity.enums.SensorState;
import org.reminder.edu.modbuscommon.entity.enums.SensorType;
import org.reminder.edu.modbuscommon.entity.service.SensorBehaviorService;
import org.reminder.edu.modbusslave.ModBusSecondary;
import org.reminder.edu.common.javafx.logging.LogAppenderManager;
import org.reminder.edu.modbusslave.viewmodel.SensorViewModel;
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
    private TableView<SensorViewModel> sensorsTable;

    @FXML
    private TableColumn<SensorViewModel, Integer> sensorIdCol;

    @FXML
    private TableColumn<SensorViewModel, SensorType> sensorTypeCol;

    @FXML
    private TableColumn<SensorViewModel, String> sensorNameCol;

    @FXML
    private TableColumn<SensorViewModel, Integer> sensorAddressCol;

    @FXML
    private TableColumn<SensorViewModel, Boolean> sensorEnabledCol;

    @FXML
    private TableColumn<SensorViewModel, SensorState> sensorStateCol;

    @FXML
    private TableColumn<SensorViewModel, String> sensorValueCol;

    @FXML
    private TableColumn<SensorViewModel, Void> sensorActionsCol;

    @FXML
    private TextArea logArea;

    private final ModBusSecondary model;
    private final SensorBehaviorService sensorService;

    @Inject
    public SlaveController(ModBusSecondary manager) {
        this.model = manager;
        this.sensorService = manager.getSensorService();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<SensorViewModel> viewModels = new ArrayList<>();
        for (Sensor sensor : model.getSensors()) {
            viewModels.add(new SensorViewModel(sensor, sensorService));
        }
        sensorsTable.getItems().addAll(viewModels);

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
        sensorEnabledCol.setCellFactory(column -> new TableCell<SensorViewModel, Boolean>() {
            private final CheckBox checkBox = new CheckBox();

            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    checkBox.setSelected(item);
                    checkBox.setOnAction(e -> {
                        SensorViewModel viewModel = getTableView().getItems().get(getIndex());
                        sensorService.setEnabled(viewModel.getSensor(), checkBox.isSelected());
                        logger.info("Sensor {} enabled: {}", viewModel.getName(), checkBox.isSelected());
                    });
                    setGraphic(checkBox);
                }
            }
        });

        sensorStateCol.setCellValueFactory(new PropertyValueFactory<>("state"));

        sensorValueCol.setCellValueFactory(new PropertyValueFactory<>("valueDisplay"));

        sensorActionsCol.setCellFactory(createActionButtonCellFactory());
    }

    private Callback<TableColumn<SensorViewModel, Void>, TableCell<SensorViewModel, Void>> createActionButtonCellFactory() {
        return new Callback<TableColumn<SensorViewModel, Void>, TableCell<SensorViewModel, Void>>() {
            @Override
            public TableCell<SensorViewModel, Void> call(TableColumn<SensorViewModel, Void> param) {
                return new TableCell<SensorViewModel, Void>() {
                    private final Button resetBtn = new Button("Сброс");
                    private final Button alarmBtn = new Button("Тревога");
                    private final Button faultBtn = new Button("Сбой");
                    private final HBox buttons = new HBox(5, resetBtn, alarmBtn, faultBtn);

                    {
                        resetBtn.setOnAction(e -> {
                            SensorViewModel viewModel = getTableView().getItems().get(getIndex());
                            sensorService.resetToDefault(viewModel.getSensor());
                            logger.info("Sensor {} reset to default", viewModel.getName());
                        });
                        alarmBtn.setOnAction(e -> {
                            SensorViewModel viewModel = getTableView().getItems().get(getIndex());
                            sensorService.forceState(viewModel.getSensor(), SensorState.ALARM);
                            logger.info("Sensor {} set to ALARM", viewModel.getName());
                        });
                        faultBtn.setOnAction(e -> {
                            SensorViewModel viewModel = getTableView().getItems().get(getIndex());
                            sensorService.forceState(viewModel.getSensor(), SensorState.FAULT);
                            logger.info("Sensor {} set to FAULT", viewModel.getName());
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
        try {
            model.stopModbusListener();
            logger.info("Modbus connection closed on exit");
        } catch (Exception e) {
            logger.error("Error closing Modbus connection on exit", e);
        }
        LogAppenderManager.unregisterTextArea();
    }
}
