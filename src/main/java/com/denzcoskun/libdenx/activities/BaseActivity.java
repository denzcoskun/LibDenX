package com.denzcoskun.libdenx.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.denzcoskun.libdenx.R;
import com.denzcoskun.libdenx.interfaces.VolleyCallBack;
import com.denzcoskun.libdenx.models.BaseResponseModel;
import com.denzcoskun.libdenx.utils.NetworkUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Objects;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Denx on 1.06.2018.
 */
abstract public class BaseActivity extends AppCompatActivity implements VolleyCallBack<BaseResponseModel> {

    private Unbinder unbinder = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());
        ButterKnife.bind(this);
        onViewReady(savedInstanceState, getIntent());
    }

    @CallSuper
    protected void onViewReady(Bundle savedInstanceState, Intent intent) {
        //To be used by child activities
    }

    public void showMessage(String message) {
        if (message != null) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.message_error), Toast.LENGTH_SHORT).show();
        }
    }

    public void showMessage(int message) {
        showMessage(getString(message));
    }

    public boolean isNetworkConnected() {
        return NetworkUtils.isNetworkConnected(getApplicationContext());
    }

    public <T> void getJsonObject(String url, Class<T> responseModel, VolleyCallBack<T> callBack) {
        RequestQueue queue = Volley.newRequestQueue(this);
        ObjectMapper mapper = new ObjectMapper();

        queue.add(new JsonObjectRequest(Request.Method.GET, url, null, (JSONObject response) -> {
            try {
                callBack.onSuccess(mapper.readValue(response.toString(), responseModel));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, error -> {
            try {
                callBack.onError();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
    }

    public <T> void getJsonArray(String url, VolleyCallBack<T> callBack) {
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(new JsonArrayRequest(Request.Method.GET, url, null, (JSONArray response) -> {
            try {
                callBack.onSuccess((T) response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, error -> showMessage(R.string.volley_error)));
    }

    public <T> void sendRequest(String url, VolleyCallBack<T> callBack) {
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(new StringRequest(Request.Method.GET, url, response -> {
            try {
                callBack.onSuccess((T) response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, error -> showMessage(R.string.volley_error)));

    }

    public void showAlertDialog(String title, String text, String buttonTitle, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(text);
        builder.setNegativeButton(buttonTitle, (dialog, id) -> finish());
        builder.setCancelable(false);
        builder.show();
    }

    public void setUnBinder(Unbinder unbinder) {
        this.unbinder = unbinder;
    }

    @Override
    public void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroy();
    }

    public void noActionBarShadow() {
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
    }

    public void addBackButton() {
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSuccess(BaseResponseModel result) {

    }

    @Override
    public void onError() {

    }

    abstract protected int getLayoutResourceId();
}