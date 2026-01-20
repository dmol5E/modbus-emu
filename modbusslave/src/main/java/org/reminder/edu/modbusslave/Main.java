package org.reminder.edu.modbusslave;

import org.reminder.edu.controller.SlaveController;
import org.reminder.edu.modbusslave.config.ModbusSecondaryModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class Main extends Application {

    public static void main(String[] args) throws Exception {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Injector injector = Guice.createInjector(new ModbusSecondaryModule());
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Slave.fxml"));
        loader.setControllerFactory(injector::getInstance);
        
        Parent root = loader.load();
        
        SlaveController controller = loader.getController();
        
        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.setTitle("ModBus Slave");
        primaryStage.setResizable(false);
        
        // Set up cleanup when the window is closed
        primaryStage.setOnCloseRequest(event -> {
            if (controller != null) {
                controller.cleanup();
            }
        });
        
        primaryStage.show();
    }

}
