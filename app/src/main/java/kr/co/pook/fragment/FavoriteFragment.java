package kr.co.pook.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.fragment.app.FragmentTransaction;
import kr.co.pook.R;
import kr.co.pook.activity.DetailActivity;
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

public class FavoriteFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private View mView;
    ImageView fragment_favorite_refresh;
    ListView favoriteListView;
    private StoreAdapter sotreAdapter;
    private ArrayList<Pook_store> data_array;
    private HashMap<String, String> userMap;

    public FavoriteFragment() {
        // Required empty public constructor
    }

    public static FavoriteFragment newInstance(String param1, String param2) {
        FavoriteFragment fragment = new FavoriteFragment();
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
        mView = inflater.inflate(R.layout.fragment_favorite, container, false);

        fragment_favorite_refresh = mView.findViewById(R.id.fragment_favorite_refresh);
        favoriteListView = mView.findViewById(R.id.favoriteListView);

        data_array = new ArrayList<>();

        /*????????? ?????? ????????????*/
        SessionManager sessionManager = new SessionManager(getActivity());
        userMap = sessionManager.getUserDetail();

        fragment_favorite_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(FavoriteFragment.this).attach(FavoriteFragment.this).commit();
            }
        });

        getList();

        // listView??? ItemClickListener
        favoriteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * ListView??? Item??? Click ??? ??? ????????? ??????
             * @param parent ????????? ????????? AdapterView.
             * @param view ?????? ??? AdapterView ?????? View(Adapter??? ?????? ???????????? View).
             * @param position ?????? ??? Item??? position
             * @param id ?????? ??? Item??? Id
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // adapter.getItem(position)??? return ?????? Object ???
                // ?????? Item??? ???????????? CustomDTO ????????? ?????????
                // ???????????? ????????? getResId() ???????????? ????????? ??? ????????????.
                String store_id = ((Pook_store)sotreAdapter.getItem(position)).getStore_id();

                // new Intent(?????? Activity??? Context, ????????? Activity ?????????)
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                // putExtra(key, value)
                intent.putExtra("store_id", store_id);
                startActivity(intent);
            }
        });

        return mView;
    }

    public void getList(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Pook.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Pook pook = retrofit.create(Pook.class);
        HashMap<String, Object> map = new HashMap<>();
        map.put("user_id",userMap.get("ID").toString());
        Call<List<Pook_store>> call = pook.getFavoriteList(map);

        call.enqueue(new Callback<List<Pook_store>>() {
            @Override
            // ?????????
            public void onResponse(Call<List<Pook_store>> call, Response<List<Pook_store>> response) {
                List<Pook_store> contributors = response.body();

                for (Pook_store contributor : contributors) {
                    data_array.add(contributor);
                }

                sotreAdapter = new StoreAdapter(data_array);
                favoriteListView.setAdapter(sotreAdapter);
            }

            @Override
            // ?????????
            public void onFailure(Call<List<Pook_store>> call, Throwable t) {
                Toast.makeText(getActivity(), "??????????????????", Toast.LENGTH_LONG).show();
            }
        });
    }

}
