package com.example.vidish.words;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

//TODO Find words that match edittext input
//TODO ListView gets pushed out of screen when adding lots of letters
public class AdvanceSearch extends Activity {

    ArrayList<String> words;
    ArrayAdapter<String> arrayAdapter, alphaAdapter, countAdapter;
    String[] count = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
    String[] alphabet = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
    LinearLayout linearLayout;
    ProgressBar progressBar;
    String selectedItem = "3";
    Button button, submit;
    Spinner spinner;
    EditText editText;
    Switch optional;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advance_search);

        words = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, count);
        button = (Button) findViewById(R.id.button);
        editText = (EditText) findViewById(R.id.edittext);
        SharedPreferences sharedPreferences = getSharedPreferences("preferences",0);
        editText.setText(sharedPreferences.getString("edittext",""));
        if (editText.length() != 0) {
            editText.setSelection(editText.length());
        }
        spinner = (Spinner) findViewById(R.id.spinner);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);
        linearLayout = (LinearLayout) findViewById(R.id.linearlayout);
        alphaAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, alphabet);
        spinner.setAdapter(arrayAdapter);
        if (sharedPreferences.getString("spinner",null) != null)
            spinner.setSelection(Integer.parseInt(sharedPreferences.getString("spinner","1")) - 1);
        else {
            if (editText.length() != 0)
                spinner.setSelection(editText.length() - 1);
        }
        submit = (Button) findViewById(R.id.submit);
        optional = (Switch) findViewById(R.id.Switch);
        optional.setChecked(false);
        if (editText.length() > 0)
            optional.setChecked(true);
        else
            optional.setChecked(false);

        if(optional.isChecked())
            editText.setEnabled(true);
        else
            editText.setEnabled(false);
        optional.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    editText.setEnabled(true);
                else
                    editText.setEnabled(false);
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                words.clear();
                if (charSequence.length() > 0) {
                    if (MainActivity.isAlpha(charSequence.charAt(charSequence.length() - 1) + "")) {
                        if (charSequence.length() > 10) {
                            Toast.makeText(AdvanceSearch.this, "Word length cannot be more than 10", Toast.LENGTH_SHORT).show();
                            editText.setText(charSequence.toString().substring(0, charSequence.length() - 1));
                            editText.setSelection(editText.getText().length());
                        }
                        spinner.setSelection(editText.length() - 1);
                    } else {
                        Toast.makeText(AdvanceSearch.this, "Character input only!", Toast.LENGTH_SHORT).show();
                        editText.setText(charSequence.toString().substring(0, charSequence.length() - 1));
                        editText.setSelection(editText.getText().length());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView != null) {
                    selectedItem = adapterView.getItemAtPosition(i).toString();
                    if (Integer.parseInt(selectedItem) - 1 > 0 && linearLayout.getChildCount() == 0) {
                        button.performClick();
                    }
                    if (linearLayout.getChildCount() > Integer.parseInt(selectedItem) - 1) {
                        while (linearLayout.getChildCount() > Integer.parseInt(selectedItem) - 1)
                            linearLayout.removeViewAt(linearLayout.getChildCount() - 1);
                    }
                    countAdapter = new ArrayAdapter<String>(AdvanceSearch.this, R.layout.spinner_layout, Arrays.copyOf(count, Integer.parseInt(selectedItem)));
                    for (int j = 0; j < linearLayout.getChildCount(); j++) {
                        LinearLayout linlay = (LinearLayout) linearLayout.getChildAt(j);
                        int pos = ((Spinner) linlay.getChildAt(1)).getSelectedItemPosition();
                        ((Spinner) linlay.getChildAt(1)).setAdapter(countAdapter);
                        if (pos < Integer.parseInt(selectedItem))
                            ((Spinner) linlay.getChildAt(1)).setSelection(pos);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedItem = (String) adapterView.getItemAtPosition(0);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (linearLayout.getChildCount() < Integer.parseInt(selectedItem) - 1) {
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View spinnerView = inflater.inflate(R.layout.layout_spinner_advanced, null);
                    linearLayout.addView(spinnerView);
                    countAdapter = new ArrayAdapter<String>(AdvanceSearch.this, R.layout.spinner_layout, Arrays.copyOf(count, Integer.parseInt(selectedItem)));
                    ((Spinner) spinnerView.findViewById(R.id.alphabet)).setAdapter(alphaAdapter);
                    ((Spinner) spinnerView.findViewById(R.id.count)).setAdapter(countAdapter);
                    if (linearLayout.getChildCount() == 1)
                        ((ImageButton) spinnerView.findViewById(R.id.imagebutton)).setVisibility(View.INVISIBLE);
                    else {
                        LinearLayout linlay = (LinearLayout) linearLayout.getChildAt(0);
                        ((ImageButton) linlay.getChildAt(2)).setVisibility(View.VISIBLE);
                    }
                } else
                    Toast.makeText(AdvanceSearch.this, "Cannot add more letters", Toast.LENGTH_SHORT).show();
            }
        });
        button.performClick();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinner.setEnabled(false);
                button.setEnabled(false);
                submit.setEnabled(false);
                ArrayList<Letters> arrayList = new ArrayList<Letters>();
                for (int i = 0; i < linearLayout.getChildCount(); i++) {
                    LinearLayout linlay = (LinearLayout) linearLayout.getChildAt(i);
                    arrayList.add(new Letters((String) ((Spinner) linlay.getChildAt(0)).getSelectedItem(),
                            Integer.parseInt((String) ((Spinner) linlay.getChildAt(1)).getSelectedItem())));
                }
                new Search().execute(arrayList);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedPreferences sharedPreferences = getSharedPreferences("preferences",0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("edittext",editText.getText().toString());
        editor.putString("spinner",(String) spinner.getSelectedItem());
        editor.apply();
        this.finish();
    }

    public void delete(View v) {
        LinearLayout linlay = (LinearLayout) linearLayout.getChildAt(0);
        if (linearLayout.getChildCount() == 2) {
            ((ImageButton) linlay.getChildAt(2)).setVisibility(View.INVISIBLE);
        }
        if (v.getParent() == linlay) {
            linlay = (LinearLayout) linearLayout.getChildAt(1);
            ((ImageButton) linlay.getChildAt(2)).setVisibility(View.INVISIBLE);
        }
        linearLayout.removeView((View) v.getParent());
    }

    private class Search extends AsyncTask<ArrayList<Letters>, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(ArrayList<Letters>... arrayList) {
            publishProgress();
            words.clear();
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(getAssets().open("words.txt")));
                String s;
                while ((s = in.readLine()) != null) {
                    if (s.length() == Integer.parseInt(selectedItem))
                        words.add(s);
                }
                for (int i = 0; i < arrayList[0].size(); i++) {
                    for (int j = words.size() - 1; j >= 0; j--)
                        if (!arrayList[0].get(i).getCharacter().equals(words.get(j).charAt(arrayList[0].get(i).getPosition() - 1) + "")) {
                            words.remove(j);
                        }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return words;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(ArrayList<String> words) {
            spinner.setEnabled(true);
            button.setEnabled(true);
            submit.setEnabled(true);
            progressBar.setVisibility(View.GONE);
            ListView list = (ListView) findViewById(R.id.listview);
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(AdvanceSearch.this, R.layout.spinner_layout, words);
            list.setAdapter(arrayAdapter);
        }
    }
}
