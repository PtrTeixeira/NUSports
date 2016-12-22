package com.github.ptrteixeira.nusports;

import com.github.ptrteixeira.nusports.model.Site;
import com.github.ptrteixeira.nusports.model.WebScraper;
import com.github.ptrteixeira.nusports.model.WebScraperFactory;
import com.github.ptrteixeira.nusports.presenter.MainPresenter;
import com.github.ptrteixeira.nusports.view.MainView;
import com.github.ptrteixeira.nusports.view.ViewPresenter;
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
    ViewPresenter view = new MainView();
    WebScraper scraper = new WebScraperFactory().forSite(Site.CAA);
    ExecutorService executor = new ThreadPoolExecutor(2, 6,
        500, TimeUnit.MILLISECONDS,
        new LinkedBlockingQueue<>());

    MainPresenter presenter = new MainPresenter(scraper, view, executor);


    Scene scene = new Scene(view.createView());
    presenter.loadPresenter();

    primaryStage.setTitle("NU Sports");
    primaryStage.setScene(scene);
    primaryStage.setOnCloseRequest(windowEvent -> {
      executor.shutdown();
    });

    primaryStage.show();
  }
}