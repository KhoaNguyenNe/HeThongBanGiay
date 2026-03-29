package com.example.hethongbangiay.firebase;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.example.hethongbangiay.firebase.FirebaseManager;

import java.util.HashMap;
import java.util.Map;

public class TestFirebaseActivity extends AppCompatActivity {

    private static final String TAG = "TestFirebaseActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseFirestore db = FirebaseManager.getFirestore();

        Map<String, Object> testData = new HashMap<>();
        testData.put("name", "Firestore connected");
        testData.put("createdAt", System.currentTimeMillis());

        db.collection("test_connection")
                .add(testData)
                .addOnSuccessListener(documentReference ->
                        Log.d(TAG, "Write success: " + documentReference.getId()))
                .addOnFailureListener(e ->
                        Log.e(TAG, "Write failed", e));
    }
}