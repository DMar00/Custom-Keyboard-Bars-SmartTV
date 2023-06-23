package com.dama.controllers;

import android.content.Context;
import android.util.Log;
import android.widget.FrameLayout;
import com.dama.utils.Cell;
import com.dama.utils.Key;
import com.dama.utils.Utils;
import java.util.Arrays;

public class ControllerOneBar extends Controller{
    public ControllerOneBar(Context context, FrameLayout rootView) {
        super(context, rootView);
        setSuggestionsController(new SuggestionsControllerOneBar(context));
    }

    @Override
    protected void addSuggestionToBars(char[] checkedSuggestions) {
        int index1 = getSuggestionsController().getBar1().getRowIndex();

        char[] sug1 = Arrays.copyOfRange(checkedSuggestions, 0, SuggestionsController.N_SUG_FOR_BAR);
        Log.d("sizeOneBar",""+sug1.length);

        char[] orderedSug1 = new char[3];
        orderedSug1[0] = sug1[1];
        orderedSug1[1] = sug1[0];
        orderedSug1[2] = sug1[2];

        //fillBar(index1, sug1);
        fillBar(index1, orderedSug1);
    }

    @Override
    protected void updateSuggestionsBars(char[] suggestions) {
        Cell curFocus = getFocusController_().getCurrentFocus();

        int[] colIndexes = getSuggestionsController().getSuggestionsColPositions();
        char[] sug = Arrays.copyOfRange(suggestions, 0, (SuggestionsController.N_SUG_FOR_BAR)-1); //0 to 2

        //modify key in focused Bar
        int[] cIndex = Utils.removeIntFromArray(colIndexes,curFocus.getCol()); //colIndexes - curFocus.getCol()
        Key k1 = getKeysController().getCharToKey(sug[0]);
        Key k2 = getKeysController().getCharToKey(sug[1]);
        modifyKeyContent(new Cell(curFocus.getRow(),cIndex[0]), k1.getCode(), k1.getLabel());
        modifyKeyContent(new Cell(curFocus.getRow(),cIndex[1]), k2.getCode(), k2.getLabel());
    }
}
