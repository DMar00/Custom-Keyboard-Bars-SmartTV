package com.dama.utils;

public class BarStatus {
    private int rowIndex;
    private boolean shown;

    public BarStatus() {
        rowIndex = -1;
        shown = false;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public boolean isShown() {
        return shown;
    }

    public void setShown(boolean shown) {
        this.shown = shown;
    }
}
