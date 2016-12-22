package com.github.ptrteixeira.nusports.model;

import javafx.beans.property.SimpleStringProperty;

import java.util.Objects;

/**
 * JavaBean wrapper for sports matches.
 *
 * @author Peter
 */
public final class Match {
  private final SimpleStringProperty date;
  private final SimpleStringProperty opponent;
  private final SimpleStringProperty result;

  public Match(String date, String opponent, String result) {
    this.date = new SimpleStringProperty(date);
    this.opponent = new SimpleStringProperty(opponent);
    this.result = new SimpleStringProperty(result);
  }

  public String getDate() {
    return date.get();
  }

  public void setDate(String date) {
    this.date.set(date);
  }

  public String getOpponent() {
    return opponent.get();
  }

  public void setOpponent(String opponent) {
    this.opponent.set(opponent);
  }

  public String getResult() {
    return result.get();
  }

  public void setResult(String result) {
    this.result.set(result);
  }

  @Override
  public int hashCode() {
    return Objects.hash(date, opponent, result);
  }


  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    Match match = (Match) other;
    return Objects.equals(date, match.date)
        && Objects.equals(opponent, match.opponent)
        && Objects.equals(result, match.result);
  }

  @Override
  public String toString() {
    return "Match{"
        + "date=" + date
        + ", opponent="
        + opponent
        + ", result="
        + result
        + '}';
  }
}