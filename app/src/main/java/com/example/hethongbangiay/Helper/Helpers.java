package com.example.hethongbangiay.Helper;

import android.annotation.SuppressLint;


import com.example.hethongbangiay.Helper.HMac.HMacUtil;

import org.jetbrains.annotations.NotNull;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class Helpers {
    private static int transIdDefault = 1;

    @NotNull
    @SuppressLint("DefaultLocale")
     public static String getAppTransId() {
        SimpleDateFormat format = new SimpleDateFormat("yyMMdd", Locale.getDefault());
        String date = format.format(new Date());

        int random = new Random().nextInt(999999);

        return date + "_" + random;
    }

    @NotNull
    public static String getMac(@NotNull String key, @NotNull String data) throws NoSuchAlgorithmException, InvalidKeyException {
        return Objects.requireNonNull(HMacUtil.HMacHexStringEncode(HMacUtil.HMACSHA256, key, data));
     }
}
