package kr.co.pook.activity;

import androidx.appcompat.app.AppCompatActivity;
import kr.co.pook.R;
import kr.co.pook.interfaces.Pook;
import kr.co.pook.util.SessionManager;
import kr.co.pook.vo.Pook_reserve;
import kr.co.pook.vo.Pook_review;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import java.util.Calendar;
import java.util.HashMap;

public class ReviewPopActivity extends AppCompatActivity {

    RatingBar review_score;
    EditText review_review;

    private String store_id = "";
    Intent intent;
    private HashMap<String, String> userMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_pop);

        init();
    }

    public void init() {
        /*인텐트 값 받아오기*/
        intent = getIntent();
        store_id = intent.getStringExtra("store_id");

        review_score = findViewById(R.id.review_score);
        review_review = findViewById(R.id.review_review);

        /*사용자 계정 불러오기*/
        SessionManager sessionManager = new SessionManager(this);
        userMap = sessionManager.getUserDetail();
    }

    public void review_insert(View view){
        if(validation()){
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Pook.URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            Pook pook = retrofit.create(Pook.class);

            HashMap<String, Object> map = new HashMap<>();
            map.put("user_id",userMap.get("ID").toString());
            map.put("nickname",userMap.get("NAME").toString());
            map.put("store_id",store_id);
            map.put("score",Float.toString(review_score.getRating()));
            map.put("review",review_review.getText().toString());

            Call<Pook_review> call = pook.setReview(map);
            call.enqueue(new Callback<Pook_review>() {
                @Override
                // 성공시
                public void onResponse(Call<Pook_review> call, Response<Pook_review> response) {
                    Pook_review contributor = response.body();

                    if(contributor.getResult().equals("200")){
                        Toast.makeText(getApplicationContext(), "정상적으로 작성되었습니다.", Toast.LENGTH_SHORT).show();
                        ((DetailActivity)DetailActivity.context).onResume();
                        finish();
                    }else{
                        Toast.makeText(getApplicationContext(), "후기작성에 실패했습니다.(관리자에게 문의)", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                // 실패시
                public void onFailure(Call<Pook_review> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "네트워크오류", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public boolean validation(){
        if(review_score.getRating() <= 0.0){
            Toast.makeText(getApplicationContext(),"점수를 선택해주세요.",Toast.LENGTH_SHORT).show();
            return false;
        }else if(review_review.getText().toString().trim().equals("")){
            Toast.makeText(getApplicationContext(),"후기를 작성해주세요.",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
