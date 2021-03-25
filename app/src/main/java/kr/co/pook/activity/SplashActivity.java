package kr.co.pook.activity;

import androidx.appcompat.app.AppCompatActivity;
import kr.co.pook.R;
import kr.co.pook.interfaces.Pook;
import kr.co.pook.util.SessionManager;
import kr.co.pook.vo.Pook_user;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;

public class SplashActivity extends AppCompatActivity {
    private  static int SPLASH_TIME_OUT = 2000;
    private Retrofit retrofit;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        /*구글 API에 넣어야할 hash key 찍기*/
        try{
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String key = new String(Base64.encode(md.digest(), 0));
                Log.d("Hash key:", "!!!!!!!"+key+"!!!!!!");
            }
        } catch (Exception e){
            Log.e("name not found", e.toString());
        }

        retrofit = new Retrofit.Builder()
                .baseUrl(Pook.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        sessionManager = new SessionManager(this);
        if(sessionManager.isLogin()){ /*가입되어 있을경우 (내부DB에 데이터가 있는경우)*/
            HashMap<String, String> user = sessionManager.getUserDetail();
            Pook pook = retrofit.create(Pook.class);
            Call<List<Pook_user>> call = pook.getUserList(user.get("ID").toString());

            call.enqueue(new Callback<List<Pook_user>>() {
                @Override
                // 성공시
                public void onResponse(Call<List<Pook_user>> call, Response<List<Pook_user>> response) {
                    List<Pook_user> contributors = response.body();
                    // 받아온 리스트를 순회하면서
                    /*for (Pook_user contributor : contributors) {
                        // 텍스트 뷰에 login 정보를 붙임
                        textView.append(contributor.login);
                    }*/

                    for (Pook_user contributor : contributors) {
                        Log.i("splash",contributor.getUser_id());
                    }

                    if(contributors.size() > 0) {
                        goMain();
                    }else{
                        Toast.makeText(getApplicationContext(), "가입정보가 존재하지 않습니다.", Toast.LENGTH_LONG).show();
                        register();
                    }
                }

                @Override
                // 실패시
                public void onFailure(Call<List<Pook_user>> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "네트워크오류", Toast.LENGTH_LONG).show();
                    finish();
                }
            });
        }else{
            register();
        }
    }

    public void goMain(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        },SPLASH_TIME_OUT);
    }

    public void register(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        },SPLASH_TIME_OUT);
    }
}
