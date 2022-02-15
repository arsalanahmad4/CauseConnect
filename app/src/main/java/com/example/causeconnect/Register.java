package com.example.causeconnect;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    Button btn2_signup;
    Button btn_login;
    EditText e_mail, pass_word,phone_number;
    public EditText user_name;
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;



    public static String username;
    String userID;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        e_mail=findViewById(R.id.email);
        pass_word=findViewById(R.id.password1);
        user_name=findViewById(R.id.username);
        phone_number=findViewById(R.id.phone_number);
        btn2_signup=findViewById(R.id.sign);
        btn_login = findViewById(R.id.login);
        mAuth=FirebaseAuth.getInstance();



        btn2_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = e_mail.getText().toString().trim();
                String password= pass_word.getText().toString().trim();
                if(email.isEmpty())
                {
                    e_mail.setError("Email is empty");
                    e_mail.requestFocus();
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    e_mail.setError("Enter the valid email address");
                    e_mail.requestFocus();
                    return;
                }
                if(password.isEmpty())
                {
                    pass_word.setError("Enter the password");
                    pass_word.requestFocus();
                    return;
                }
                if(password.length()<6)
                {
                    pass_word.setError("Length of the password should be more than 6");
                    pass_word.requestFocus();
                    return;
                }
                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            username = user_name.getText().toString();

                            Toast.makeText(Register.this,"You are successfully Registered", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(Register.this,Login.class);
                            startActivity(i);
                        }
                        else
                        {
                            Toast.makeText(Register.this,"You are not Registered! Try again",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Register.this,Login.class);
                startActivity(i);
            }
        });

    }



    public void uploadUserData(){
        String email = e_mail.getText().toString();
        String phoneNumber = phone_number.getText().toString();
        userID = mAuth.getCurrentUser().getUid();
        CollectionReference collectionReference = fStore.collection("Users data");
        Map<String,Object> user = new HashMap<>();
        user.put("timestamp", FieldValue.serverTimestamp());
        user.put("userid",userID);
        user.put("email",email);
        user.put("phoneNumber",phoneNumber);


        collectionReference.add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Error!",Toast.LENGTH_SHORT).show();
                        //Log.w(TAG, "Error!", e);
                    }
                });
    }
}