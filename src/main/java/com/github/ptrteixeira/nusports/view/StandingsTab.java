package com.github.ptrteixeira.nusports.view;

import com.github.ptrteixeira.nusports.model.Standing;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * @author Peter Teixeira
 */
final class StandingsTab extends AbstractViewTab<Standing> {
  private static final Logger logger = LogManager.getLogger();
  private ObservableList<Standing> tableContents;

  StandingsTab() {
    super();
    this.setText("Standings");
    logger.trace("Created Standings tab.");
  }

  @Override
  final Node createCenter() {
    this.tableContents = FXCollections.observableArrayList();

    TableColumn<Standing, String> teamName = new TableColumn<>("Team");
    teamName.setCellValueFactory(new PropertyValueFactory<>("teamName"));
    TableColumn<Standing, String> conference = new TableColumn<>("Conference");
    conference.setCellValueFactory(new PropertyValueFactory<>("conference"));
    TableColumn<Standing, String> overall = new TableColumn<>("Overall");
    overall.setCellValueFactory(new PropertyValueFactory<>("overall"));

    TableView<Standing> tableView = new TableView<>(this.tableContents);
    tableView.getColumns().add(teamName);
    tableView.getColumns().add(conference);
    tableView.getColumns().add(overall);

    tableView.setId("standingsTable");

    return tableView;
  }

  @Override
  void populateTable(List<Standing> contents) {
    logger.trace("Set contents of Standings table to {}", contents);
    this.tableContents.setAll(contents);
  }
}