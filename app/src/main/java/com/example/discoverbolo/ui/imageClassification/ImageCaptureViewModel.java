package com.example.discoverbolo.ui.imageClassification;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ImageCaptureViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ImageCaptureViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is notifications fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}