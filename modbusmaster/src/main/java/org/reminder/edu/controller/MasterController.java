package org.reminder.edu.controller;

import com.fazecast.jSerialComm.SerialPort;
import com.google.inject.Inject;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import org.reminder.edu.common.javafx.logging.LogAppenderManager;
import org.reminder.edu.modbuscommon.entity.enums.SensorState;
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
    private FlowPane sensorCardsContainer;

    @FXML
    private TextArea logArea;

    private final MasterModel model;
    private final Map<SensorProxy, SensorState> lastStates = new HashMap<>();
    private final Map<SensorProxy, String> lastValues = new HashMap<>();

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

        for (SensorProxy proxy : model.getSensors()) {
            VBox card = createSensorCard(proxy);
            sensorCardsContainer.getChildren().add(card);
        }

        LogAppenderManager.registerTextArea(logArea);
    }

    private VBox createSensorCard(SensorProxy proxy) {
        VBox card = new VBox(5.0);
        card.setPrefWidth(150.0);
        card.setPrefHeight(130.0);
        card.setMinWidth(150.0);
        card.setMinHeight(130.0);
        card.setMaxWidth(150.0);
        card.setMaxHeight(130.0);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(8.0));
        card.getStyleClass().add("sensor-card");

        Label shortNameLabel = new Label();
        shortNameLabel.getStyleClass().add("sensor-card-shortname");
        shortNameLabel.textProperty().bind(proxy.shortNameProperty());

        Label typeLabel = new Label(proxy.getType().name());
        typeLabel.getStyleClass().add("sensor-card-type");

        Label valueLabel = new Label();
        valueLabel.getStyleClass().add("sensor-card-value");
        valueLabel.textProperty().bind(proxy.valueDisplayProperty());

        Label nameLabel = new Label();
        nameLabel.getStyleClass().add("sensor-card-name");
        nameLabel.textProperty().bind(proxy.nameProperty());
        nameLabel.setWrapText(true);

        Label addrLabel = new Label("Адрес: " + proxy.getModbusAddress());
        addrLabel.getStyleClass().add("sensor-card-addr");

        card.getChildren().addAll(shortNameLabel, typeLabel, valueLabel, nameLabel, addrLabel);

        Runnable updateStyle = () ->
            Platform.runLater(() -> {
                card.getStyleClass().removeAll("state-normal", "state-alarm", "state-fault", "state-disabled");
                if (!proxy.isEnabled()) {
                    card.getStyleClass().add("state-disabled");
                } else {
                    switch (proxy.getState()) {
                        case NORMAL -> card.getStyleClass().add("state-normal");
                        case ALARM -> card.getStyleClass().add("state-alarm");
                        case FAULT -> card.getStyleClass().add("state-fault");
                    }
                }
            });

        proxy.stateProperty().addListener((obs, old, val) -> updateStyle.run());
        proxy.enabledProperty().addListener((obs, old, val) -> updateStyle.run());
        updateStyle.run();

        card.setOnMouseClicked(e -> showSensorDetails(proxy));

        return card;
    }

    private void showSensorDetails(SensorProxy proxy) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Детали датчика");
        alert.setHeaderText(proxy.getName());

        StringBuilder sb = new StringBuilder();
        sb.append("Тип: ").append(proxy.getType()).append("\n");
        sb.append("Адрес Modbus: ").append(proxy.getModbusAddress()).append("\n");
        sb.append("Состояние: ").append(proxy.getState()).append("\n");
        sb.append("Включён: ").append(proxy.isEnabled() ? "Да" : "Нет").append("\n");
        sb.append("Значение: ").append(proxy.getValueDisplay()).append("\n");
        sb.append("Raw значение: ").append(proxy.getValue()).append("\n");

        alert.setContentText(sb.toString());
        alert.showAndWait();
    }

    @FXML
    private void handleOpenConnection() {
        logArea.clear();

        if (portNames.getValue() == null) {
            logger.error("Error: No port selected");
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
            logger.info("Connection opened successfully on port {}", portNames.getValue());

            lastStates.clear();
            lastValues.clear();
        } catch (Exception e) {
            logger.error("Error opening connection: {}", e.getMessage());
        }
    }

    @FXML
    private void handleCloseConnection() {
        try {
            model.closeConnection();
            logger.info("Connection closed");
        } catch (Exception e) {
            logger.error("Error closing connection: {}", e.getMessage());
        }
    }

    public void cleanup() {
        try {
            if (model.isOpenConnection()) {
                model.closeConnection();
                logger.info("Connection closed on exit");
            }
        } catch (Exception e) {
            logger.error("Error closing connection on exit", e);
        }
        LogAppenderManager.unregisterTextArea();
    }

    @FXML
    private void handleSensorStateRequest() {
        if (!model.isOpenConnection()) {
            logger.error("Error: Not connected");
            return;
        }

        int updated = 0;
        Collection<SensorProxy> sensors = model.getSensors();
        for (SensorProxy sensor : sensors) {
            SensorState prevState = lastStates.get(sensor);
            String prevValue = lastValues.get(sensor);
            try {
                sensor.update();
                SensorState newState = sensor.getState();
                String newValue = sensor.getValueDisplay();

                if (prevState != null && prevState != newState) {
                    logger.info(
                        sensor.getName() +
                            ": " +
                            stateDisplay(prevState) +
                            " → " +
                            stateDisplay(newState) +
                            " (" +
                            newValue +
                            ")"
                    );
                } else if (prevValue != null && !prevValue.equals(newValue) && prevState == newState) {
                    logger.info(sensor.getName() + ": значение изменено " + prevValue + " → " + newValue);
                }

                lastStates.put(sensor, newState);
                lastValues.put(sensor, newValue);
                updated++;
            } catch (Exception e) {
                logger.error("Error updating sensor {}: {}", sensor.getName(), e.getMessage());
            }
        }
        logger.info("Sensor states updated: {}/{}", updated, sensors.size());
    }

    private String stateDisplay(SensorState state) {
        return switch (state) {
            case NORMAL -> "НОРМА";
            case ALARM -> "ТРЕВОГА";
            case FAULT -> "НЕИСПРАВНОСТЬ";
        };
    }
}
