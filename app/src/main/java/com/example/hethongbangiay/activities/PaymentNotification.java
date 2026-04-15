package com.example.hethongbangiay.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hethongbangiay.R;

public class PaymentNotification extends AppCompatActivity {
    TextView txtPaymentNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_notification);

        txtPaymentNotification = findViewById(R.id.txtPaymentNotification);

        Intent intent = getIntent();
        txtPaymentNotification.setText( intent.getStringExtra("result"));
    }
}