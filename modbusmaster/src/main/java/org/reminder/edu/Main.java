package org.reminder.edu;

import org.reminder.edu.modbusmaster.config.ModbusMasterModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Injector injector = Guice.createInjector(new ModbusMasterModule());

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Master.fxml"));
        loader.setControllerFactory(injector::getInstance);

        Parent root = loader.load();

        Scene scene = new Scene(root);

        primaryStage.setTitle("ModBus Master");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}
