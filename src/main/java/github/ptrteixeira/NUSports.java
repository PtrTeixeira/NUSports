package github.ptrteixeira;

import github.ptrteixeira.model.Site;
import github.ptrteixeira.model.WebScraper;
import github.ptrteixeira.model.WebScraperFactory;
import github.ptrteixeira.presenter.MainPresenter;
import github.ptrteixeira.view.MainView;
import github.ptrteixeira.view.ViewPresenter;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author Peter
 * @version 0.1
 */
public class NUSports extends Application {

  public void start(Stage primaryStage) {
    ViewPresenter view = new MainView();
    WebScraper scraper = new WebScraperFactory().forSite(Site.CAA);

    MainPresenter presenter = new MainPresenter(scraper, view);


    Scene scene = new Scene(view.createView());
    presenter.loadPresenter();

    primaryStage.setTitle("NU Sports");
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    launch(args);
  }
}
