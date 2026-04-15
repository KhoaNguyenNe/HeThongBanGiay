package com.example.hethongbangiay.utils;

public interface OnFirestoreResult<T> {
    void onSuccess(T data);
    void onError(Exception e);
}
