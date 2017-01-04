package com.github.ptrteixeira.nusports;

import com.github.ptrteixeira.nusports.view.MainView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.concurrent.ExecutorService;

/**
 * @author Peter
 * @version 0.1
 */
public class NUSports extends Application {

  /**
   * Entry point for application.
   *
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    SportsApplication application = DaggerSportsApplication.create();
    ExecutorService executor = application.executor();
    MainView view = application.mainView();


    Scene scene = new Scene(view.getRoot());

    primaryStage.setTitle("NU Sports");
    primaryStage.setScene(scene);
    primaryStage.setOnCloseRequest(windowEvent -> executor.shutdown());

    primaryStage.show();
  }
}
