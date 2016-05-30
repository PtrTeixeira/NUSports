package github.ptrteixeira.model;

import javafx.beans.property.SimpleStringProperty;

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
}
