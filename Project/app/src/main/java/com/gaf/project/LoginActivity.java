package com.gaf.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.gaf.project.authentication.AuthenticationRequest;
import com.gaf.project.authentication.AuthenticationResponse;
import com.gaf.project.constant.SystemConstant;
import com.gaf.project.fragment.ModuleFragment;
import com.gaf.project.service.AuthenticationService;
import com.gaf.project.utils.ApiUtils;
import com.gaf.project.utils.SessionManager;

import java.util.Optional;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private Button btnSignIn;
    private CheckBox cbRememberMe;
    private Spinner pnRole;
    private AuthenticationService authenticationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        addControls();
        addValues();
        addEvent();
    }

    private void addValues() {
        edtEmail.setText("thao");
        edtPassword.setText("1234");

        String[] role = {SystemConstant.ADMIN_ROLE,SystemConstant.TRAINEE_ROLE,SystemConstant.TRAINER_ROLE};
        ArrayAdapter<CharSequence> roleAdapter = new ArrayAdapter<CharSequence>(getApplication(),
                R.layout.simple_spinner_item_role, role );

        roleAdapter.setDropDownViewResource(R.layout.simple_list_item_dropdown);
        pnRole.setAdapter(roleAdapter);
    }

    //get all view in activity
    public void addControls(){
        getSupportActionBar().hide();
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        btnSignIn = findViewById(R.id.btn_sign_in);
        cbRememberMe = findViewById(R.id.cb_remember_me);
        pnRole = findViewById(R.id.spinner_role);

        authenticationService = ApiUtils.getAuthenticationService();
    }
    //set event for views
    public void addEvent(){

        btnSignIn.setOnClickListener(v -> {

            final String username = edtEmail.getText().toString().trim();
            final String password = edtPassword.getText().toString().trim();
            final Boolean remember = cbRememberMe.isChecked();
            final String role = pnRole.getSelectedItem().toString();

            //if the user has not entered the complete information
            if(TextUtils.isEmpty(username)) {
            }
            if(TextUtils.isEmpty(password)){
            }
            else {
                AuthenticationRequest authenticationRequest =
                        new AuthenticationRequest(username,password,role,remember);

                authenticationService.login(authenticationRequest)
                        .enqueue( new Callback<AuthenticationResponse>() {
                            @Override
                            public void onResponse(Call<AuthenticationResponse> call, Response<AuthenticationResponse> response) {

                                if (response.isSuccessful()&&response.body()!=null){
                                    AuthenticationResponse authenticationResponse = response.body();

                                    setSession(authenticationResponse, username, role);
                                    Log.e("Success",authenticationResponse.getJwt());

                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }
                            }

                            @Override
                            public void onFailure(Call<AuthenticationResponse> call, Throwable t) {
                                Log.e("Error",t.getLocalizedMessage());
                                showToast("Error");
                            }
                        });
            }

//            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//            startActivity(intent);
        });

    }

    private void setSession(AuthenticationResponse authenticationResponse, String username, String role) {

        SystemConstant.authenticationResponse = authenticationResponse;// cái này là sao chuyển dô session dc ko

        SessionManager.getInstance().setIsLogin(true);
        SessionManager.getInstance().setUserName(username);
        SessionManager.getInstance().setUserRole(role);

//        edtPassword.setText("");
//        edtEmail.setText("");
    }


    public void showToast(String string){
        Toast.makeText(getApplication(),string,Toast.LENGTH_LONG).show();
    }

}