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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {

    Button reg_btn;
    EditText nickname, phone1, phone2, phone3;
    String UUID = "";
    private Retrofit retrofit;
    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();

        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editCheck()){

                    HashMap<String, Object> userMap = new HashMap<>();
                    userMap.put("user_id",UUID);
                    userMap.put("nickname",nickname.getText().toString());
                    userMap.put("phone",phone1.getText().toString()+"-"+phone2.getText().toString()+"-"+phone3.getText().toString());

                    Pook pook = retrofit.create(Pook.class);
                    Call<Pook_user> call = pook.setUser(userMap);

                    call.enqueue(new Callback<Pook_user>() {
                        @Override
                        // 성공시
                        public void onResponse(Call<Pook_user> call, Response<Pook_user> response) {
                            Pook_user contributor = response.body();
                            if(contributor.getResult().equals("200")){
                                Toast.makeText(getApplicationContext(), "정상적으로 가입되었습니다.", Toast.LENGTH_SHORT).show();
                                sessionManager.createSession(UUID,nickname.getText().toString(),phone1.getText().toString()+"-"+phone2.getText().toString()+"-"+phone3.getText().toString());
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }else{
                                Toast.makeText(getApplicationContext(), "가입에 실패했습니다.(관리자에게 문의)", Toast.LENGTH_LONG).show();
                            }

                        }

                        @Override
                        // 실패시
                        public void onFailure(Call<Pook_user> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "네트워크오류", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    public void init(){
        reg_btn = findViewById(R.id.reg_btn);
        nickname = findViewById(R.id.nickname);
        phone1 = findViewById(R.id.phone1);
        phone2 = findViewById(R.id.phone2);
        phone3 = findViewById(R.id.phone3);

        UUID = GetDevicesUUID(getApplicationContext());

        retrofit = new Retrofit.Builder()
                .baseUrl(Pook.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        sessionManager = new SessionManager(this);
    }

    public boolean editCheck(){
        if(nickname.getText().toString().trim().equals("")){
            Toast.makeText(getApplicationContext(),"닉네임을 입력해주세요.",Toast.LENGTH_SHORT).show();
            return false;
        }else if(phone1.getText().toString().trim().equals("")){
            Toast.makeText(getApplicationContext(),"전화번호 앞자리를 입력해주세요.",Toast.LENGTH_SHORT).show();
            return false;
        }else if(phone2.getText().toString().trim().equals("")){
            Toast.makeText(getApplicationContext(),"전화번호 중간자리를 입력해주세요.",Toast.LENGTH_SHORT).show();
            return false;
        }else if(phone3.getText().toString().trim().equals("")){
            Toast.makeText(getApplicationContext(),"전화번호 뒷자리를 입력해주세요.",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private String GetDevicesUUID(Context mContext){
        UUID uuid = null;
        String androidId = Settings.Secure.getString(mContext.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        if (androidId == null || androidId.isEmpty() || androidId.equals("9774d56d682e549c")) {
            uuid = uuid.randomUUID();
        } else {
            try {
                uuid = uuid.nameUUIDFromBytes(androidId.getBytes("UTF8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                uuid = uuid.randomUUID();
            }
        }
        return uuid.toString();
    }


}
