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
    private final Text err = new Text();
    
    private final OutputGenerator og = new OutputGenerator(actionTarget, err);
    
    
    // Set up stage: add table, side bar
    public void start(Stage primaryStage) {
        BorderPane border = new BorderPane();
        
        VBox leftMenu = this.addSideBar();
        border.setLeft(leftMenu);
        
        this.setResponseText();
        border.setCenter(actionTarget);
        
        Scene scene = new Scene(border, 800, 800);
        scene.getStylesheets().add(
                getClass().getResource("OutputStyles.css").toExternalForm());
        
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
    
    // Add the side bar containing a header, the controls, and an error text
    private VBox addSideBar() {
        // The side bar
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(8);
        
        // Header for the side bar
        Text title = new Text("Settings");
        title.setId("title");
        
        // controls: sports options
        sports.getItems().addAll("Baseball", 
                                 "Men's Basketball", 
                                 "Men's Soccer", 
                                 "Women's Basketball", 
                                 "Women's Soccer", 
                                 "Volleyball");
        sports.setValue("Baseball");
        sports.setOnAction((ActionEvent e) -> {
            setResponseText();
        });
        // Controls: display options
        options.getItems().addAll("League Standings",
                                   "Schedule/Results");
        options.setValue("League Standings");
        options.setOnAction((ActionEvent e) -> {
            setResponseText();
        });
        
        // The error bar
        err.setId("error");
        
        // Add children to the side bar
        vbox.getChildren().add(title);
        vbox.getChildren().add(sports);
        vbox.getChildren().add(options);
        vbox.getChildren().add(err);
        
        
        return vbox;
    }
    
    // Query ouput generator to set the response text
    private void setResponseText() {
        og.resetTable(this.sports.getValue(), this.options.getValue());
    }
}
