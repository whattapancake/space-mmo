package com.spacegame.eventbus;

import java.util.ArrayList;

/**
 *
 * @author Brendan
 */
public class EventBus {

    private static EventBus eventBus;

    public enum Event {
        EVENT_TEST_NOTIFICATION,
        EVENT_PLAYER_POSITION,
        EVENT_PLAYER_ROTATION,
        EVENT_CAM_SWITCH;
    }
    private ArrayList<Observer> observers;

    private EventBus() {
        observers = new ArrayList();
    }

    //STATIC ACCESS METHODS
    public static EventBus getInstance() {
        if (eventBus == null) {
            eventBus = new EventBus();
        }
        return eventBus;
    }

    public static void notify(Object entity, Event e) {
        getInstance().notifyObservers(entity, e);
    }

    public static void registerObserver(Observer o) {
        getInstance().addObserver(o);
    }

    public static void unregisterObserver(Observer o) {
        getInstance().removeObserver(o);
    }

    //UNDERLYING STRUCTURE
    private void notifyObservers(Object entity, Event e) {
        for (Observer o : observers) {
            o.onNotify(entity, e);
        }
    }

    private void addObserver(Observer o) {
        observers.add(o);
    }

    private void removeObserver(Observer o) {
        observers.remove(o);
    }
}