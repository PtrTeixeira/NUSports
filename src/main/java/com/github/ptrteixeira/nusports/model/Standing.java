package com.github.ptrteixeira.nusports.model;

import javafx.beans.property.SimpleStringProperty;

import java.util.Objects;

/**
 * JavaBean wrapper for sports win/loss records records.
 * <p>
 * <p>In context, used to generate relative standings between teams by sorting
 * by their win/loss records.
 *
 * @author Peter
 */
public final class Standing {
  private final SimpleStringProperty teamName;
  private final SimpleStringProperty conference;
  private final SimpleStringProperty overall;

  public Standing(String teamName, String conference, String overall) {
    this.teamName = new SimpleStringProperty(teamName);
    this.conference = new SimpleStringProperty(conference);
    this.overall = new SimpleStringProperty(overall);
  }

  public String getTeamName() {
    return teamName.get();
  }

  public void setTeamName(String teamName) {
    this.teamName.set(teamName);
  }

  public String getConference() {
    return conference.get();
  }

  public void setConference(String conference) {
    this.conference.set(conference);
  }

  public String getOverall() {
    return overall.get();
  }

  public void setOverall(String overall) {
    this.overall.set(overall);
  }

  @Override
  public int hashCode() {
    return Objects.hash(teamName, conference, overall);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    Standing standing = (Standing) other;
    return Objects.equals(teamName, standing.teamName)
        && Objects.equals(conference, standing.conference)
        && Objects.equals(overall, standing.overall);
  }

  @Override
  public String toString() {
    return "Standing{teamName="
        + teamName
        + ", conference="
        + conference
        + ", overall="
        + overall
        + '}';
  }
}