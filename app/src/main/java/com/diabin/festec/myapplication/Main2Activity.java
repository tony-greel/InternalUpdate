package com.diabin.festec.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONObject;

public class Main2Activity extends AppCompatActivity implements View.OnClickListener {

    private FaceBookLogin faceBookLogin = null;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        button = findViewById(R.id.but_1);
        button.setOnClickListener(this);

        faceBookLogin = new FaceBookLogin(this);

        faceBookLogin.setFacebookListener(new FaceBookLogin.FacebookListener() {
            @Override
            public void facebookLoginSuccess(JSONObject object) {
                Toast.makeText(Main2Activity.this, "登录成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void facebookLoginFail(String message) {
                Toast.makeText(Main2Activity.this, "登录失败", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void facebookLoginCancel() {
                Toast.makeText(Main2Activity.this, "登录取消", Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        faceBookLogin.getCallbackManager().onActivityResult(resultCode,resultCode,data);
        super.onActivityReenter(resultCode, data);
    }

    @Override
    public void onClick(View v) {
        faceBookLogin.login();
    }
}
