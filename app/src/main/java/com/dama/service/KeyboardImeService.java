package com.dama.service;

import static java.lang.Character.isLetter;

import android.annotation.SuppressLint;
import android.inputmethodservice.InputMethodService;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.FrameLayout;
import com.dama.controllers.Controller;
import com.dama.controllers.ControllerOneBar;
import com.dama.controllers.ControllerTwoBars;
import com.dama.customkeyboardbarstv.R;
import com.dama.utils.Cell;
import com.dama.utils.Key;

public class KeyboardImeService extends InputMethodService {
    private Controller controller;
    private boolean keyboardShown;
    private InputConnection ic;

    @Override
    public View onCreateInputView() {
        @SuppressLint("InflateParams")
        FrameLayout rootView = (FrameLayout) this.getLayoutInflater().inflate(R.layout.keyboard_layout, null);
        controller = new ControllerTwoBars(getApplicationContext(), rootView);
        //controller = new ControllerOneBar(getApplicationContext(), rootView);
        return rootView;
    }

    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        super.onStartInputView(info, restarting);
        keyboardShown = true;
    }

    @Override
    public void onFinishInputView(boolean finishingInput) {
        super.onFinishInputView(finishingInput);
        keyboardShown = false;
        controller.reInitKeyboard();
        ic = null;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyboardShown){
            Log.d("KeyboardImeService", "onKeyDown code: "+ keyCode);
            ic = getCurrentInputConnection();
            switch (keyCode){
                case KeyEvent.KEYCODE_BACK:
                    hideKeyboard();
                    break;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                case KeyEvent.KEYCODE_DPAD_UP:
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    Cell newCell = controller.findNewFocus(keyCode);
                    if (controller.isNextFocusable(newCell)){
                        //update focus
                        controller.getFocusController_().setCurrentFocus(newCell);
                        controller.setKeyboardBehaviour(newCell);
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_CENTER:
                case KeyEvent.KEYCODE_0:    //todo remove - just for emulator test OK
                    Cell focus = controller.getFocusController_().getCurrentFocus();
                    Key key = controller.getKeysController().getKeyAtPosition(focus);
                    int code = key.getCode();
                    if(code!=Controller.FAKE_KEY){
                        handleText(code, ic);
                        char character = (char) code;
                        if(code!=Controller.ENTER_KEY && code!=Controller.DEL_KEY){
                            controller.getTextController().addCharacterWritten(character);
                            if(isLetter(character)){
                                if(!key.isSuggestion()){
                                    controller.showBars();
                                }else {
                                    controller.updateBars();
                                }
                            }
                        }
                    }
                    break;
            }
            return true;
        }
        return false;
    }

    private void handleText(int code, InputConnection ic){
        switch (code){
            case Controller.DEL_KEY:
                ic.deleteSurroundingText(1, 0);
                CharSequence beforeCursor = ic.getTextBeforeCursor(20, 0); //Get text 20 chars before cursor
                if(beforeCursor!=null){
                    controller.getTextController().deleteCharacterWritten(beforeCursor.toString());
                }
                break;
            case Controller.ENTER_KEY:
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER));
                break;
            default: //user press letter, number, symbol
                char c = (char) code;
                ic.commitText(String.valueOf(c),1);
        }
    }

    private void hideKeyboard(){
        requestHideSelf(0); //calls onFinishInputView
    }
}
