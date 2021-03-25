package kr.co.pook.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import kr.co.pook.R;
import kr.co.pook.fragment.FavoriteFragment;
import kr.co.pook.fragment.MapFragment;
import kr.co.pook.fragment.ReviewListFragment;
import kr.co.pook.fragment.TimeFragment;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNav;
    private FragmentManager fragmentManager;
    private Fragment reviewList, map, time, favorite;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();

        reviewList = new ReviewListFragment();
        fragmentManager.beginTransaction().add(R.id.main_frame, reviewList).commit();

        bottomNav = findViewById(R.id.bottom_menu);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.reviewList:
                        if(reviewList==null){
                            reviewList = new ReviewListFragment();
                            fragmentManager.beginTransaction().add(R.id.main_frame, reviewList).commit();
                        }else{
                            fragmentManager.beginTransaction().show(reviewList).commit();
                        }
                        if(map!=null) fragmentManager.beginTransaction().hide(map).commit();
                        if(time!=null) fragmentManager.beginTransaction().hide(time).commit();
                        if(favorite!=null) fragmentManager.beginTransaction().hide(favorite).commit();
                        break;
                    case R.id.map:
                        if(map==null){
                            map = new MapFragment();
                            fragmentManager.beginTransaction().add(R.id.main_frame, map).commit();
                        }else{
                            fragmentManager.beginTransaction().show(map).commit();
                        }
                        if(reviewList!=null) fragmentManager.beginTransaction().hide(reviewList).commit();
                        if(time!=null) fragmentManager.beginTransaction().hide(time).commit();
                        if(favorite!=null) fragmentManager.beginTransaction().hide(favorite).commit();
                        break;
                    case R.id.time:
                        if(time==null){
                            time = new TimeFragment();
                            fragmentManager.beginTransaction().add(R.id.main_frame, time).commit();
                        }else{
                            fragmentManager.beginTransaction().show(time).commit();
                        }
                        if(reviewList!=null) fragmentManager.beginTransaction().hide(reviewList).commit();
                        if(map!=null) fragmentManager.beginTransaction().hide(map).commit();
                        if(favorite!=null) fragmentManager.beginTransaction().hide(favorite).commit();
                        break;
                    case R.id.favorite:
                        if(favorite==null){
                            favorite = new FavoriteFragment();
                            fragmentManager.beginTransaction().add(R.id.main_frame, favorite).commit();
                        }else{
                            fragmentManager.beginTransaction().detach(favorite).attach(favorite).commit();
                            fragmentManager.beginTransaction().show(favorite).commit();
                        }
                        if(reviewList!=null) fragmentManager.beginTransaction().hide(reviewList).commit();
                        if(map!=null) fragmentManager.beginTransaction().hide(map).commit();
                        if(time!=null) fragmentManager.beginTransaction().hide(time).commit();
                        break;
                }
                return true;
            }
        });
    }
}
