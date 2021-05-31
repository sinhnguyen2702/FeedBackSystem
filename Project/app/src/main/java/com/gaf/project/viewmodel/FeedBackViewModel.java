package com.gaf.project.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.gaf.project.model.Feedback;
import com.gaf.project.response.FeedbackResponse;
import com.gaf.project.response.QuestionResponse;
import com.gaf.project.service.FeedbackService;
import com.gaf.project.utils.ApiUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedBackViewModel extends ViewModel {
    private FeedbackService feedbackService;
    private MutableLiveData<List<Feedback>> mListFeedBackLiveData;
    private List<Feedback> mListFeedBack;

    public FeedBackViewModel() {
        feedbackService = ApiUtils.getFeedbackService();
        mListFeedBackLiveData = new MutableLiveData<>();

        initData();
    }

    public void initData(){
        mListFeedBack = new ArrayList<>();
        Call<FeedbackResponse> call =  feedbackService.getListFeedback();
        call.enqueue(new Callback<FeedbackResponse>() {
            @Override
            public void onResponse(Call<FeedbackResponse> call, Response<FeedbackResponse> response) {
                if (response.isSuccessful()&&response.body()!=null){
                    mListFeedBack = response.body().getFeedbacks();
                    mListFeedBackLiveData.setValue(mListFeedBack);
                }
            }

            @Override
            public void onFailure(Call<FeedbackResponse> call, Throwable t) {
                Log.e("Error",t.getLocalizedMessage());
            }
        });
    }

    public MutableLiveData<List<Feedback>> getListFeedBackLiveData() {
        return mListFeedBackLiveData;
    }
}
