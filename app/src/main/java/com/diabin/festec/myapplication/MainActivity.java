package com.diabin.festec.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private static final String EMAIL = "email";

    private LoginButton loginButton;
    private LoginManager loginManager;
    private FaceBookLogin.FacebookListener facebookListener;
    private List<String> permissions;
    private ProfileTracker profileTracker;
    private AccessTokenTracker accessTokenTracker;
    private AccessToken accessToken;

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.img_qe_code);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList(EMAIL));
        loginButton.setReadPermissions(Arrays.asList(USER_SERVICE));
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
        iniData();

        imageView.setImageBitmap(QrCodeTools.createQRCodeBitmap("dsasa",199,199));
    }

    private void iniData() {
        callbackManager = CallbackManager.Factory.create();
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                // Set the access token using
                // currentAccessToken when it's loaded or set.
                Log.d("LJJ",currentAccessToken.getUserId());
            }
        };
        // If the access token is available already assign it.
        accessToken = AccessToken.getCurrentAccessToken();

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                Log.d("LJJ",currentProfile.getFirstName());
            }
        };


        getLoginManager().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // login success
                AccessToken accessToken = loginResult.getAccessToken();
                getLoginInfo(accessToken);
                Toast.makeText(MainActivity.this, "iniData：成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                //取消登录
                if (facebookListener != null) {
                    facebookListener.facebookLoginCancel();
                }
                Toast.makeText(MainActivity.this, "iniData：取消授权", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                //登录出错
                if (facebookListener != null) {
                    facebookListener.facebookLoginFail(exception.getMessage());
                }
                Toast.makeText(MainActivity.this, "iniData：失败", Toast.LENGTH_SHORT).show();
                Log.d("LJJ",exception.getMessage());
            }
        });
        permissions = Arrays
                .asList("email", "user_likes", "user_status", "user_photos", "user_birthday", "public_profile", "user_friends");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 获取loginMananger
     * @return
     */
    private LoginManager getLoginManager() {
        if (loginManager == null) {
            loginManager = LoginManager.getInstance();
        }
        return loginManager;
    }

    /**
     * 获取登录信息
     *
     * @param accessToken
     */
    public void getLoginInfo(AccessToken accessToken) {

        GraphRequest request = GraphRequest.newMeRequest(accessToken, (object, response) -> {
            if (object != null) {
                //比如:1565455221565
                String id = object.optString("id");
                //比如：Zhang San
                String name = object.optString("name");
                //性别：比如 male （男）  female （女）
                String gender = object.optString("gender");
                //邮箱：比如：56236545@qq.com
                String emali = object.optString("email");

                //获取用户头像
                JSONObject object_pic = object.optJSONObject("picture");
                JSONObject object_data = object_pic.optJSONObject("data");
                String photo = object_data.optString("url");

                //获取地域信息
                //zh_CN 代表中文简体
                String locale = object.optString("locale");

                Toast.makeText(this, "" + object.toString(), Toast.LENGTH_SHORT).show();
                Log.d("LJJ","ID："+id);
                Log.d("LJJ","昵称："+name);
                Log.d("LJJ","性别："+gender);
                Log.d("LJJ","邮箱："+emali);
                Log.d("LJJ","头像："+photo);
                Log.d("LJJ","地域信息："+locale);
                Log.d("LJJ","全部数据："+object.toString());

                if (facebookListener != null) {
                    // 此处参数根需要自己修改
                    facebookListener.facebookLoginSuccess(object);
                }
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,gender,birthday,email,picture,locale,updated_time,timezone,age_range,first_name,last_name");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        profileTracker.startTracking();
    }
}
