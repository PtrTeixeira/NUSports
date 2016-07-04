package github.ptrteixeira.model;

import javafx.beans.property.SimpleStringProperty;

import java.util.Objects;

/**
 * JavaBean wrapper for sports win/loss records records.
 * 
 * In context, used to generate relative standings between teams by sorting 
 * by their win/loss records.
 * 
 * @author Peter
 */
public final class Standing {
    private final SimpleStringProperty teamName;
    private final SimpleStringProperty conference;
    private final SimpleStringProperty overall;
    
    Standing(String teamName, String conference, String overall) {
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
    public String toString() {
        return "Standing{" +
            "teamName=" + teamName +
            ", conference=" + conference +
            ", overall=" + overall +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Standing standing = (Standing) o;
        return Objects.equals(teamName, standing.teamName) &&
            Objects.equals(conference, standing.conference) &&
            Objects.equals(overall, standing.overall);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamName, conference, overall);
    }
}
