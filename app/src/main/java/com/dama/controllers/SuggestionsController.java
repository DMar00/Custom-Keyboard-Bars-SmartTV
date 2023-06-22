package com.dama.controllers;

import android.content.Context;
import com.dama.generators.SuggestionsGenerator;
import com.dama.utils.BarStatus;
import com.dama.utils.Cell;

public abstract class SuggestionsController {
    public static final int N_SUG_FOR_BAR = 3;
    private int totSuggestions;
    private SuggestionsGenerator suggestionsGenerator;
    private BarStatus bar1;
    private BarStatus bar2;
    private int[] suggestionsColPositions;

    public SuggestionsController(Context context) {
        suggestionsGenerator = new SuggestionsGenerator(context);
        bar1 = new BarStatus();
        bar2 = new BarStatus();
        suggestionsColPositions = new int[N_SUG_FOR_BAR];
        totSuggestions = -1;
    }


    /***************GENERATE SUGGESTIONS*****************/
    public char[] getSuggestionsChars(String sequence){
        char[] allSuggestions = suggestionsGenerator.getSuggestionArray(sequence);
        return allSuggestions;
    }

    /***************COL INDEXES*****************/

    public void setSuggestionsColPositions(Cell clicked){
        suggestionsColPositions = generateSuggestionsColPositions(clicked);
    }

    private int[] generateSuggestionsColPositions(Cell cell){
        int position = cell.getCol();
        int[] indexes = new int[N_SUG_FOR_BAR];
        indexes[0] = position-1;
        indexes[1] = position;
        indexes[2] = position+1;
        return indexes;
    }

    /***************ABSTRACT*****************/

    public abstract void setBarsRowPosition(Cell clicked);
    public abstract void setShownBars(boolean shown);
    public  abstract boolean areShownBars();

    /***************GETTERS*****************/

    public BarStatus getBar1() {
        return bar1;
    }

    public BarStatus getBar2() {
        return bar2;
    }

    public int[] getSuggestionsColPositions() {
        return suggestionsColPositions;
    }

    public int getTotSuggestions() {
        return totSuggestions;
    }

    /***************SETTERS*****************/

    protected void setTotSuggestions(int totSuggestions) {
        this.totSuggestions = totSuggestions;
    }
}
