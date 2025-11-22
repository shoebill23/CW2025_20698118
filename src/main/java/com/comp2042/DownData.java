package com.comp2042;

public record DownData (ClearRow clearRow, ViewData viewData) {

    public ClearRow getClearRow() {
        return clearRow;
    }

    public ViewData getViewData() {
        return viewData;
    }
}
