package com.dama.controllers;

import android.content.Context;
import android.util.Log;
import android.widget.FrameLayout;
import com.dama.utils.Cell;
import com.dama.utils.Key;
import com.dama.utils.Utils;
import java.util.Arrays;

public class ControllerTwoBars extends Controller{
    public ControllerTwoBars(Context context, FrameLayout rootView) {
        super(context, rootView);
        setSuggestionsController(new SuggestionsControllerTwoBars(context));
    }

    @Override
    protected void addSuggestionToBars(char[] checkedSuggestions) {
        /*Log.d("checkedSuggestions","char1: "+checkedSuggestions[0]+" - char2: "+checkedSuggestions[1]+" - char3 :"+checkedSuggestions[2]);
        Log.d("checkedSuggestions","char4: "+checkedSuggestions[3]+" - char5: "+checkedSuggestions[4]+" - char6 :"+checkedSuggestions[5]);
        */
        //bars index
        int index1 = getSuggestionsController().getBar1().getRowIndex();
        int index2 = getSuggestionsController().getBar2().getRowIndex();

        //each bar has 3 suggestions
        char[] sug1 = new char[3];
        char[] sug2 = new char[3];

        //put first 2 suggestions up and down clicked letter
        sug1[1] = checkedSuggestions[0];
        sug2[1] = checkedSuggestions[1];

        //fill others
        sug1[0] = checkedSuggestions[2];
        sug1[2] = checkedSuggestions[3];
        sug2[0] = checkedSuggestions[4];
        sug2[2] = checkedSuggestions[5];

        //char[] sug1 = Arrays.copyOfRange(checkedSuggestions, 0, SuggestionsController.N_SUG_FOR_BAR);
        //char[] sug2 = Arrays.copyOfRange(checkedSuggestions, SuggestionsController.N_SUG_FOR_BAR, SuggestionsController.N_SUG_FOR_BAR*2);

        /*Log.d("indexesRow", "index1: "+index1+" - index2: "+index2);
        Log.d("Sizes", "size1: "+sug1.length+" - size2: "+sug2.length);
        Log.d("sug1","char1: "+sug1[0]+" - char2: "+sug1[1]+" - char3 :"+sug1[2]);
        Log.d("sug2","char1: "+sug2[0]+" - char2: "+sug2[1]+" - char3 :"+sug2[2]);*/

        fillBar(index1, sug1);
        fillBar(index2, sug2);
    }

    @Override
    protected void updateSuggestionsBars(char[] suggestions) {
        int indexRow1 = getSuggestionsController().getBar1().getRowIndex();
        int indexRow2 = getSuggestionsController().getBar2().getRowIndex();

        Cell curFocus = getFocusController_().getCurrentFocus();

        int focusRowIndex = -1, noFocusRowIndex = -1;
        if(curFocus.getRow()==indexRow1){
            focusRowIndex = indexRow1;
            noFocusRowIndex = indexRow2;
        }else if(curFocus.getRow()==indexRow2){
            focusRowIndex = indexRow2;
            noFocusRowIndex = indexRow1;
        }

        int[] colIndexes = getSuggestionsController().getSuggestionsColPositions();
        char[] sug = Arrays.copyOfRange(suggestions, 0, (SuggestionsController.N_SUG_FOR_BAR*2)-1); //0 to 5

        //modify key in focused Bar
        int[] cIndex = Utils.removeIntFromArray(colIndexes,curFocus.getCol()); //colIndexes - curFocus.getCol()
        Key k1 = getKeysController().getCharToKey(sug[0]);
        Key k2 = getKeysController().getCharToKey(sug[1]);
        modifyKeyContent(new Cell(focusRowIndex,cIndex[0]), k1.getCode(), k1.getLabel());
        modifyKeyContent(new Cell(focusRowIndex,cIndex[1]), k2.getCode(), k2.getLabel());

        //modify key in noFocused Bar
        Key k3 = getKeysController().getCharToKey(sug[2]);
        Key k4 = getKeysController().getCharToKey(sug[3]);
        Key k5 = getKeysController().getCharToKey(sug[4]);
        modifyKeyContent(new Cell(noFocusRowIndex,colIndexes[0]), k3.getCode(), k3.getLabel());
        modifyKeyContent(new Cell(noFocusRowIndex,colIndexes[1]), k4.getCode(), k4.getLabel());
        modifyKeyContent(new Cell(noFocusRowIndex,colIndexes[2]), k5.getCode(), k5.getLabel());
    }


}
