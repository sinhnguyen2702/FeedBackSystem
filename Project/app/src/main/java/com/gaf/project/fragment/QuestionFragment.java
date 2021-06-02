package com.gaf.project.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Spinner;
import android.widget.Toast;

import com.gaf.project.R;
import com.gaf.project.adapter.QuestionAdapter;
import com.gaf.project.constant.SystemConstant;
import com.gaf.project.dialog.FailDialog;
import com.gaf.project.dialog.SuccessDialog;
import com.gaf.project.dialog.WarningDialog;
import com.gaf.project.model.Feedback;
import com.gaf.project.model.Question;
import com.gaf.project.model.Topic;
import com.gaf.project.viewmodel.FeedBackViewModel;
import com.gaf.project.viewmodel.QuestionViewModel;
import com.gaf.project.viewmodel.TopicViewModel;

import java.util.Collection;
import java.util.List;

public class QuestionFragment extends Fragment {

    private View view;
    private QuestionViewModel questionViewModel;
    private TopicViewModel topicViewModel;
    private FeedBackViewModel feedBackViewModel;
    private RecyclerView recyclerViewQuestion;
    private QuestionAdapter questionAdapter;
    private ArrayAdapter<Topic> topicArrayAdapter;
    private List<Feedback> feedbackList;
    private List<Topic> topicList;
    private Button btnAdd;
    private Spinner sprTopic;
    private Boolean checkDeleteFlag = false;
    private Integer countUse = 0;

    public QuestionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        questionViewModel = new ViewModelProvider(this).get(QuestionViewModel.class);
        topicViewModel = new ViewModelProvider(this).get(TopicViewModel.class);
        feedBackViewModel = new ViewModelProvider(this).get(FeedBackViewModel.class);
    }

    @Override
    public void onStart(){
        super.onStart();

        //Update data every time you enter Fragment
        questionViewModel.initData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_question, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //declare recyclerview(view, adapter and set layout)
        recyclerViewQuestion = view.findViewById(R.id.rcv_question);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerViewQuestion.setLayoutManager(linearLayoutManager);
        questionAdapter = new QuestionAdapter(new QuestionAdapter.IClickItem() {
            @Override
            public void update(Question item) {
                clickUpdate(item);
            }

            @Override
            public void delete(Question item) {
                clickDelete(item);
            }
        });

        //set data for question adapter
        questionViewModel.getListQuestionLiveData().observe(getViewLifecycleOwner(), new Observer<List<Question>>() {
            @Override
            public void onChanged(List<Question> questions) {
                questionAdapter.setData(questions);
            }
        });

        //set data for recyclerview
        recyclerViewQuestion.setAdapter(questionAdapter);

        //get topic and set filter
        topicViewModel.getListTopicLiveData().observe(getViewLifecycleOwner(), new Observer<List<Topic>>() {
            @Override
            public void onChanged(List<Topic> topics) {
                sprTopic = view.findViewById(R.id.spinner_topic_name);

                //create default topic to filter
                Topic topic = new Topic(0,"Show All");

                try {
                    topics.remove(topic); //remove default value
                } finally {

                    //create new list topic and add default value to it
                    topicList = topics;
                    topicList.add(0,topic);

                    //set data for topic spinner
                    topicArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, topicList);
                    sprTopic.setAdapter(topicArrayAdapter);
                }

                //set filter on item selected
                sprTopic.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Topic topic = topicArrayAdapter.getItem(position);
                        questionAdapter.getFilter().filter(topic.getTopicName(), new Filter.FilterListener() {
                            @Override
                            public void onFilterComplete(int count) {

                            }
                        });
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        });

        ////go to add question page
        btnAdd = view.findViewById(R.id.btn_add_question);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("mission", SystemConstant.ADD);

                Navigation.findNavController(view).navigate(R.id.action_nav_question_to_add_question_fragment,bundle);
            }
        });
    }

    ////go to update question page
    private void clickUpdate(Question item) {
        Bundle bundle = new Bundle();
        bundle.putString("mission", SystemConstant.UPDATE);
        bundle.putSerializable("item", item);

        Navigation.findNavController(view).navigate(R.id.action_nav_question_to_add_question_fragment,bundle);
    }

    private void clickDelete(Question item){

        countUse = 0;
        checkDeleteFlag = false;

        //get list feedback
        feedBackViewModel.getListFeedBackLiveData().observe(getViewLifecycleOwner(), new Observer<List<Feedback>>() {
            @Override
            public void onChanged(List<Feedback> feedbacks) {
                feedbackList = feedbacks;
            }
        });

        for (Feedback m : feedbackList) {
            Collection<Question> questions = m.getQuestions(); //foreach feedback in list, get list question of feedback
            for (Question question : questions) {
                if (question.equals(item)) {    //if question exist set check = true and increase count
                    checkDeleteFlag = true;
                    countUse++;
                }

                //show dialog
                FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                final WarningDialog dialog;
                if (checkDeleteFlag) {      //check delete flag and show warning to delete
                    dialog = new WarningDialog(
                            () -> {
                                showDialog(questionViewModel.deleteQuestion(item),"Delete");
                            },
                            "This Question is in use with " + countUse + " Feedback.You really want to delete this Question?");
                } else {
                    dialog = new WarningDialog(
                            () -> {

                                showDialog(questionViewModel.deleteQuestion(item),"Delete");
                            },
                            "Do you want to delete this Question?");
                }
                dialog.show(ft, "dialog success");
            }
        }
    }

    //show dialog when the action is finished
    public void showDialog(MutableLiveData<String> actionStatus, String action){
        actionStatus.observe(getViewLifecycleOwner(),s -> {
            if(s.equals(SystemConstant.SUCCESS)){
                showSuccessDialog(action+" Success!!");
            }else {
                showFailDialog(action+" Fail!!");
            }
        });
    }

    //show toast
    public void showToast(String string){
        Toast.makeText(getContext(),string,Toast.LENGTH_LONG).show();
    }

    //show success dialog
    public void showSuccessDialog(String message){
        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
        SuccessDialog newFragment = new SuccessDialog(message, new SuccessDialog.IClick() {
            @Override
            public void changeFragment() {

            }
        });
        newFragment.show(ft, "dialog success");
    }

    //show fail dialog
    public void showFailDialog(String message){
        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
        FailDialog newFragment = new FailDialog(message);
        newFragment.show(ft, "dialog fail");
    }
}