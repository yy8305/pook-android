package kr.co.pook.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.fragment.app.FragmentTransaction;
import kr.co.pook.R;
import kr.co.pook.adapter.StoreAdapter;
import kr.co.pook.adapter.TimeAdapter;
import kr.co.pook.interfaces.Pook;
import kr.co.pook.util.SessionManager;
import kr.co.pook.vo.Pook_store;
import kr.co.pook.vo.Pook_time;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TimeFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    ImageView fragment_time_refresh;
    View mView;
    ListView timeListView;
    private TimeAdapter timeAdapter;
    private ArrayList<Pook_time> data_array;
    private HashMap<String, String> userMap;

    public TimeFragment() {
        // Required empty public constructor
    }

    public static TimeFragment newInstance(String param1, String param2) {
        TimeFragment fragment = new TimeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_time, container, false);

        fragment_time_refresh = mView.findViewById(R.id.fragment_time_refresh);
        timeListView = mView.findViewById(R.id.timeListView);

        data_array = new ArrayList<>();

        /*사용자 계정 불러오기*/
        SessionManager sessionManager = new SessionManager(getActivity());
        userMap = sessionManager.getUserDetail();

        fragment_time_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(TimeFragment.this).attach(TimeFragment.this).commit();
            }
        });

        getList();

        return mView;
    }

    public void getList(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Pook.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Pook pook = retrofit.create(Pook.class);
        Call<List<Pook_time>> call = pook.getReserveList(userMap.get("ID").toString());

        call.enqueue(new Callback<List<Pook_time>>() {
            @Override
            // 성공시
            public void onResponse(Call<List<Pook_time>> call, Response<List<Pook_time>> response) {
                List<Pook_time> contributors = response.body();

                for (Pook_time contributor : contributors) {
                    data_array.add(contributor);
                }

                timeAdapter = new TimeAdapter(data_array);
                timeListView.setAdapter(timeAdapter);
            }

            @Override
            // 실패시
            public void onFailure(Call<List<Pook_time>> call, Throwable t) {
                Toast.makeText(getActivity(), "네트워크오류", Toast.LENGTH_LONG).show();
            }
        });
    }

}
