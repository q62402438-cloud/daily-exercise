package com.example.myapplication.network;

import com.example.myapplication.model.AdminUser;
import com.example.myapplication.model.CommentEntity;
import com.example.myapplication.model.ExerciseRecord;
import com.example.myapplication.model.FavoriteEntity;
import com.example.myapplication.model.OrdinaryUser;
import com.example.myapplication.model.PostEntity;
import com.example.myapplication.model.Result;
import com.example.myapplication.model.SportsEvent;
import com.example.myapplication.model.TrainingPlan;
import com.example.myapplication.model.User;

import java.util.List;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {

    @POST("api/auth/login")
    Call<Result<User>> login(@Body User user);

    @POST("api/auth/admin/login")
    Call<Result<User>> adminLogin(@Body User user);

    @POST("api/auth/register")
    Call<Result<String>> register(@Body User user);

    @POST("api/user/update")
    Call<Result<String>> updateUser(@Body OrdinaryUser user);

    @POST("api/user/getUserInfo")
    Call<Result<OrdinaryUser>> getUserInfo(@Body User user);

    @POST("api/user/sendCode")
    Call<Result<Object>> sendCode(@Body User user);

    @POST("api/auth/resetPassword")
    Call<Result<String>> resetPassword(@Body User user);

    @POST("api/auth/forgetPassword")
    Call<Result<String>> forgetPassword(@Body User user);

    @Multipart
    @POST("api/user/changePassword")
    Call<Result<Object>> changePassword(@Part("userID") RequestBody userID, @Part("oldPassword") RequestBody oldPassword, @Part("newPassword") RequestBody newPassword);

    @GET("api/sportsEvent/list")
    Call<Result<List<SportsEvent>>> getAllSportsEvents();

    @POST("api/exerciseRecord/add")
    Call<Result<String>> addExerciseRecord(@Body ExerciseRecord record);

    @POST("api/exerciseRecord/listByUser")
    Call<Result<List<ExerciseRecord>>> getExerciseRecordsByUser(@Body User user);

    @DELETE("api/exerciseRecord/{id}")
    Call<Result<String>> deleteExerciseRecord(@Path("id") Integer id);

    @POST("api/exerciseRecord/listByDateRange")
    Call<Result<List<ExerciseRecord>>> getExerciseRecordsByDateRange(@Body ExerciseRecord request);

    @POST("api/trainingPlan/get")
    Call<Result<TrainingPlan>> getTrainingPlanById(@Body TrainingPlan plan);

    @POST("api/trainingPlan/listByUser")
    Call<Result<List<TrainingPlan>>> getTrainingPlansByUser(@Body User user);

    @POST("api/trainingPlan/create")
    Call<Result<String>> createTrainingPlan(@Body TrainingPlan plan);

    @POST("api/trainingPlan/listPublished")
    Call<Result<List<TrainingPlan>>> getPublishedTrainingPlans(@Body Map<String, Object> body);

    @POST("api/trainingPlan/update")
    Call<Result<String>> updateTrainingPlan(@Body TrainingPlan plan);

    @POST("api/trainingPlan/updateProgress")
    Call<Result<String>> updatePlanProgress(@Body Map<String, Object> body);

    @POST("api/trainingPlan/delete")
    Call<Result<String>> deleteTrainingPlan(@Body TrainingPlan plan);

    @GET("api/trainingPlan/getPending")
    Call<Result<List<TrainingPlan>>> getPendingTrainingPlans();

    @POST("api/trainingPlan/auditPass")
    Call<Result<String>> auditPlanPass(@Body Map<String, Integer> body);

    @POST("api/trainingPlan/auditReject")
    Call<Result<String>> auditPlanReject(@Body Map<String, Integer> body);

    @POST("api/post/create")
    Call<Result<String>> createPost(@Body PostEntity post);

    @POST("api/post/list")
    Call<Result<List<PostEntity>>> getPosts(@Body Map<String, Integer> body);

    @POST("api/post/get")
    Call<Result<PostEntity>> getPostById(@Body PostEntity post);

    @POST("api/post/update")
    Call<Result<String>> updatePost(@Body PostEntity post);

    @POST("api/post/delete")
    Call<Result<String>> deletePost(@Body PostEntity post);

    @GET("api/post/pending")
    Call<Result<List<PostEntity>>> getPendingPosts();

    @POST("api/post/audit")
    Call<Result<String>> auditPost(@Body Map<String, Integer> body);

    @GET("api/post/author/{authorID}")
    Call<Result<List<PostEntity>>> getPostsByAuthor(@retrofit2.http.Path("authorID") Integer authorID);

    @POST("api/comment/listByPost")
    Call<Result<List<CommentEntity>>> getCommentsByPost(@Body CommentEntity comment);

    @POST("api/comment/add")
    Call<Result<String>> addComment(@Body CommentEntity comment);

    @POST("api/comment/get")
    Call<Result<CommentEntity>> getCommentById(@Body CommentEntity comment);

    @POST("api/comment/update")
    Call<Result<String>> updateComment(@Body CommentEntity comment);

    @POST("api/comment/delete")
    Call<Result<String>> deleteComment(@Body CommentEntity comment);

    @POST("api/favorite/add")
    Call<Result<String>> addFavorite(@Body FavoriteEntity favorite);

    @POST("api/favorite/listByUser")
    Call<Result<List<FavoriteEntity>>> getFavoritesByUser(@Body User user);

    @POST("api/favorite/delete")
    Call<Result<String>> deleteFavorite(@Body FavoriteEntity favorite);

    @POST("api/favorite/check")
    Call<Result<FavoriteEntity>> checkFavorite(@Body FavoriteEntity favorite);

    @POST("api/user/delete")
    Call<Result<Object>> cancelUser(@Body User user);
}