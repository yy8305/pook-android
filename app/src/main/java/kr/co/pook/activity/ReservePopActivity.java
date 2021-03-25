package kr.co.pook.activity;

import androidx.appcompat.app.AppCompatActivity;
import kr.co.pook.R;
import kr.co.pook.interfaces.Pook;
import kr.co.pook.util.SessionManager;
import kr.co.pook.vo.Pook_reserve;
import kr.co.pook.vo.Pook_store;
import kr.co.pook.vo.Pook_user;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ReservePopActivity extends Activity {

    CalendarView reserve_cal;
    TimePicker reserve_time;
    EditText reserve_people;

    Calendar calendar = Calendar.getInstance();
    private HashMap<String, String> userMap;
    private String store_id = "";
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_reserve_pop);

        init();
    }

    public void init(){
        /*인텐트 값 받아오기*/
        intent = getIntent();
        store_id = intent.getStringExtra("store_id");

        reserve_cal = findViewById(R.id.reserve_cal);
        reserve_time = findViewById(R.id.reserve_time);
        reserve_people = findViewById(R.id.reserve_people);

        /*캘린더 변경시*/
        reserve_cal.setOnDateChangeListener( new CalendarView.OnDateChangeListener() {
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            }
        });

        /*시간 변경시*/
        reserve_time.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                calendar.set(Calendar.MINUTE,minute);
            }
        });

        Date curDate = new Date(reserve_cal.getDate());
        calendar.setTime(curDate);

        if(Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M){
            reserve_time.setHour(calendar.get(Calendar.HOUR)-3);
            reserve_time.setMinute(calendar.get(Calendar.MINUTE));
        }else {
            reserve_time.setCurrentHour(calendar.get(Calendar.HOUR)-3);
            reserve_time.setCurrentMinute(calendar.get(Calendar.MINUTE));
        }

        /*사용자 계정 불러오기*/
        SessionManager sessionManager = new SessionManager(this);
        userMap = sessionManager.getUserDetail();
    }

    public void reserve_insert(View view){
        if(validation()){
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Pook.URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            Pook pook = retrofit.create(Pook.class);

            HashMap<String, Object> map = new HashMap<>();
            map.put("store_id",store_id);
            map.put("user_id",userMap.get("ID").toString());
            map.put("reserve_people",reserve_people.getText().toString());
            calendar.add(Calendar.DATE, 14);
            map.put("reserve_date",Integer.toString(calendar.get(Calendar.YEAR))+ "-" + Integer.toString(calendar.get(Calendar.MONTH)+1) + "-" +Integer.toString(calendar.get(Calendar.DATE)-14));
            map.put("reserve_time",Integer.toString(calendar.get(Calendar.HOUR_OF_DAY)) + ":" + Integer.toString(calendar.get(Calendar.MINUTE)));

            Call<Pook_reserve> call = pook.setReserve(map);
            call.enqueue(new Callback<Pook_reserve>() {
                @Override
                // 성공시
                public void onResponse(Call<Pook_reserve> call, Response<Pook_reserve> response) {
                    Pook_reserve contributor = response.body();

                    if(contributor.getResult().equals("200")){
                        Toast.makeText(getApplicationContext(), "정상적으로 예약되었습니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    }else{
                        Toast.makeText(getApplicationContext(), "예약에 실패했습니다.(관리자에게 문의)", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                // 실패시
                public void onFailure(Call<Pook_reserve> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "네트워크오류", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public boolean validation(){
        if(reserve_people.getText().toString().trim().equals("")){
            Toast.makeText(getApplicationContext(),"인원을 입력해주세요.",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
