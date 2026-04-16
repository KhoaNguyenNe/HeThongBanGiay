package com.example.hethongbangiay.firestore;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseMigrationSeeder {

    private final Context context;
    private final FirebaseFirestore firestore;

    public FirebaseMigrationSeeder(@NonNull Context context) {
        this.context = context.getApplicationContext();
        this.firestore = FirebaseFirestore.getInstance();
    }

    public void migrateAll(@NonNull Runnable onSuccess,
                           @NonNull java.util.function.Consumer<Exception> onError) {
        try {
            onSuccess.run();

        } catch (Exception e) {
            onError.accept(e);
        }
    }
}