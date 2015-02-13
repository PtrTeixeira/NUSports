package nusports;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 * @author Peter
 * @version 0.1
 */
public class NUSports extends Application {
    private final TableView actionTarget = new TableView();
    private final ComboBox<String> sports = new ComboBox<>();
    private final ComboBox<String> options = new ComboBox();
    
    private final OutputGenerator og = new OutputGenerator(actionTarget);
    
    
    @Override
    public void start(Stage primaryStage) {
        BorderPane border = new BorderPane();
        
        VBox leftMenu = this.addSideBar();
        border.setLeft(leftMenu);
        
        this.setResponseText();
        border.setCenter(actionTarget);
        
        Scene scene = new Scene(border, 800, 800);
        
        primaryStage.setTitle("NU Sports");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
        /*
        try {
            new WebScraper().();
        }
        catch(IOException e) {
            System.err.println("No Connection.");
        }
        */
    }
    
    private VBox addSideBar() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(8);
        
        Text title = new Text("Settings");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        vbox.getChildren().add(title);
        
        sports.getItems().addAll("Baseball", 
                                 "Men's Basketball", 
                                 "Men's Soccer", 
                                 "Women's Basketball", 
                                 "Women's Soccer", 
                                 "Volleyball");
        sports.setValue("Baseball");
        sports.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                setResponseText();
            }
        });
        
        options.getItems().addAll("League Standings",
                                   "Schedule/Results");
        options.setValue("League Standings");
        options.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                setResponseText();
            }
        });
        
        vbox.getChildren().add(sports);
        vbox.getChildren().add(options);
        
        
        return vbox;
    }
    
    private void setResponseText() {
        og.resetTable(this.sports.getValue(), this.options.getValue());
    }
}
