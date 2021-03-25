package kr.co.pook.activity;

import androidx.appcompat.app.AppCompatActivity;
import kr.co.pook.R;
import kr.co.pook.adapter.ReviewAdapter;
import kr.co.pook.adapter.StoreAdapter;
import kr.co.pook.fragment.MapFragment;
import kr.co.pook.interfaces.Pook;
import kr.co.pook.util.SessionManager;
import kr.co.pook.vo.Pook_reserve;
import kr.co.pook.vo.Pook_review;
import kr.co.pook.vo.Pook_store;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class DetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    private TextView detail_name,detail_contents,detail_addr,detail_phone,detail_category,detail_open,detail_breaktime,detail_parking,detail_reserve_yn;
    private ImageView detail_thumbnail, detail_favorite;
    private ListView detail_review_list;
    Intent intent;
    private String store_id;
    private boolean favorite_yn=false;

    private ReviewAdapter reviewAdapter;
    private ArrayList<Pook_review> data_array;
    public static Context context;
    private GoogleMap mMap;

    private String store_name;
    private Double location_x;
    private Double location_y;

    //사용자 정보
    private HashMap<String, String> userMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
    }

    @Override
    protected void onResume() {
        super.onResume();

        init();

        getDetail();

        getReviewList();
    }

    public void init(){
        context = this;

        intent = getIntent();
        store_id = intent.getStringExtra("store_id");

        data_array = new ArrayList<>();

        detail_name = findViewById(R.id.detail_name);
        detail_contents = findViewById(R.id.detail_contents);
        detail_addr = findViewById(R.id.detail_addr);
        detail_phone = findViewById(R.id.detail_phone);
        detail_category = findViewById(R.id.detail_category);
        detail_open = findViewById(R.id.detail_open);
        detail_breaktime = findViewById(R.id.detail_breaktime);
        detail_parking = findViewById(R.id.detail_parking);
        detail_reserve_yn = findViewById(R.id.detail_reserve_yn);
        detail_thumbnail = findViewById(R.id.detail_thumbnail);

        detail_review_list = findViewById(R.id.detail_review_list);
        detail_favorite = findViewById(R.id.detail_favorite);
        detail_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favorite_setting();
            }
        });

        /*사용자 계정 불러오기*/
        SessionManager sessionManager = new SessionManager(this);
        userMap = sessionManager.getUserDetail();
    }

    public void close(View view){
        finish();
    }

    public void reserve_write(View view){
        Intent intent = new Intent(this, ReservePopActivity.class);
        intent.putExtra("store_id", store_id);
        startActivity(intent);
    }

    public void review_write(View view){
        Intent intent = new Intent(this, ReviewPopActivity.class);
        intent.putExtra("store_id", store_id);
        startActivity(intent);
    }

    public void getDetail(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("store_id",store_id);
        map.put("user_id",userMap.get("ID").toString());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Pook.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Pook pook = retrofit.create(Pook.class);
        Call<Pook_store> call = pook.getStoreDetail(map);

        call.enqueue(new Callback<Pook_store>() {
            @Override
            // 성공시
            public void onResponse(Call<Pook_store> call, Response<Pook_store> response) {
                Pook_store contributors = response.body();

                Picasso.get().load(Pook.URL+contributors.getThumbnail_path()).into(detail_thumbnail);
                detail_name.setText("가게명 : "+contributors.getName());
                detail_addr.setText("주소 : "+contributors.getAddr());
                detail_phone.setText("전화번호 : "+contributors.getPhone());
                detail_category.setText("음식종류 : "+contributors.getCategory());
                detail_open.setText("엽업시간 : "+contributors.getOpen());
                detail_breaktime.setText("쉬는시간 : "+contributors.getBreaktime());
                detail_parking.setText("주차여부 : "+contributors.getParking());
                if(contributors.getReserve_yn().equals("Y")){
                    detail_reserve_yn.setText("예약여부 : 예약가능");
                }
                detail_contents.setText(contributors.getContents());

                store_name = contributors.getName();
                location_x = Double.parseDouble(contributors.getLocation_x());
                location_y = Double.parseDouble(contributors.getLocation_y());
                getMap();

                /*즐겨찾기 초기화*/
                favorite_init(contributors.getFavorite_cnt());
            }

            @Override
            // 실패시
            public void onFailure(Call<Pook_store> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "네트워크오류", Toast.LENGTH_LONG).show();
            }
        });
    }


    public void getReviewList(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Pook.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Pook pook = retrofit.create(Pook.class);
        Call<List<Pook_review>> call = pook.getReviewList(store_id);

        call.enqueue(new Callback<List<Pook_review>>() {
            @Override
            // 성공시
            public void onResponse(Call<List<Pook_review>> call, Response<List<Pook_review>> response) {
                List<Pook_review> contributors = response.body();

                data_array= new ArrayList<>();
                for (Pook_review contributor : contributors) {
                    data_array.add(contributor);
                }

                reviewAdapter = new ReviewAdapter(data_array);
                detail_review_list.setAdapter(reviewAdapter);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, (120*data_array.size()));
                detail_review_list.setLayoutParams(params);
            }

            @Override
            // 실패시
            public void onFailure(Call<List<Pook_review>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "네트워크오류", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getMap(){
        //fragment_map_view
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.detail_map_view);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;

        LatLng POINT = new LatLng(location_x, location_y);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(POINT);
        markerOptions.title(store_name);
        /*markerOptions.snippet("한국의 수도");*/
        mMap.addMarker(markerOptions);
        mMap.setOnMarkerClickListener(markerClickListener);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(POINT));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    public void favorite_setting(){
        if(favorite_yn){
            favorite_yn = false;
            detail_favorite.setImageResource(R.drawable.ic_star_border_black_24dp);
            favorite_delete();
        }else{
            favorite_yn = true;
            detail_favorite.setImageResource(R.drawable.ic_star_black_24dp);
            favorite_insert();
        }
    }

    public void favorite_init(String favorite_cnt){
        if(favorite_cnt.equals("0")){
            favorite_yn = false;
            detail_favorite.setImageResource(R.drawable.ic_star_border_black_24dp);
        }else{
            favorite_yn = true;
            detail_favorite.setImageResource(R.drawable.ic_star_black_24dp);
        }
    }

    public void favorite_insert(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Pook.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Pook pook = retrofit.create(Pook.class);

        HashMap<String, Object> map = new HashMap<>();
        map.put("store_id",store_id);
        map.put("user_id",userMap.get("ID").toString());
        Call<Pook_reserve> call = pook.setFavorite(map);
        call.enqueue(new Callback<Pook_reserve>() {
            @Override
            // 성공시
            public void onResponse(Call<Pook_reserve> call, Response<Pook_reserve> response) {
                Pook_reserve contributor = response.body();

                if(contributor.getResult().equals("200")){
                    Toast.makeText(getApplicationContext(), "즐겨찾기에 추가 되었습니다.", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), "즐겨찾기 추가에 실패했습니다.(관리자에게 문의)", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            // 실패시
            public void onFailure(Call<Pook_reserve> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "네트워크오류", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void favorite_delete(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Pook.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Pook pook = retrofit.create(Pook.class);

        HashMap<String, Object> map = new HashMap<>();
        map.put("store_id",store_id);
        map.put("user_id",userMap.get("ID").toString());
        Call<Pook_reserve> call = pook.delFavorite(map);
        call.enqueue(new Callback<Pook_reserve>() {
            @Override
            // 성공시
            public void onResponse(Call<Pook_reserve> call, Response<Pook_reserve> response) {
                Pook_reserve contributor = response.body();

                if(contributor.getResult().equals("200")){
                    Toast.makeText(getApplicationContext(), "즐겨찾기가 해제 되었습니다.", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), "즐겨찾기 해제가 실패되었습니다.(관리자에게 문의)", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            // 실패시
            public void onFailure(Call<Pook_reserve> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "네트워크오류", Toast.LENGTH_LONG).show();
            }
        });
    }

    GoogleMap.OnMarkerClickListener markerClickListener = new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            String markerId = marker.getId();
            //선택한 타겟위치
            LatLng location = marker.getPosition();
            return false;
        }
    };
}
