package com.example.vidish.words;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class MainActivity extends Activity {
    ArrayList<String> words = new ArrayList<>();
    static Map<Integer,ArrayList<String>> map = new HashMap<>();
    ArrayAdapter<String> arrayAdapter;
    static String[] count = {"3","4","5","6","7","8","9","10"};
    String s, selectedItem = "";
    ProgressBar progress, progress1;
    Button button;
    Spinner spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView text = (TextView) findViewById(R.id.text);
        text.setText(Html.fromHtml("<u>Advanced Search</u>",0));
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,AdvanceSearch.class));
            }
        });
        text.performClick();
        progress1 = (ProgressBar) findViewById(R.id.progressbar1);
        new Load().execute(this);
        progress = (ProgressBar) findViewById(R.id.progressbar);
        progress.setVisibility(View.GONE);
        final EditText editText = (EditText) findViewById(R.id.edittext);
        editText.setFocusedByDefault(false);
        spinner = (Spinner) findViewById(R.id.spinner);
        button = (Button) findViewById(R.id.button);
        button.setFocusedByDefault(true);
        button.setEnabled(false);
        spinner.requestFocus();
        ((LinearLayout)findViewById(R.id.linearlayout)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    View view = MainActivity.this.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    if(button.isEnabled())
                        button.performClick();
                    return true;
                }
                return false;
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                words.clear();
                if (charSequence.length() > 10)
                {
                    Toast.makeText(MainActivity.this, "Word length cannot be more than 10", Toast.LENGTH_SHORT).show();
                    editText.setText(charSequence.toString().substring(0,charSequence.length() - 1));
                    editText.setSelection(editText.getText().length());
                    Log.e("^^^^^^^^^^^^", charSequence.toString());
                }
                if (charSequence.length() < 3)
                {
                    spinner.setEnabled(false);
                    arrayAdapter = new ArrayAdapter<String>(MainActivity.this,R.layout.spinner_layout,new String[]{});
                    spinner.setAdapter(arrayAdapter);
                    return;
                }
                else
                    spinner.setEnabled(true);
                arrayAdapter = new ArrayAdapter<String>(MainActivity.this, R.layout.spinner_layout, Arrays.copyOf(count,editText.getText().length()-2));
                arrayAdapter.setDropDownViewResource(R.layout.spinner_layout);
                spinner.setAdapter(arrayAdapter);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (spinner.getAdapter() != null)
                    selectedItem = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                words.clear();
                s = editText.getText().toString().toLowerCase();
                if (!selectedItem.equals(""))
                    if (s.toCharArray().length < Integer.parseInt(selectedItem))
                        Toast.makeText(MainActivity.this, "Wrong Input", Toast.LENGTH_SHORT).show();
                    else {
                        button.setEnabled(false);
                        spinner.setEnabled(false);
                        new Words().execute(MainActivity.this);
                    }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private class Load extends AsyncTask<MainActivity, Void, Void> {

        @Override
        protected Void doInBackground(MainActivity... mainActivities) {
            publishProgress();
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(getAssets().open("words.txt")));
                String line;
                while((line = in.readLine()) != null) {
                    if(map.containsKey(line.length()))
                    {
                        ArrayList temp = map.get(line.length());
                        temp.add(line);
                        map.put(line.length(),temp);
                    }
                    else {
                        ArrayList<String> temp = new ArrayList<>();
                        temp.add(line);
                        map.put(line.length(),temp);
                    }
                }
            }catch (Exception ignored){
            }
            return null;
        }

        protected void onProgressUpdate(Void... values) {
            progress1.setVisibility(View.VISIBLE);
        }

        protected void onPostExecute(Void v) {
            progress1.setVisibility(View.GONE);
            button.setEnabled(true);
            Toast.makeText(MainActivity.this, "Words Loaded", Toast.LENGTH_SHORT).show();
        }
    }

    private class Words extends AsyncTask<MainActivity, Void, Void> {
        @Override
        protected Void doInBackground(MainActivity... mainActivities) {
            publishProgress();
            ArrayList<String> temp = map.get(Integer.parseInt(selectedItem));
            for (int i=0; i < temp.size(); i++)
            {
                LinkedList<String> listEntered = new LinkedList<>(Arrays.asList(s.split("")));
                LinkedList<String> listFind = new LinkedList<>(Arrays.asList(temp.get(i).split("")));
                listEntered.remove(0);
                listFind.remove(0);
                boolean contains = true;
                for (int j=0; j < listFind.size(); j++)
                {
                    if(listEntered.contains(listFind.get(j)))
                    {
                        listEntered.removeFirstOccurrence(listFind.get(j));

                    }
                    else
                    {
                        contains = false;
                        break;
                    }
                }
                if(contains)
                    words.add(temp.get(i));
            }
            return null;
        }

        protected void onProgressUpdate(Void... values) {
            progress.setVisibility(View.VISIBLE);
        }

        protected void onPostExecute(Void v) {
            if(words.size() == 0)
                words.add("NO WORDS FOUND");
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,R.layout.spinner_layout,words);
            GridView gridView = (GridView) findViewById(R.id.gridview);
            gridView.setAdapter(adapter);
            progress.setVisibility(View.GONE);
            button.setEnabled(true);
            spinner.setEnabled(true);
        }
    }
}
