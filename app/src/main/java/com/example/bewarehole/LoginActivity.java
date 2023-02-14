package com.example.bewarehole;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import com.example.bewarehole.Model.UserModel;
import com.example.bewarehole.databinding.ActivityLogin2Binding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

public class LoginActivity extends AppCompatActivity {
    private ActivityLogin2Binding binding;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference referenceuser=database.getReference().child("datebase").child("User");
    private int REQUESTFINELOCATIONCODE=109;
    private boolean FINELocationPermission=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityLogin2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setLayoutDirection(getWindow().getDecorView(), ViewCompat.LAYOUT_DIRECTION_LTR);

        getLocationPermission();
        binding.dosignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(FINELocationPermission) {
                    binding.dosignin.setClickable(false);
                    boolean c=checkall();
                    if(!c){
                        binding.dosignin.setClickable(true);
                    }

                }else{

                    Toast.makeText(getApplicationContext(),"يجب قبول الاذونات اولا ",Toast.LENGTH_SHORT).show();
                    getLocationPermission();
                }

            }
        });
        binding.Signupbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(i);

            }
        });

    }
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (Build.VERSION.SDK_INT >= 23) {

            checkpermission(Manifest.permission.ACCESS_FINE_LOCATION, REQUESTFINELOCATIONCODE);

        } else {
            FINELocationPermission = true;
        }

    }

    private void checkpermission(String permission,int coderequest)

    {

        if (ContextCompat.checkSelfPermission(LoginActivity.this, permission) == PackageManager.PERMISSION_DENIED) {

            Toast.makeText(getApplicationContext(),coderequest+"",Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(LoginActivity.this, new String[] { permission},coderequest);
        }
        else{


            if(coderequest==REQUESTFINELOCATIONCODE){

                FINELocationPermission=true;

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode==REQUESTFINELOCATIONCODE){

            if(grantResults.length > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){

                FINELocationPermission=true;

            }

            else{
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                Toast.makeText(getApplicationContext(),"no success",Toast.LENGTH_SHORT).show();

            }

        }

    }

    public void permissionnessary(){

        new AlertDialog.Builder(this)
                .setTitle("التطبيق يحتاج الازونات للموقع")
                .setMessage("من فضلك عدل الاذونات واسمح استخدام اذونات الموقع لتسطيع استخدام التطبيق")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent settingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        settingsIntent.setData(uri);
                        startActivityForResult(settingsIntent, 7);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();

    }

    private boolean checkall() {

        if (binding.emailSignin.length() == 0) {

            binding.emailSigninlayout.setError("من فضلك ادخل الايميل ");
            return false;

        }

        binding.emailSigninlayout.setErrorEnabled(false);


        if (binding.passwordSignin.length() < 8) {

            binding.passwordSigninlayout.setError("من فضلك ادخل الباسورد الصحيح ");
            return false;

        }
        binding.passwordSigninlayout.setErrorEnabled(false);
        binding.progreeloadsignin.setVisibility(View.VISIBLE);
        searchemail();
        return false;

    }
    private void searchemail(){

        String email=binding.emailSignin.getText().toString();
        String password=binding.passwordSignin.getText().toString();

        referenceuser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean c=false;
                for(DataSnapshot snap:snapshot.getChildren()){

                    UserModel userModel =   snap.getValue(UserModel.class);

                    if(userModel.getEmail().equals(email)&&userModel.getPassword().equals(password)) {

                        binding.progreeloadsignin.setVisibility(View.GONE);
                        c=true;
                        Intent i=new Intent(LoginActivity.this,MapsActivity.class);
                        i.putExtra("userid",userModel.getId());
                        startActivity(i);


                    }
                }

                if(!c){

                    binding.dosignin.setClickable(true);
                    binding.progreeloadsignin.setVisibility(View.GONE);
                    binding.emailSigninlayout.setError("الباسورد او الايميل غير صحيح ");

                }


            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                binding.dosignin.setClickable(true);
                binding.progreeloadsignin.setVisibility(View.GONE);

            }
        });
    }

}