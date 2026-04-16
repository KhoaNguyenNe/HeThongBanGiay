package com.example.hethongbangiay.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.hethongbangiay.Api.CreateOrder;
import com.example.hethongbangiay.R;
import com.example.hethongbangiay.models.ChiTietDonHang;
import com.example.hethongbangiay.viewmodels.OrderViewModel;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class PaymentActivity extends AppCompatActivity {
    Button btnDatHang;
    OrderViewModel orderViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // ZaloPay SDK Init
        ZaloPaySDK.init(2553, Environment.SANDBOX);
        double totalAmount = (double) 1000;
        String totalString = String.format("%.0f", totalAmount);
        btnDatHang = findViewById(R.id.btnDatHang);
        btnDatHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateOrder orderApi = new CreateOrder();

                try {
                    JSONObject data = orderApi.createOrder(totalString);
                    String code = data.getString("return_code");

                    if (code.equals("1")) {
                        String token = data.getString("zp_trans_token");
                        ZaloPaySDK.getInstance().payOrder(PaymentActivity.this, token, "demozpdk://app", new PayOrderListener() {
                            @Override
                            public void onPaymentSucceeded(String s, String s1, String s2) {
                                orderViewModel = new ViewModelProvider(PaymentActivity.this).get(OrderViewModel.class);

                                Intent intent1 = new Intent(PaymentActivity.this, PaymentNotification.class);
                                intent1.putExtra("result", "Thanh toán thành công");
                                startActivity(intent1);
                            }

                            @Override
                            public void onPaymentCanceled(String s, String s1) {
                                Intent intent1 = new Intent(PaymentActivity.this, PaymentNotification.class);
                                intent1.putExtra("result", "Hủy thanh toán");
                                startActivity(intent1);
                            }

                            @Override
                            public void onPaymentError(ZaloPayError zaloPayError, String s, String s1) {
                               Intent intent1 = new Intent(PaymentActivity.this, PaymentNotification.class);
                                intent1.putExtra("result", "Thanh toán thất bại");
                                startActivity(intent1);
                            }
                        });

                    }

                }catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }
}