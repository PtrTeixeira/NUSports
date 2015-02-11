package nusports;

import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Peter
 */
public class Standing {
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
}
