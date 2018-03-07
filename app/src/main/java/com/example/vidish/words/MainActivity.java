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
import android.view.MotionEvent;
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
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

//TODO Check for numeric input
//TODO Show meaning if touched on word
public class MainActivity extends Activity {
    ArrayList<String> words = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    static String[] count = {"3","4","5","6","7","8","9","10"};
    String s, selectedItem = "";
    ProgressBar progress;
    Button button;
    Spinner spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView text = (TextView) findViewById(R.id.text);
        text.setText(Html.fromHtml("<u>Advanced Search</u>",0));

        progress = (ProgressBar) findViewById(R.id.progressbar);
        progress.setVisibility(View.GONE);
        final EditText editText = (EditText) findViewById(R.id.edittext);
        editText.setFocusedByDefault(false);
        spinner = (Spinner) findViewById(R.id.spinner);
        button = (Button) findViewById(R.id.button);
        button.setFocusedByDefault(true);
        spinner.requestFocus();
        spinner.setVisibility(View.INVISIBLE);
        spinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                return false;
            }
        });
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
                    spinner.setVisibility(View.INVISIBLE);
                    arrayAdapter = new ArrayAdapter<String>(MainActivity.this,R.layout.spinner_layout,new String[]{});
                    spinner.setAdapter(arrayAdapter);
                    return;
                }
                else
                    spinner.setVisibility(View.VISIBLE);
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
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MainActivity.this,AdvanceSearch.class)
                        .putExtra("edittext",editText.getText().toString())
                        .putExtra("spinner",selectedItem));
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private class Words extends AsyncTask<MainActivity, Void, Void> {
        @Override
        protected Void doInBackground(MainActivity... mainActivities) {
            publishProgress();
            ArrayList<String> temp = new ArrayList<>();
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(getAssets().open("words.txt")));
                String str;
                while ((str = in.readLine()) != null)
                {
                    if(str.length() == Integer.parseInt(selectedItem))
                    {
                        List<String> listEntered = new LinkedList<String>(Arrays.asList(s.split("")));
                        List<String> listFind = new LinkedList<String>(Arrays.asList(str.split("")));
                        listEntered.remove(0);
                        listFind.remove(0);
                        boolean contains = true;
                        for (int j = listFind.size() - 1; j >= 0; j--)
                        {
                            if(listEntered.contains(listFind.get(j)))
                            {
                                listEntered.remove(listFind.get(j));
                            }
                            else
                            {
                                contains = false;
                                break;
                            }
                        }
                        if(contains)
                            words.add(str);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
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
