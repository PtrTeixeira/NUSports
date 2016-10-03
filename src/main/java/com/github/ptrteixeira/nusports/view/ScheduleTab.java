package com.github.ptrteixeira.nusports.view;

import com.github.ptrteixeira.nusports.model.Match;
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
final class ScheduleTab extends AbstractViewTab<Match> {
  private static final Logger logger = LogManager.getLogger();
  private ObservableList<Match> tableContents;

  ScheduleTab() {
    super();
    this.setText("Schedule");
    logger.trace("Created Schedule tab.");
  }

  @Override
  Node createCenter() {
    this.tableContents = FXCollections.observableArrayList();

    TableColumn<Match, String> date = new TableColumn<>("Date");
    date.setCellValueFactory(new PropertyValueFactory<>("date"));
    TableColumn<Match, String> opponent = new TableColumn<>("Opponent");
    opponent.setCellValueFactory(new PropertyValueFactory<>("opponent"));
    TableColumn<Match, String> result = new TableColumn<>("Result");
    result.setCellValueFactory(new PropertyValueFactory<>("result"));

    TableView<Match> tableView = new TableView<>(this.tableContents);
    tableView.getColumns().add(date);
    tableView.getColumns().add(opponent);
    tableView.getColumns().add(result);

    tableView.setId("scheduleTable");

    return tableView;
  }

  @Override
  void populateTable(List<Match> contents) {
    logger.trace("Set contents of Schedule table to {}", contents);
    this.tableContents.setAll(contents);
  }
}
