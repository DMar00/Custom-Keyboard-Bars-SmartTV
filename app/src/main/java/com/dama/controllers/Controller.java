package com.dama.controllers;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import com.dama.customkeyboardbarstv.R;
import com.dama.utils.Cell;
import com.dama.utils.Key;
import com.dama.utils.Utils;
import java.util.ArrayList;


public abstract class Controller {
    public static final int COLS = 10 + 2;  //todo mod +2 added
    public static final int ROWS = 5 + 4;
    public static final int INVALID_KEY = -1;
    public static final int HIDDEN_KEY = -3;
    public static final int SPACE_KEY = 32;
    public static final int FAKE_KEY = -66;
    public static final int ENTER_KEY = 66;
    public static final int DEL_KEY = 24;
    public static final int[] barsIndexes = {1,3,5,7};
    private TextController textController;
    private FocusController focusController;
    private KeysController keysController;
    private SuggestionsController suggestionsController;
    private ViewsController viewsController;

    public Controller(Context context, FrameLayout rootView) {
        keysController = new KeysController(new Keyboard(context, R.xml.querty));
        textController = new TextController();
        focusController = new FocusController();
        focusController.setCurrentFocus(new Cell(2,1)); //q
        viewsController = new ViewsController(rootView);
        viewsController.drawKeyboard(keysController.getAllKeys(), focusController.getCurrentFocus());
    }

    /*********************FOCUS**********************/
    public boolean isNextFocusable(Cell newFocus){
        Log.d("newFocus", ""+newFocus);
        if(focusController.isFocusInRange(newFocus)
                && !(keysController.isInvalidKey(newFocus))
                && !(keysController.isHiddenKey(newFocus))){
            Log.d("ok","ok");
            return true;
        }
        return false;
    }

