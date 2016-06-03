package github.ptrteixeira;

import github.ptrteixeira.model.Site;
import github.ptrteixeira.model.WebScraper;
import github.ptrteixeira.model.WebScraperFactory;
import github.ptrteixeira.presenter.MainPresenter;
import github.ptrteixeira.view.MainView;
import github.ptrteixeira.view.ViewPresenter;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * @author Peter
 * @version 0.1
 */
public class NUSports extends Application {
  private final TableView actionTarget = new TableView();
  private final ComboBox<String> sports = new ComboBox<>();
  private final ComboBox<String> options = new ComboBox<>();
  private final Text err = new Text();

  private final OutputGenerator og = new OutputGenerator(actionTarget, err);


  // Set up stage: add table, side bar
  public void start(Stage primaryStage) {
    ViewPresenter view = new MainView();
    WebScraper scraper = new WebScraperFactory().forSite(Site.CAA);

    MainPresenter presenter = new MainPresenter(scraper, view);
    presenter.loadPresenter();


    Scene scene = new Scene(view.createView());

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
