package com.github.ptrteixeira.nusports;

import com.github.ptrteixeira.nusports.model.Site;
import com.github.ptrteixeira.nusports.model.WebScraper;
import com.github.ptrteixeira.nusports.model.WebScraperFactory;
import com.github.ptrteixeira.nusports.presenter.MainController;
import com.github.ptrteixeira.nusports.view.KMainView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
    WebScraper scraper = new WebScraperFactory().forSite(Site.CAA);
    ExecutorService executor = new ThreadPoolExecutor(2, 6,
        500, TimeUnit.MILLISECONDS,
        new LinkedBlockingQueue<>());
    MainController controller = new MainController(executor, scraper);
    KMainView view = new KMainView(controller);


    Scene scene = new Scene(view.getRoot());

    primaryStage.setTitle("NU Sports");
    primaryStage.setScene(scene);
    primaryStage.setOnCloseRequest(windowEvent -> {
      executor.shutdown();
    });

    primaryStage.show();
  }
}