    public Cell findNewFocus(int code){
        Cell curFocus = focusController.getCurrentFocus();
        Cell newFocus = focusController.calculateNewFocus(code);

        switch (code){
            case KeyEvent.KEYCODE_DPAD_UP:
                if(keysController.getKeysAtRow(newFocus.getRow())!= null && keysController.getKeysAtRow(newFocus.getRow()).isEmpty()){
                    newFocus.setRow(newFocus.getRow()-1);
                }

                //shift from bar 2 to bar 1
                if(suggestionsController.areShownBars()
                        && keysController.getKeyAtPosition(newFocus).getCode()==HIDDEN_KEY
                        && newFocus.getRow()>suggestionsController.getBar1().getRowIndex()
                        && newFocus.getRow()<suggestionsController.getBar2().getRowIndex()){
                    newFocus.setRow(suggestionsController.getBar1().getRowIndex());
                }

                //last row behaviour focus
                if((curFocus.getRow() == ROWS-1) && (curFocus.getCol() >= 5 && curFocus.getCol()<=7)){
                    switch (curFocus.getCol()){
                        case 5:
                            newFocus.setCol(8);
                            break;
                        case 6:
                            newFocus.setCol(9);
                            break;
                        case 7:
                            newFocus.setCol(10);
                            break;
                    }
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if(keysController.getKeysAtRow(newFocus.getRow())!= null && keysController.getKeysAtRow(newFocus.getRow()).isEmpty()){
                    newFocus.setRow(newFocus.getRow()+1);
                }

                //shift from bar 1 to bar 2
                if(suggestionsController.areShownBars()
                        && keysController.getKeyAtPosition(newFocus).getCode()==HIDDEN_KEY
                        && newFocus.getRow()>suggestionsController.getBar1().getRowIndex()
                        && newFocus.getRow()<suggestionsController.getBar2().getRowIndex()){
                    newFocus.setRow(suggestionsController.getBar2().getRowIndex());
                }

                //last row behaviour focus
                if(((curFocus.getRow() == ROWS-2) || (curFocus.getRow() == ROWS-3)) && (curFocus.getCol() >= 4 && curFocus.getCol()<=COLS-1)){
                    switch (curFocus.getCol()){
                        case 5:
                        case 6:
                        case 7:
                            newFocus.setCol(4);
                            break;
                        case 8:
                            newFocus.setCol(5);
                            break;
                        case 9:
                            newFocus.setCol(6);
                            break;
                        case 10:
                            newFocus.setCol(7);
                            break;
                    }
                }
                break;
        }

        return newFocus;
    }

    public void moveFocusOnKeyboard(Cell position){
        viewsController.moveCursorPosition(position);
    }

    /*********************SUGGESTIONS**********************/

    public void showBars(){
        //set not suggestion key click focus - highLight
        Cell previousFocus = focusController.getCurrentFocus();
        focusController.setPreviousFocus(previousFocus);

        //clear all bars
        clearBars();

        //set bars index
        Cell curFocus = focusController.getCurrentFocus();
        suggestionsController.setBarsRowPosition(curFocus);
        suggestionsController.setSuggestionsColPositions(curFocus);

        //get suggestions
        String sequence = textController.getFourCharacters();
        char suggestions[] = suggestionsController.getSuggestionsChars(sequence); //size 12
        char checkedSuggestions[] = getCheckedSuggestions(suggestions); //size<12

        //add suggestions to bars
        Log.d("checkedSuggestionsSize","size: "+checkedSuggestions.length);
        addSuggestionToBars(checkedSuggestions);

        //add highlight
        viewsController.setHighLightPosition(previousFocus);

        //update coordinates for cursor
        viewsController.setUpdatedCursorCoordinates(focusController.getCurrentFocus());

        //set show bars
        suggestionsController.setShownBars(true);
    }

    public void clearBars(){
        keysController.clearAllBars();
        viewsController.clearBarViews();
    }

    public boolean areBarShown(){
        return suggestionsController.areShownBars();
    }

    protected abstract void addSuggestionToBars(char[] checkedSuggestions);

    public void hideBars(){
        //clear all bars
        clearBars();

        //delete highlight
        viewsController.removeHighLight();

        //update coordinates for cursor
        viewsController.setUpdatedCursorCoordinates(focusController.getCurrentFocus());

        //set hide bars
        suggestionsController.setShownBars(false);
    }

    public void updateBars(){
        //get suggestions
        String sequence = textController.getFourCharacters();
        char suggestions[] = suggestionsController.getSuggestionsChars(sequence); //size 12
        char checkedSuggestions[] = getCheckedSuggestions(suggestions);
        Log.d("update sugg","size: "+ checkedSuggestions.length);
        //todo check prevFocus etc...
        updateSuggestionsBars(checkedSuggestions);
    }

    protected abstract void updateSuggestionsBars(char[] suggestions);

    protected void fillBar(int index, char[] suggestions){
        ArrayList<Key> keys = new ArrayList<>();
        for(char s : suggestions){
            Key k = keysController.getCharToKey(s).clone();
            k.setSuggestion(true);
            keys.add(k);
        }

        fillKeyboardBar(index, keys);
        viewsController.fillKeyboardViewBar(index, keysController.getKeysAtRow(index));
    }

    private char[] getCheckedSuggestions(char[] allSuggestions){ //allSuggestions has size = 12
        for(char c : allSuggestions)
            Log.d("allSuggestions","c: "+c);

        ArrayList<Character> charsToDelete = new ArrayList<>();

        //delete from suggestions clicked highlight letter
        Cell prevFocus = focusController.getPreviousFocus();
        char d1 = keysController.getKeyAtPosition(prevFocus).getLabel().charAt(0);
        Log.d("toDelete","del d1: "+d1);
        charsToDelete.add(d1);

        //delete from suggestions focused letter [in bar for example]
        Cell curFocus = focusController.getCurrentFocus();
        char d2 = keysController.getKeyAtPosition(curFocus).getLabel().charAt(0);
        Log.d("toDelete","del d2: "+d2);
        charsToDelete.add(d2);

        //delete from suggestions letters in rectangle [2] near prevFocus
        int prevRow = prevFocus.getRow();
        int[] colIndexes = suggestionsController.getSuggestionsColPositions();
        int[] prevCols = Utils.removeIntFromArray(colIndexes, prevFocus.getCol());
        Key k3 = keysController.getKeyAtPosition(new Cell(prevRow, prevCols[0]));
        //todo control this in others d
        if(k3.getIcon()==null && k3.getCode()!=HIDDEN_KEY){
            char d3 = k3.getLabel().charAt(0);
            Log.d("toDelete","del d3: "+d3);
            charsToDelete.add(d3);
        }

        Key k4 = keysController.getKeyAtPosition(new Cell(prevRow, prevCols[1]));
        if(k4.getIcon()==null && k4.getCode()!=HIDDEN_KEY){
            char d4 = k4.getLabel().charAt(0);
            Log.d("toDelete","del d4: "+d4);
            charsToDelete.add(d4);
        }

        //4 to delete -> 12-4 = 8 suggestions
        int dim = allSuggestions.length-charsToDelete.size();
        Log.d("dimens","N: "+ dim);
        char[] checkedSuggestions = new char[allSuggestions.length-charsToDelete.size()];
        int i = 0;
        for(char c: allSuggestions){
            if(!charsToDelete.contains(c) && i<dim){
                checkedSuggestions[i] = c;
                i++;
            }
        }

        return checkedSuggestions;
    }

    private void fillKeyboardBar(int index, ArrayList<Key> keys){
        //clear bar
        keysController.getKeysAtRow(index).clear();

        //get col indexes suggestions
        int[] colIndexes = suggestionsController.getSuggestionsColPositions();

        //
        for(int i=0, j=0; i<COLS; i++){
            if(Utils.isIntPresent(colIndexes,i)){
                keysController.getKeysAtRow(index).add(keys.get(j));
                j++;
            }else {
                keysController.getKeysAtRow(index).add(new Key(HIDDEN_KEY,"", null));
            }
        }
    }

    /*********************GETTERS**********************/

    public TextController getTextController() {
        return textController;
    }

    public FocusController getFocusController_() {
        return focusController;
    }

    public KeysController getKeysController() {
        return keysController;
    }

    protected SuggestionsController getSuggestionsController() {
        return suggestionsController;
    }

    /*********************SETTERS**********************/
    public void setSuggestionsController(SuggestionsController suggestionsController) {
        this.suggestionsController = suggestionsController;
    }

    /*********************OTHER**********************/
    protected void modifyKeyContent(Cell position, int code, String label){
        keysController.modifyKeyAtPosition(position, code, label);
        viewsController.modifyKeyLabel(position, label);
    }

    public void reInitKeyboard(){
        textController.reset();
        //close bars
        hideBars();
    }

    public void setKeyboardBehaviour(Cell newCell){
        if(!areBarShown()) { //bars not shown
            moveFocusOnKeyboard(newCell);
        }else{ //bars shown
            boolean isSugg = getKeysController().getKeyAtPosition(newCell).isSuggestion();
            if(!isSugg){ //not key suggestion
                int[] colIndexes = suggestionsController.getSuggestionsColPositions();
                Cell prevFocus = focusController.getPreviousFocus();

                //Cells in rectangle
                int[] nearColIndexes = Utils.removeIntFromArray(colIndexes, prevFocus.getCol());
                Cell nearPrev1 = new Cell(prevFocus.getRow(), nearColIndexes[0]);
                Cell nearPrev2 = new Cell(prevFocus.getRow(), nearColIndexes[1]);

                if(newCell.equals(prevFocus) || newCell.equals(nearPrev1) || newCell.equals(nearPrev2)){
                    moveFocusOnKeyboard(newCell);
                }else{
                    hideBars();
                }
            }else{ //key suggestion
                moveFocusOnKeyboard(newCell);
            }

        }
    }

}
