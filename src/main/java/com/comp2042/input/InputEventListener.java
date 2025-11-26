package com.comp2042.input;

import com.comp2042.model.data.DownData;
import com.comp2042.model.data.MoveEvent;
import com.comp2042.model.data.ViewData;

public interface InputEventListener {

    DownData onDownEvent(MoveEvent event);

    ViewData onLeftEvent(MoveEvent event);

    ViewData onRightEvent(MoveEvent event);

    ViewData onRotateEvent(MoveEvent event);

    ViewData onHoldEvent();

    void createNewGame();
    
    DownData onHardDropEvent();
}