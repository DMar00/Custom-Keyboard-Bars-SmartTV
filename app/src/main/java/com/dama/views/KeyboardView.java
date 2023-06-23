package com.dama.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import androidx.core.content.ContextCompat;
import com.dama.controllers.Controller;
import com.dama.customkeyboardbarstv.R;
import com.dama.utils.Cell;
import com.dama.utils.Key;
import com.dama.utils.Utils;
import java.util.ArrayList;
import java.util.HashMap;

public class KeyboardView  extends TableLayout {
    private HashMap<Integer, KeyboardRowView> rows;

    public KeyboardView(Context context) {
        super(context);
    }

    public KeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void initKeyboardView(HashMap<Integer, ArrayList<Key>> allKeys){
        rows = new HashMap<>();
        String colorLabel = Utils.colorToString(ContextCompat.getColor(getContext(), R.color.label));
        Drawable keyDrawable = ContextCompat.getDrawable(getContext(), R.drawable.key_background);
        Drawable hiddenKeyDrawable = ContextCompat.getDrawable(getContext(), R.drawable.key_hidden_background);

        //create rows and bars
        for(int i = 0; i< Controller.ROWS; i++){
            if(Utils.isIntPresent(Controller.barsIndexes, i)){
                KeyboardRowView bar = new KeyboardRowView(getContext());
                bar.addBarBackground();
                rows.put(i, bar);
                addView(bar);
            }else{
                KeyboardRowView row = new KeyboardRowView(getContext());
                ArrayList<Key> keys = allKeys.get(i);
                for(int j=0; j<keys.size(); j++){
                    Drawable background;
                    if(keys.get(j).getCode()==Controller.HIDDEN_KEY){
                        background = hiddenKeyDrawable;
                    }else {
                        background = keyDrawable;
                    }
                    KeyView kv = new KeyView(getContext(), background, keys.get(j).getLabel(), colorLabel);
                    if(keys.get(j).getIcon()!=null)
                        kv.setIcon(keys.get(j).getIcon());
                    kv.addKeyParams(keys.get(j).getCode(), false);
                    row.addKeyView(kv);
                }
                rows.put(i, row);
                addView(row);
            }
        }
    }

    public KeyView getKeyViewAtCell(Cell cell){
        return rows.get(cell.getRow()).getKeyView(cell.getCol());
    }

    public void fillBarView(int rowIndex, ArrayList<Key> suggestionKeys){
        Drawable s_drawable = ContextCompat.getDrawable(getContext(), R.drawable.key_sug_background);
        Drawable h_drawable = ContextCompat.getDrawable(getContext() ,R.drawable.key_hidden_background);
        String colorLabel = Utils.colorToString(ContextCompat.getColor(getContext(), R.color.label));

        for(Key key: suggestionKeys){
            KeyView keyView = null;

            String finaLabel;
            if(key.getLabel().equals(" "))
                finaLabel = "␣";
            else
                finaLabel = key.getLabel();

            if(key.isSuggestion()){
                keyView = new KeyView(getContext(), s_drawable, finaLabel, colorLabel);
            }else{
                keyView = new KeyView(getContext(), h_drawable, finaLabel, colorLabel);
            }
            //set params
            TableRow.LayoutParams params = new TableRow.LayoutParams();
            params.gravity = Gravity.CENTER;
            params.setMargins(0, 3, 0, 3);
            keyView.setLayoutParams(params);
            //set dims
            int height = (int) getResources().getDimension(R.dimen.sug_key_height);
            int width = (int) getResources().getDimension(R.dimen.sug_key_width);
            int textSize = (int) getResources().getDimension(R.dimen.sug_key_text_size);
            keyView.changeDimension(height,width,textSize);
            //add keyView
            rows.get(rowIndex).addKeyView(keyView);
        }
    }

    public void clearBarViews(){
        int[] indexes = Controller.barsIndexes;
        for (int i: indexes)
            rows.get(i).clearBar();
    }

    public void deleteAll(){
        //remove views
        for(int i=0; i<rows.size(); i++){
            removeView(rows.get(i));
        }
        rows = null;
    }
}
