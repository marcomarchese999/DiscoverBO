package com.example.discoverbolo.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Scatta una foto o pronuncia il nome di un monumento per immergerti nella storia della città! Inizia ora!");
    }

    public LiveData<String> getText() {
        return mText;
    }
}