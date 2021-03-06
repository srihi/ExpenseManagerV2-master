package expmanager.idea.spark.in.expensemanager.fragments;

import android.app.DatePickerDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import expmanager.idea.spark.in.expensemanager.R;
import expmanager.idea.spark.in.expensemanager.adapters.ReportsAdapter;
import expmanager.idea.spark.in.expensemanager.common.RuntimeData;
import expmanager.idea.spark.in.expensemanager.model.CategoryItem;
import expmanager.idea.spark.in.expensemanager.model.ProductListResponse;
import expmanager.idea.spark.in.expensemanager.model.ReportResponse;
import expmanager.idea.spark.in.expensemanager.network.RetrofitApi;
import expmanager.idea.spark.in.expensemanager.utils.NetworkUtils;
import expmanager.idea.spark.in.expensemanager.utils.SessionManager;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Ramana.Reddy on 4/13/2017.
 */

public class ReportsFragment extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener{

    private TextView txtDateFrom,txtDateTo;
    private ImageView imgCalendarFrom,imgCalendarTo;
    private TextView imgRefresh;
    private RecyclerView mRecyclerView;
    private DatePickerDialog datePickerDialog;
    private Spinner spinner;
    private String categoryName;
    private int categoryId;
    private int i = 0;
    private Typeface typeface;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.reports_layout,
                container, false);

        txtDateFrom = (TextView) rootView.findViewById(R.id.txt_date);
        txtDateTo = (TextView) rootView.findViewById(R.id.txt_to_date);
        imgCalendarFrom = (ImageView) rootView.findViewById(R.id.img_calendar);
        imgCalendarTo = (ImageView) rootView.findViewById(R.id.img_to_calendar);
        typeface = Typeface.createFromAsset(getContext().getAssets(),
                "fontawesome.ttf");
        imgRefresh = (TextView) rootView.findViewById(R.id.img_refresh);
        imgRefresh.setTypeface(typeface);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_reports);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        mRecyclerView.setLayoutManager(layoutManager);

        imgCalendarFrom.setOnClickListener(this);
        imgCalendarTo.setOnClickListener(this);
        imgRefresh.setOnClickListener(this);

        spinner = (Spinner) rootView.findViewById(R.id.spinner_report_type);


//        ArrayAdapter<CharSequence> adapterper = ArrayAdapter.createFromResource(getActivity(),
//                R.array.perWhen, R.layout.simple_spinner_item);
//        adapterper.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
//        whenval.setAdapter(adapterper);

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

         datePickerDialog = new DatePickerDialog(
                getContext(), this, year, month, day);


        initCategoriesList();

        return rootView;
    }

    private void initCategoriesList(){
        if(RuntimeData.getCatelogList() == null)
            return;
        final List<String> categoryList = new ArrayList<>();
        for(CategoryItem categoryItem: RuntimeData.getCatelogList()) {
            // Initialize a new Adapter for RecyclerView

            categoryList.add(categoryItem.getCategory().getCategoryName());

                  }

        ArrayAdapter<String> adapter= new ArrayAdapter<String>(getActivity(),R.layout.simple_spinner_item, categoryList);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(2);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                categoryName = categoryList.get(position);
                categoryId = RuntimeData.getCatelogList().get(position).getCategory().getCategoryId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

    }

    @Override
    public void onClick(View view) {

        switch(view.getId()){

            case R.id.img_calendar:

                i=0;
                datePickerDialog.show();

                break;
            case R.id.img_to_calendar:

                i=1;
                datePickerDialog.show();

                break;
            case R.id.img_refresh:
                if(txtDateFrom.getText() == null || txtDateFrom.getText().toString().isEmpty() ||
                        txtDateTo.getText() == null || txtDateTo.getText().toString().isEmpty()){
                    Toast.makeText(getActivity(),"From and To Dates has to be filled",Toast.LENGTH_SHORT).show();
                    return;
                }
                //txtDateFrom.getText().toString(),txtDateTo.getText()
                serviceCall();
                break;

        }

    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {

        month = month+1;

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(year);
        stringBuilder.append("-");
        if(month >0 && month < 10){
            stringBuilder.append("0");
            stringBuilder.append(month);
        }else{
            stringBuilder.append(month);
        }
        stringBuilder.append("-");
        if(day >0 && day < 10){
            stringBuilder.append("0");
            stringBuilder.append(day);
        }else{
            stringBuilder.append(day);
        }

        if(i==0){
            //txtDateFrom.setText(day+"-"+month+"-"+year);
            txtDateFrom.setText(stringBuilder.toString());
        }else {
            txtDateTo.setText(stringBuilder.toString());
        }
    }

    private void serviceCall(){

        if (!NetworkUtils.getInstance().isNetworkAvailable(getActivity())) {

            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            return;
        }

        SessionManager sessionManager = new SessionManager(getActivity());
        RetrofitApi.getApi().GetReports(sessionManager.getAuthToken(), txtDateFrom.getText().toString(),txtDateTo.getText().toString(),categoryId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {



                if (response.isSuccessful()) {

                    try {

                    Gson gson = new Gson();
                    String jsonArray = response.body().string();

                        Type listType = new TypeToken<List<ReportResponse>>(){}.getType();
                        List<ReportResponse> myModelList = gson.fromJson(jsonArray, listType);

                        ReportsAdapter reportsAdapter = new ReportsAdapter(getActivity(),myModelList);
                        mRecyclerView.setAdapter(reportsAdapter);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {

                    try {
                        Toast.makeText(getActivity(), response.errorBody().string(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

               // progressBar.setVisibility(View.GONE);

                Toast.makeText(getActivity(), "Oops something went wrong", Toast.LENGTH_SHORT).show();

            }
        });

    }
}
