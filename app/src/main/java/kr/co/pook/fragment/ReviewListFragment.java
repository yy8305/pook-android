package kr.co.pook.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.fragment.app.FragmentTransaction;
import kr.co.pook.R;
import kr.co.pook.activity.DetailActivity;
import kr.co.pook.adapter.StoreAdapter;
import kr.co.pook.interfaces.Pook;
import kr.co.pook.vo.Pook_category;
import kr.co.pook.vo.Pook_store;
import kr.co.pook.vo.Pook_user;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ReviewListFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    ImageView fragment_review_refresh;
    View mView;
    ListView storeListView;
    Spinner fragment_review_search_option;
    EditText fragment_review_search_text;
    Button fragment_review_search_btn;
    private StoreAdapter sotreAdapter;
    private int page = 1;
    private ArrayList<Pook_store> data_array;

    private boolean lastItemVisibleFlag = true;    // 리스트 스크롤이 마지막 셀(맨 바닥)로 이동했는지 체크할 변수
    private ProgressBar storeProgress;                // 데이터 로딩중을 표시할 프로그레스바
    private boolean mLockListView = false;          // 데이터 불러올때 중복안되게 하기위한 변수
    private boolean lastFlag = true;

    //검색을 위한 변수
    private String search_category="";
    private String search_text="";


    public ReviewListFragment() {
        // Required empty public constructor
    }

    public static ReviewListFragment newInstance(String param1, String param2) {
        ReviewListFragment fragment = new ReviewListFragment();
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
        mView = inflater.inflate(R.layout.fragment_review_list, container, false);

        fragment_review_refresh = mView.findViewById(R.id.fragment_review_refresh);
        storeListView = mView.findViewById(R.id.storeListView);
        storeProgress = mView.findViewById(R.id.storeProgress);
        fragment_review_search_option = mView.findViewById(R.id.fragment_review_search_option);
        fragment_review_search_text = mView.findViewById(R.id.fragment_review_search_text);
        fragment_review_search_btn = mView.findViewById(R.id.fragment_review_search_btn);

        data_array = new ArrayList<>();

        storeListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // 1. OnScrollListener.SCROLL_STATE_IDLE : 스크롤이 이동하지 않을때의 이벤트(즉 스크롤이 멈추었을때).
                // 2. lastItemVisibleFlag : 리스트뷰의 마지막 셀의 끝에 스크롤이 이동했을때.
                // 3. mLockListView == false : 데이터 리스트에 다음 데이터를 불러오는 작업이 끝났을때.
                // 1, 2, 3 모두가 true일때 다음 데이터를 불러온다.
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastItemVisibleFlag && mLockListView == false && lastFlag) {
                    storeProgress.setVisibility(View.VISIBLE);
                    ++page;
                    getList2();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                lastItemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
            }
        });
        storeProgress.setVisibility(View.GONE);
        getCategory();
        getList();

        // listView의 ItemClickListener
        storeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * ListView의 Item을 Click 할 때 수행할 동작
             * @param parent 클릭이 발생한 AdapterView.
             * @param view 클릭 한 AdapterView 내의 View(Adapter에 의해 제공되는 View).
             * @param position 클릭 한 Item의 position
             * @param id 클릭 된 Item의 Id
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // adapter.getItem(position)의 return 값은 Object 형
                // 실제 Item의 자료형은 CustomDTO 형이기 때문에
                // 형변환을 시켜야 getResId() 메소드를 호출할 수 있습니다.
                String store_id = ((Pook_store)sotreAdapter.getItem(position)).getStore_id();

                // new Intent(현재 Activity의 Context, 시작할 Activity 클래스)
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                // putExtra(key, value)
                intent.putExtra("store_id", store_id);
                startActivity(intent);
            }
        });

        fragment_review_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page = 1;
                lastFlag = true;
                search_category = "";
                search_text = "";
                fragment_review_search_text.setText("");

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(ReviewListFragment.this).attach(ReviewListFragment.this).commit();
            }
        });

        fragment_review_search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_category = fragment_review_search_option.getSelectedItem().toString();
                search_text = fragment_review_search_text.getText().toString();
                page = 1;
                lastFlag = true;

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(ReviewListFragment.this).attach(ReviewListFragment.this).commit();
            }
        });

        return mView;
    }

    public void getCategory(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Pook.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Pook pook = retrofit.create(Pook.class);
        Call<List<Pook_category>> call = pook.getAllCategoryList();

        call.enqueue(new Callback<List<Pook_category>>() {
            @Override
            // 성공시
            public void onResponse(Call<List<Pook_category>> call, Response<List<Pook_category>> response) {
                List<Pook_category> contributors = response.body();

                ArrayList<String> category_array = new ArrayList<>();
                category_array.add("전체");
                for (Pook_category contributor : contributors) {
                    category_array.add(contributor.getCategory());
                }

                ArrayAdapter<String> arrAdapt=new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, category_array);
                arrAdapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                fragment_review_search_option.setAdapter(arrAdapt);

                if(!search_category.equals("") && !search_category.equals("전체")){
                    int spinnerPosition = arrAdapt.getPosition(search_category);
                    fragment_review_search_option.setSelection(spinnerPosition);
                }
            }

            @Override
            // 실패시
            public void onFailure(Call<List<Pook_category>> call, Throwable t) {
                Toast.makeText(getActivity(), "네트워크오류", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getList(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Pook.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Pook pook = retrofit.create(Pook.class);
        HashMap<String, Object> map = new HashMap<>();
        map.put("page",page);
        map.put("search_category",search_category);
        map.put("search_text",search_text);
        Call<List<Pook_store>> call = pook.getStoreList(map);

        call.enqueue(new Callback<List<Pook_store>>() {
            @Override
            // 성공시
            public void onResponse(Call<List<Pook_store>> call, Response<List<Pook_store>> response) {
                List<Pook_store> contributors = response.body();

                for (Pook_store contributor : contributors) {
                    data_array.add(contributor);
                }

                sotreAdapter = new StoreAdapter(data_array);
                storeListView.setAdapter(sotreAdapter);
            }

            @Override
            // 실패시
            public void onFailure(Call<List<Pook_store>> call, Throwable t) {
                Toast.makeText(getActivity(), "네트워크오류", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getList2(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Pook.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Pook pook = retrofit.create(Pook.class);
        HashMap<String, Object> map = new HashMap<>();
        map.put("page",page);
        map.put("search_category",search_category);
        map.put("search_text",search_text);
        Call<List<Pook_store>> call = pook.getStoreList(map);

        call.enqueue(new Callback<List<Pook_store>>() {
            @Override
            // 성공시
            public void onResponse(Call<List<Pook_store>> call, Response<List<Pook_store>> response) {
                List<Pook_store> contributors = response.body();

                ArrayList<Pook_store> data_array2 = new ArrayList<>();
                for (Pook_store contributor : contributors) {
                    data_array2.add(contributor);
                }
                sotreAdapter.addItem(data_array2);

                Log.i("getlist2size",Integer.toString(contributors.size()));
                if(contributors.size() <= 0){
                    lastFlag = false;
                    --page;
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sotreAdapter.notifyDataSetChanged();
                        storeProgress.setVisibility(View.GONE);
                        mLockListView = false;
                    }
                },1000);
            }

            @Override
            // 실패시
            public void onFailure(Call<List<Pook_store>> call, Throwable t) {
                Toast.makeText(getActivity(), "네트워크오류", Toast.LENGTH_LONG).show();
            }
        });
    }

    public boolean searchValidation(){
        if(fragment_review_search_text.getText().toString().trim().equals("") ){
            Toast.makeText(getContext(),"검색어를 입력해주세요.",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
