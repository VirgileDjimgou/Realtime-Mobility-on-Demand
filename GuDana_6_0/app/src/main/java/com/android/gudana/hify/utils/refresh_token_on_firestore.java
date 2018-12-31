package com.android.gudana.hify.utils;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

public class refresh_token_on_firestore {

    public static FirebaseFirestore mFirestore;
    public static FirebaseAuth mAuth;
    private static DatabaseReference userDatabase;


    public static  void refresh_token(){
        String newToken = FirebaseInstanceId.getInstance().getToken();

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        final DocumentReference userDocument=mFirestore.collection("Users").document(mAuth.getCurrentUser().getUid());



        Map<String,Object> map=new HashMap<>();
        map.put("token_id",newToken);

        userDocument.update(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i("Update","success");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("Update","failed: "+e.getMessage());

                    }
                });

    }
}
