package com.example.vidish.words;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;

public class AdvanceSearch extends Activity {

    ArrayAdapter<String> arrayAdapter,alphaAdapter,countAdapter;
    String[] count = {"1","2","3","4","5","6","7","8","9","10"};
    String[] alphabet = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
    LinearLayout linearLayout;
    ProgressBar progressBar;
    String selectedItem = "3";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advance_search);

        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);
        linearLayout = (LinearLayout) findViewById(R.id.linearlayout);
        arrayAdapter = new ArrayAdapter<String>(this,R.layout.spinner_layout,MainActivity.count);
        alphaAdapter = new ArrayAdapter<String>(this,R.layout.spinner_layout,alphabet);
        spinner.setAdapter(arrayAdapter);
        Button button = (Button) findViewById(R.id.button);
        Button submit = (Button) findViewById(R.id.submit);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(adapterView != null)
                {
                    selectedItem = adapterView.getItemAtPosition(i).toString();
                    if (linearLayout.getChildCount() > Integer.parseInt(selectedItem) - 1)
                    {
                        while (linearLayout.getChildCount() > Integer.parseInt(selectedItem) - 1)
                            linearLayout.removeViewAt(linearLayout.getChildCount() - 1);
                    }
                    countAdapter = new ArrayAdapter<String>(AdvanceSearch.this,R.layout.spinner_layout,Arrays.copyOf(count,Integer.parseInt(selectedItem)));
                    for (int j = 0; j < linearLayout.getChildCount(); j++)
                    {
                        LinearLayout linlay = (LinearLayout) linearLayout.getChildAt(j);
                        int pos = ((Spinner)linlay.getChildAt(1)).getSelectedItemPosition();
                        ((Spinner)linlay.getChildAt(1)).setAdapter(countAdapter);
                        if(pos<Integer.parseInt(selectedItem))
                            ((Spinner)linlay.getChildAt(1)).setSelection(pos);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedItem = (String)adapterView.getItemAtPosition(0);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (linearLayout.getChildCount() < Integer.parseInt(selectedItem) - 1) {
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View spinnerView = inflater.inflate(R.layout.layout_spinner_advanced, null);
                    linearLayout.addView(spinnerView);
                    countAdapter = new ArrayAdapter<String>(AdvanceSearch.this, R.layout.spinner_layout, Arrays.copyOf(count,Integer.parseInt(selectedItem)));
                    ((Spinner) spinnerView.findViewById(R.id.alphabet)).setAdapter(alphaAdapter);
                    ((Spinner) spinnerView.findViewById(R.id.count)).setAdapter(countAdapter);
                    if(linearLayout.getChildCount() == 1)
                        ((ImageButton)spinnerView.findViewById(R.id.imagebutton)).setVisibility(View.INVISIBLE);
                    else
                    {
                        LinearLayout linlay = (LinearLayout) linearLayout.getChildAt(0);
                        ((ImageButton)linlay.getChildAt(2)).setVisibility(View.VISIBLE);
                    }
                }
                else
                    Toast.makeText(AdvanceSearch.this, "Cannot add more letters", Toast.LENGTH_SHORT).show();
            }
        });
        button.performClick();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Letters> arrayList = new ArrayList<Letters>();
                for(int i=0; i < linearLayout.getChildCount(); i++)
                {
                    LinearLayout linlay = (LinearLayout) linearLayout.getChildAt(i);
                    arrayList.add(new Letters((String)((Spinner)linlay.getChildAt(0)).getSelectedItem(),
                            Integer.parseInt((String)((Spinner)linlay.getChildAt(1)).getSelectedItem())));
                }
                new Search().execute(arrayList);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void delete(View v)
    {
        LinearLayout linlay = (LinearLayout) linearLayout.getChildAt(0);
        if(linearLayout.getChildCount() == 2)
        {
            ((ImageButton)linlay.getChildAt(2)).setVisibility(View.INVISIBLE);
        }
        if(v.getParent() == linlay)
        {
            linlay = (LinearLayout) linearLayout.getChildAt(1);
            ((ImageButton)linlay.getChildAt(2)).setVisibility(View.INVISIBLE);
        }
        linearLayout.removeView((View) v.getParent());
    }

    private class Search extends AsyncTask<ArrayList<Letters>,Void,Void>
    {

        @Override
        protected Void doInBackground(ArrayList<Letters>... arrayList) {
            return null;
        }
    }
}
