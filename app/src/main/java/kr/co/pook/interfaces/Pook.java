package kr.co.pook.interfaces;

import java.util.HashMap;
import java.util.List;

import kr.co.pook.vo.Pook_category;
import kr.co.pook.vo.Pook_reserve;
import kr.co.pook.vo.Pook_review;
import kr.co.pook.vo.Pook_store;
import kr.co.pook.vo.Pook_time;
import kr.co.pook.vo.Pook_user;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface Pook {
    String URL = "http://localhost:5000";

    @GET("/pook_user/{user_id}")
    Call<List<Pook_user>> getUserList(@Path("user_id") String user_id);

    @FormUrlEncoded
    @POST("/pook_user_insert")
    Call<Pook_user> setUser(@FieldMap HashMap<String, Object> param);

    @GET("/pook_all_store")
    Call<List<Pook_store>> getAllStoreList();

    @FormUrlEncoded
    @POST("/pook_store")
    Call<List<Pook_store>> getStoreList(@FieldMap HashMap<String, Object> param);

    @FormUrlEncoded
    @POST("/pook_store_detail")
    Call<Pook_store> getStoreDetail(@FieldMap HashMap<String, Object> param);

    @GET("/pook_reserve/{user_id}")
    Call<List<Pook_time>> getReserveList(@Path("user_id") String store_id);

    @FormUrlEncoded
    @POST("/pook_reserve_insert")
    Call<Pook_reserve> setReserve(@FieldMap HashMap<String, Object> param);

    @GET("/pook_review/{store_id}")
    Call<List<Pook_review>> getReviewList(@Path("store_id") String store_id);

    @FormUrlEncoded
    @POST("/pook_review_insert")
    Call<Pook_review> setReview(@FieldMap HashMap<String, Object> param);

    @GET("/pook_all_category")
    Call<List<Pook_category>> getAllCategoryList();

    @FormUrlEncoded
    @POST("/pook_favorite_list")
    Call<List<Pook_store>> getFavoriteList(@FieldMap HashMap<String, Object> param);

    @FormUrlEncoded
    @POST("/pook_favorite_insert")
    Call<Pook_reserve> setFavorite(@FieldMap HashMap<String, Object> param);

    @FormUrlEncoded
    @POST("/pook_favorite_delete")
    Call<Pook_reserve> delFavorite(@FieldMap HashMap<String, Object> param);
}
