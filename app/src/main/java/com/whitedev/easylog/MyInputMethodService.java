package com.whitedev.easylog;

import android.inputmethodservice.InputMethodService;

public class MyInputMethodService extends InputMethodService {

    //only use to force soft keyboard instead of bluetooth hard keyboard
    @Override
    public boolean onEvaluateInputViewShown() {
        super.onEvaluateInputViewShown();
        return true;
    }
}
