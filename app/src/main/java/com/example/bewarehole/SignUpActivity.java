package com.example.bewarehole;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.bewarehole.Model.UserModel;
import com.example.bewarehole.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {
    private UserModel newuser=new UserModel();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference reference=database.getReference();
    private ActivitySignUpBinding signUpBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signUpBinding=ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(signUpBinding.getRoot());
        ViewCompat.setLayoutDirection(getWindow().getDecorView(), ViewCompat.LAYOUT_DIRECTION_LTR);

        signUpBinding.dosignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpBinding.dosignup.setClickable(false);
                signUpBinding.progreeload.setVisibility(View.VISIBLE);
                boolean c =checkall();
                if(!c){
                    signUpBinding.dosignup.setClickable(true);
                   signUpBinding.progreeload.setVisibility(View.GONE);
                }

            }
        });
    }

    private boolean checkall(){

        if (signUpBinding.nameSignup.length() == 0) {

            signUpBinding.nameSignuplayout.setError("من فضلك ادخل اسم المحل  ");
            return false;

        }

        signUpBinding.nameSignuplayout.setErrorEnabled(false);
        if (signUpBinding.numberSignup.length() == 0) {

            signUpBinding.numberSignuplayout.setError("من فضلك ادخل رقم التليفون");
            return false;
        }
        signUpBinding.numberSignuplayout.setErrorEnabled(false);
        if (signUpBinding.numberSignup.length() < 11) {

            signUpBinding.numberSignuplayout.setError("من فضلك ادخل رقم التليفون الصحيح  ");
            return false;
        }

        signUpBinding.numberSignuplayout.setErrorEnabled(false);

        if(signUpBinding.emailSignup.length()==0){

            signUpBinding.emailSignuplayout.setError("من فضلك ادخل الايميل الخاص بك ");
            return false;

        }

        signUpBinding.emailSignuplayout.setErrorEnabled(false);



         if (signUpBinding.passwordSignup.length() < 8) {

             signUpBinding.passwordSignuplayout.setError("من فضلك ادخل الباسورد اكثر من 8 حروف او ارقام ");
            return false;
        }
        signUpBinding.passwordSignuplayout.setErrorEnabled(false);

        String pass=signUpBinding.passwordSignup.getText().toString();
        String confimpass=signUpBinding.confirmpasswordSignup.getText().toString();
        if (!pass.equals(confimpass)) {
            signUpBinding.passwordSignuplayout.setError("الباسورد يجب ان يساوي تاكيد الباسورد");
            signUpBinding.confirmpasswordSignuplayout.setError("الباسورد يجب ان يساوي تاكيد الباسورد");
            return false;
        }
        signUpBinding.passwordSignuplayout.setErrorEnabled(false);
        signUpBinding.confirmpasswordSignuplayout.setErrorEnabled(false);

        signUpBinding.progreeload.setVisibility(View.VISIBLE);

        setInfonewuser();
        return true;
    }
    public void setInfonewuser(){
        newuser.setName(signUpBinding.nameSignup.getText().toString());
        newuser.setPhone(signUpBinding.numberSignup.getText().toString());
        newuser.setEmail(signUpBinding.emailSignup.getText().toString());
        newuser.setPassword(signUpBinding.passwordSignup.getText().toString());
        uploadnewuser();
    }
    public void uploadnewuser(){
        newuser.setId(reference.push().getKey());
        reference.child("datebase").child("User").child(newuser.getId())
                .setValue(newuser).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                signUpBinding.progreeload.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), e.getMessage()
                        .toString(), Toast.LENGTH_LONG).show();
                signUpBinding.dosignup.setClickable(true);
                signUpBinding.progreeload.setVisibility(View.GONE);

            }
        }).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                signUpBinding.progreeload.setVisibility(View.GONE);
                Intent i=new Intent(SignUpActivity.this,MapsActivity.class);
                i.putExtra("userid",newuser.getId());
                startActivity(i);
            }
        });

    }

}