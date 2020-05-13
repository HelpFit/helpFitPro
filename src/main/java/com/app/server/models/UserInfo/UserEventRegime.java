package com.app.server.models.UserInfo;

import com.app.server.models.Event.Event;
import com.app.server.models.HealthRegime.Regime;

import java.util.List;

public class UserEventRegime {

    Regime regime;
    List<Event> eventList;

    public UserEventRegime(Regime regime, List<Event> eventList) {
        this.regime = regime;
        this.eventList = eventList;
    }

    public Regime getRegime() {
        return regime;
    }

    public void setRegime(Regime regime) {
        this.regime = regime;
    }

    public List<Event> getEventList() {
        return eventList;
    }

    public void setEventList(List<Event> eventList) {
        this.eventList = eventList;
    }
}
