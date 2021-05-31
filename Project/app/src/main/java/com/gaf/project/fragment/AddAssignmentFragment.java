package com.gaf.project.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.XmlRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.gaf.project.R;
import com.gaf.project.dialog.FailDialog;
import com.gaf.project.dialog.SuccessDialog;
import com.gaf.project.model.Assignment;
import com.gaf.project.model.Class;
import com.gaf.project.model.Module;
import com.gaf.project.model.Trainer;
import com.gaf.project.response.AssignmentResponse;
import com.gaf.project.response.ClassResponse;
import com.gaf.project.response.ModuleResponse;
import com.gaf.project.response.TrainerReponse;
import com.gaf.project.service.AssignmentService;
import com.gaf.project.service.ClassService;
import com.gaf.project.service.ModuleService;
import com.gaf.project.service.TrainerService;
import com.gaf.project.utils.ApiUtils;
import com.gaf.project.viewmodel.ClassViewModel;
import com.gaf.project.viewmodel.ModuleViewModel;
import com.gaf.project.viewmodel.QuestionViewModel;
import com.gaf.project.viewmodel.TrainerViewModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddAssignmentFragment extends Fragment {

    private View view;
    private Button btnSave,btnBack;
    private AssignmentService assignmentService;
    private List<Assignment> assignmentList;
    private ArrayAdapter<Module> adapterModule;
    private ArrayAdapter<Class> adapterClass;
    private ArrayAdapter<Trainer> adapterTrainer;
    private Boolean flag = true;
    private ClassViewModel classViewModel;
    private ModuleViewModel moduleViewModel;
    private TrainerViewModel trainerViewModel;

    public AddAssignmentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assignmentService = ApiUtils.getAssignmentService();
        classViewModel = new ViewModelProvider(this).get(ClassViewModel.class);
        moduleViewModel = new ViewModelProvider(this).get(ModuleViewModel.class);
        trainerViewModel = new ViewModelProvider(this).get(TrainerViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.add_assignment, container, false);

        final Spinner spnModule = (Spinner) view.findViewById(R.id.spinner_module_name);
        moduleViewModel.getListModuleLiveData().observe(getViewLifecycleOwner(), new Observer<List<Module>>() {
            @Override
            public void onChanged(List<Module> modules) {
                adapterModule =
                        new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, modules);
                spnModule.setAdapter(adapterModule);
            }
        });

        final Spinner spnClass = (Spinner) view.findViewById(R.id.spinner_class_name);
        classViewModel.getListClassLiveData().observe(getViewLifecycleOwner(), new Observer<List<Class>>() {
            @Override
            public void onChanged(List<Class> classes) {
                adapterClass =
                        new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, classes);
                spnClass.setAdapter(adapterClass);
            }
        });

        final Spinner spnTrainer = (Spinner) view.findViewById(R.id.spinner_trainer_id);
        trainerViewModel.getListTrainerLiveData().observe(getViewLifecycleOwner(), new Observer<List<Trainer>>() {
            @Override
            public void onChanged(List<Trainer> trainers) {
                adapterTrainer =
                        new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item,trainers);
                spnTrainer.setAdapter(adapterTrainer);
            }
        });

        btnSave = view.findViewById(R.id.btn_save);
        btnSave.setOnClickListener(v->{
            Module module = (Module) spnModule.getSelectedItem();
            Class mClass = (Class) spnClass.getSelectedItem();
            Trainer trainer = (Trainer) spnTrainer.getSelectedItem();
            String code = "CL" + mClass.getClassID() +"M" + module.getModuleID() + "T"+ System.currentTimeMillis();

            Assignment newAssignment = new Assignment(code,module,trainer,mClass);

            assignmentList = new ArrayList<>();
            Call<AssignmentResponse> callListAssignment =  assignmentService.loadListAssignment();
            callListAssignment.enqueue(new Callback<AssignmentResponse>() {
                @Override
                public void onResponse(Call<AssignmentResponse> call, Response<AssignmentResponse> response) {
                    if (response.isSuccessful()&&response.body()!=null){
                        assignmentList = response.body().getAssignments();
                        flag = checkExistAssignment(assignmentList,newAssignment);
                        if(flag){
                            Call<Assignment> callAddAssignment = assignmentService.create(newAssignment);
                            callAddAssignment(callAddAssignment);
                        }else {
                            showFailDialog("Assignment already exist!");
                        }
                        Log.e("Success","Assignment get success");
                    }
                }

                @Override
                public void onFailure(Call<AssignmentResponse> call, Throwable t) {
                    Log.e("Error",t.getLocalizedMessage());
                    showToast("Call API fail!");
                }
            });

            reloadFragment();
        });

        btnBack= view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        return view;
    }

    public Boolean checkExistAssignment(List<Assignment> list, Assignment assignment){
        for(Assignment ass : list){
            if(ass.getTrainer().equals(assignment.getTrainer())
                    && ass.getModule().equals(assignment.getModule())
                    && ass.getMClass().equals(assignment.getMClass())){
                return false;
            }
        }
        return true;
    }

    public void callAddAssignment(Call<Assignment> call){
        call.enqueue(new Callback<Assignment>() {
            @Override
            public void onResponse(Call<Assignment> call, Response<Assignment> response) {
                if (response.isSuccessful()&&response.body()!=null) {
                    showSuccessDialog("Add Success!");
                }
            }

            @Override
            public void onFailure(Call<Assignment> call, Throwable t) {
                Log.e("Error",t.getLocalizedMessage());
                showFailDialog("Error");
            }
        });
    }

    public void showSuccessDialog(String message){
        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
        SuccessDialog newFragment = new SuccessDialog(message, new SuccessDialog.IClick() {
            @Override
            public void changeFragment() {

            }
        });
        newFragment.show(ft, "dialog success");
    }

    public void showFailDialog(String message){
        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
        FailDialog newFragment = new FailDialog(message);
        newFragment.show(ft, "dialog fail");
    }

    public void showToast(String string){
        Toast.makeText(getContext(),string,Toast.LENGTH_LONG).show();
    }

    public void reloadFragment(){
        if (getFragmentManager() != null) {
            getFragmentManager()
                    .beginTransaction()
                    .detach(this)
                    .attach(this)
                    .commit();
        }
    }
}