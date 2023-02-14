package com.example.bewarehole;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BottomsheetViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private MutableLiveData<Boolean> completeaddhole=new MutableLiveData<>();
    public void setcompleteaddhole(Boolean x){
        completeaddhole.setValue(x);
    }
    public LiveData<Boolean> getcompleteaddhole() {
        return completeaddhole;
    }


}