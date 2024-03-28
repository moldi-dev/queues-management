package org.moldidev.main;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {
    /*
    * @param stage
    * @return void
    *
    * The start method sets up the application's main view (scene).
    */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("application-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        stage.setTitle("Queues management application made by Moldovan Darius-Andrei, 30421");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> System.exit(0));
        stage.show();
    }

    /*
    * @param args
    * @return void
    *
    * The application's main method calls the launch() method in order to open up the main view.
    */
    public static void main(String[] args) {
        launch();
    }
}