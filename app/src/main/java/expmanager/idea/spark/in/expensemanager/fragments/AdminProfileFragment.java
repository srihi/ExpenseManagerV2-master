package expmanager.idea.spark.in.expensemanager.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import expmanager.idea.spark.in.expensemanager.LoginActivity;
import expmanager.idea.spark.in.expensemanager.R;
import expmanager.idea.spark.in.expensemanager.UsePinActivity;
import expmanager.idea.spark.in.expensemanager.network.RetrofitApi;
import expmanager.idea.spark.in.expensemanager.utils.SessionManager;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Haresh.Veldurty on 2/22/2017.
 */

public class AdminProfileFragment extends Fragment {

    private EditText username,email;
    private Button logout;
    private ProgressBar progressBar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
    public AdminProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.adminprofile_layout,
                container, false);

         username = (EditText) rootView.findViewById(R.id.et_user_name);
         email = (EditText) rootView.findViewById(R.id.et_email);
        logout = (Button) rootView.findViewById(R.id.btn_logout);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);


        final SessionManager sessionManager = new SessionManager(getActivity());
        username.setText(sessionManager.getUserName());
        email.setText(sessionManager.getEmailId());

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                progressBar.setVisibility(View.VISIBLE);
                RetrofitApi.getApi().Logout(sessionManager.getAuthToken()).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        progressBar.setVisibility(View.GONE);

                        if (response.isSuccessful()) {

                            Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                            sessionManager.logoutUser();
                            Intent i = new Intent(getActivity(), LoginActivity.class);
                            startActivity(i);
                            getActivity().finish();

                        } else {

                            try {
                                String errorBody = response.errorBody().string();
                                JSONObject jsonObject = new JSONObject(errorBody);
                                Toast.makeText(getActivity(), jsonObject.optString("message"), Toast.LENGTH_SHORT).show();

                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                        progressBar.setVisibility(View.GONE);

                        Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });

        return rootView;
    }


}
