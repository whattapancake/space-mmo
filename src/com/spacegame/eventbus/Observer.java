package com.spacegame.eventbus;

import com.spacegame.eventbus.EventBus.Event;

/**
 *
 * @author Brendan
 */
public interface Observer {
    
    public void onNotify(Object entity, Event event);
}