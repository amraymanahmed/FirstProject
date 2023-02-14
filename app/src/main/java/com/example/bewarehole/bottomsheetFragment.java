package com.example.bewarehole;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.bewarehole.Model.PlaceHoleModel;
import com.example.bewarehole.databinding.BottomsheetFragmentBinding;
import com.example.bewarehole.databinding.PlaceholefragmentBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class bottomsheetFragment extends BottomSheetDialogFragment {

    private BottomsheetViewModel mViewModel;
    private BottomsheetFragmentBinding binding;
    private PlaceholefragmentBinding holebinding;
    private boolean operationnumber;
    private String userid;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference reference=database.getReference();
    private String location;
    public bottomsheetFragment(Boolean operationnumber,String userid,String location) {

        this.operationnumber = operationnumber;
        this.userid=userid;
        this.location=location;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if(operationnumber){

            binding = BottomsheetFragmentBinding.inflate(inflater,container,false);
            return binding.getRoot();

        }
        else {

            holebinding = PlaceholefragmentBinding.inflate(inflater, container, false);

            return holebinding.getRoot();

        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
         mViewModel = new ViewModelProvider(requireActivity()).get(BottomsheetViewModel.class);
        if(operationnumber){


        }
        else{
            holebinding.holelocation.setText(location);

            holebinding.addhole.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holebinding.addhole.setClickable(false);
                    boolean c=checkdatahole();
                    if(!c){
                        holebinding.addhole.setClickable(true);
                    }

                }
            });

        }
    }
    private boolean checkdatahole(){


        if(holebinding.kindhole.length()==0){

            holebinding.kindholelayout.setError("من فضلك ادخل نوع الحفرة");
            return false;
        }

        holebinding.kindholelayout.setErrorEnabled(false);

        if(holebinding.descriptionhole.length()==0){

            holebinding.descriptionholelayout.setError("من فضلك ادخل وصف عن شكل الحفرة ");
            return false;
        }

        holebinding.descriptionholelayout.setErrorEnabled(false);
        loadholeinformation();
     return true;
    }
    public void loadholeinformation(){

        PlaceHoleModel placeHoleModel=new PlaceHoleModel();
        placeHoleModel.setTitlekind(holebinding.kindhole.getText().toString());
        placeHoleModel.setSnippetdesciption(holebinding.descriptionhole.getText().toString());
        String locationhole=holebinding.holelocation.getText().toString();
        String[] latlong =locationhole.split(",");
        double latitude = Double.parseDouble(latlong[0]);
        double longitude = Double.parseDouble(latlong[1]);
        placeHoleModel.setLatitudelocation(latitude);
        placeHoleModel.setLongitudelocation(longitude);
        placeHoleModel.setUserid(userid);
        placeHoleModel.setId(reference.push().getKey());
        reference.child("datebase").child("PlaceHole").child(placeHoleModel.getId())
                .setValue(placeHoleModel).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(getContext(),e.getMessage().toString(),Toast.LENGTH_SHORT).show();
                holebinding.addhole.setClickable(true);

            }
        }).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                Toast.makeText(getContext(),"suceess add hole",Toast.LENGTH_SHORT).show();
                mViewModel.setcompleteaddhole(true);
                dismiss();

            }
        });
    }

}