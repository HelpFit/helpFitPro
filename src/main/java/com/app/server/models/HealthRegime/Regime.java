package com.app.server.models.HealthRegime;

import java.util.Date;
import java.util.List;

public class Regime {

    String id;
    String helpFitID;
    String name;
    String description;
    String regimeType;
    String timeRequiredWeeks;
    List<String> ailmentTags;
    List<String> insterestTags;
    List<String> habitsTags;
    String isActive;
    String timeCreated;
    String timeUpdated;

    public Regime(String helpFitID, String name, String description, String regimeType, String timeRequiredWeeks, List<String> ailmentTags, List<String> insterestTags, List<String> habitsTags, String isActive, String timeCreated, String timeUpdated) {
        this.helpFitID = helpFitID;
        this.name = name;
        this.description = description;
        this.regimeType = regimeType;
        this.timeRequiredWeeks = timeRequiredWeeks;
        this.ailmentTags = ailmentTags;
        this.insterestTags = insterestTags;
        this.habitsTags = habitsTags;
        this.isActive = isActive;
        this.timeCreated = timeCreated;
        this.timeUpdated = timeUpdated;
    }

    public String getHelpFitID() {
        return helpFitID;
    }

    public void setHelpFitID(String helpFitID) {
        this.helpFitID = helpFitID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRegimeType() {
        return regimeType;
    }

    public void setRegimeType(String regimeType) {
        this.regimeType = regimeType;
    }

    public String getTimeRequiredWeeks() {
        return timeRequiredWeeks;
    }

    public void setTimeRequiredWeeks(String timeRequiredWeeks) {
        this.timeRequiredWeeks = timeRequiredWeeks;
    }

    public List<String> getAilmentTags() {
        return ailmentTags;
    }

    public void setAilmentTags(List<String> ailmentTags) {
        this.ailmentTags = ailmentTags;
    }

    public List<String> getInsterestTags() {
        return insterestTags;
    }

    public void setInsterestTags(List<String> insterestTags) {
        this.insterestTags = insterestTags;
    }

    public List<String> getHabitsTags() {
        return habitsTags;
    }

    public void setHabitsTags(List<String> habitsTags) {
        this.habitsTags = habitsTags;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public String getTimeCreated() { return timeCreated; }

    public void setTimeCreated(String timeCreated) { this.timeCreated = timeCreated;}

    public String getTimeUpdated() {return timeUpdated;}

    public void setTimeUpdated(String timeUpdated) {this.timeUpdated = timeUpdated;}

    @Override
    public String toString() {
        return "Regime{" +
                "id='" + id + '\'' +
                ", helpFitID='" + helpFitID + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", regimeType='" + regimeType + '\'' +
                ", timeRequiredWeeks='" + timeRequiredWeeks + '\'' +
                ", ailmentTags=" + ailmentTags +
                ", insterestTags=" + insterestTags +
                ", habitsTags=" + habitsTags +
                ", isActive='" + isActive + '\'' +
                ", timeCreated=" + timeCreated +
                ", timeUpdated=" + timeUpdated +
                '}';
    }
}
