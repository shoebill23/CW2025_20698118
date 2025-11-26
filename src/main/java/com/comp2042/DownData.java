package com.comp2042;

public record DownData (ClearRow clearRow, ViewData viewData) { //Passes the data for the down movement of the brick

    public ClearRow getClearRow() {
        return clearRow;
    }

    public ViewData getViewData() {
        return viewData;
    }
}