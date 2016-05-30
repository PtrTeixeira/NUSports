package github.ptrteixeira.model;

import javafx.beans.property.SimpleStringProperty;

/**
 * JavaBean wrapper for sports matches.
 * 
 * @author Peter
 */
public final class Match {
    private final SimpleStringProperty date;
    private final SimpleStringProperty opponent;
    private final SimpleStringProperty result;
    
    Match(String date, String opponent, String result) {
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
}