package com.denzcoskun.libdenx.interfaces;

/**
 * Created by Denx on 1.06.2018.
 */
public interface VolleyCallBack<T> {
    void onSuccess(T result);
    void onError();
}
