package com.dama.controllers;

import android.content.Context;

import com.dama.utils.Cell;

public class SuggestionsControllerOneBar extends SuggestionsController{
    public SuggestionsControllerOneBar(Context context) {
        super(context);
    }

    @Override
    public void setBarsRowPosition(Cell clicked) {
        int row = clicked.getRow();
        int rowIndexBar1 = -1;
        
        if(row == 2)
            rowIndexBar1 = 3;
        else if(row == 6)
            rowIndexBar1 = 5;
        else if(row == 4) {
            if(getBar1().getRowIndex() != -1)
                rowIndexBar1 = getBar1().getRowIndex();
            else
                rowIndexBar1 = 5;
        }
       
        getBar1().setRowIndex(rowIndexBar1);
    }

    @Override
    public void setShownBars(boolean shown) {
        getBar1().setShown(shown);
    }

    @Override
    public boolean areShownBars() {
        return getBar1().isShown();
    }
}
