package com.example.vidish.words;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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

//TODO Show meaning if touched on word
//TODO change edittext if user changes it in AdvanceSearch
public class MainActivity extends Activity {
    static String[] count = {"3", "4", "5", "6", "7", "8", "9", "10"};
    boolean exit = false;
    ArrayList<String> words = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    String s, selectedItem = "";
    ProgressBar progress;
    Button button;
    Spinner spinner;
    EditText editText;
    static boolean paused = false;
    Meaning meaning;

    public static boolean isAlpha(String str) {
        return str.matches("[a-zA-Z]+");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView text = (TextView) findViewById(R.id.text);
        text.setText(Html.fromHtml("<u>Advanced Search</u>", 0));

        progress = (ProgressBar) findViewById(R.id.progressbar);
        progress.setVisibility(View.GONE);
        editText = (EditText) findViewById(R.id.edittext);
        editText.setFocusedByDefault(false);
        spinner = (Spinner) findViewById(R.id.spinner);
        button = (Button) findViewById(R.id.button);
        button.setFocusedByDefault(true);
        button.setVisibility(View.GONE);
        spinner.requestFocus();
        spinner.setVisibility(View.GONE);
        spinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                return false;
            }
        });
        ((LinearLayout) findViewById(R.id.linearlayout)).setOnClickListener(new View.OnClickListener() {
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
                    if (button.isEnabled())
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
                if (charSequence.length() > 0) {
                    if (isAlpha(charSequence.charAt(charSequence.length() - 1) + "")) {
                        if (charSequence.length() > 10) {
                            Toast.makeText(MainActivity.this, "Word length cannot be more than 10", Toast.LENGTH_SHORT).show();
                            editText.setText(charSequence.toString().substring(0, charSequence.length() - 1));
                            editText.setSelection(editText.getText().length());
                        }
                        if (charSequence.length() < 3) {
                            button.setVisibility(View.GONE);
                            spinner.setVisibility(View.GONE);
                            arrayAdapter = new ArrayAdapter<String>(MainActivity.this, R.layout.spinner_layout, new String[]{});
                            spinner.setAdapter(arrayAdapter);
                            return;
                        } else {
                            button.setVisibility(View.VISIBLE);
                            spinner.setVisibility(View.VISIBLE);
                        }
                        int pos = -1;
                        if (!selectedItem.equals("") && !selectedItem.equals("3")) {
                            if (arrayAdapter.getCount() != Integer.parseInt(selectedItem) - 2) {
                                pos = spinner.getSelectedItemPosition();
                            }
                        }
                        arrayAdapter = new ArrayAdapter<String>(MainActivity.this, R.layout.spinner_layout, Arrays.copyOf(count, editText.getText().length() - 2));
                        arrayAdapter.setDropDownViewResource(R.layout.spinner_layout);
                        spinner.setAdapter(arrayAdapter);
                        if (pos != -1 && pos < arrayAdapter.getCount())
                            spinner.setSelection(pos);
                        else
                            spinner.setSelection(arrayAdapter.getCount() - 1);
                    } else {
                        Toast.makeText(MainActivity.this, "Character input only!", Toast.LENGTH_SHORT).show();
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

                startActivity(new Intent(MainActivity.this, AdvanceSearch.class)
                        .putExtra("edittext", editText.getText().toString())
                        .putExtra("spinner", (String) spinner.getSelectedItem()));
                SharedPreferences sharedPreferences = getSharedPreferences("preferences", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("edittext", editText.getText().toString());
                editor.putString("spinner", (String) spinner.getSelectedItem());
                editor.apply();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (exit) {
            super.onBackPressed();
            SharedPreferences sharedPreferences = getSharedPreferences("preferences", 0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("edittext", "");
            editor.putString("spinner", "");
            editor.commit();
            this.finish();
            return;
        }
        exit = true;
        Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                exit = false;
            }
        }, 2000);
    }


    @Override
    protected void onPause() {
        super.onPause();
        paused = true;
        SharedPreferences sharedPreferences = getSharedPreferences("preferences", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("edittext",editText.getText().toString());
        editor.putString("spinner",(String) spinner.getSelectedItem());
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (paused) {
            paused = false;
            Log.e("%%%%%%%%%%%%%%%", "ONRESUME");
            SharedPreferences sharedPreferences = getSharedPreferences("preferences", 0);
            editText.setText(sharedPreferences.getString("edittext", ""));
            editText.setSelection(editText.getText().length());
            if (!sharedPreferences.getString("spinner", "0").equals(""))
                spinner.setSelection(Integer.parseInt(sharedPreferences.getString("spinner", "0")) - 3);
            if (spinner.getSelectedItem() == null)
                spinner.setSelection(0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences sharedPreferences = getSharedPreferences("preferences", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("edittext", "");
        editor.putString("spinner", "");
        editor.apply();
    }

    private class Words extends AsyncTask<MainActivity, Void, Void> {
        @Override
        protected Void doInBackground(MainActivity... mainActivities) {
            publishProgress();
            ArrayList<String> temp = new ArrayList<>();
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(getAssets().open("words.txt")));
                String str;
                while ((str = in.readLine()) != null) {
                    if (str.length() == Integer.parseInt(selectedItem)) {
                        List<String> listEntered = new LinkedList<String>(Arrays.asList(s.split("")));
                        List<String> listFind = new LinkedList<String>(Arrays.asList(str.split("")));
                        listEntered.remove(0);
                        listFind.remove(0);
                        boolean contains = true;
                        for (int j = listFind.size() - 1; j >= 0; j--) {
                            if (listEntered.contains(listFind.get(j))) {
                                listEntered.remove(listFind.get(j));
                            } else {
                                contains = false;
                                break;
                            }
                        }
                        if (contains)
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
            if (words.size() == 0)
                words.add("NO WORDS FOUND");
            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, R.layout.spinner_layout, words);
            GridView gridView = (GridView) findViewById(R.id.gridview);
            gridView.setAdapter(adapter);
            gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    if (meaning == null)
                        meaning = new Meaning();
                    if (meaning.getStatus() == Status.RUNNING || meaning.getStatus() == Status.PENDING) {
                        meaning.cancel(true);
                    }
                    meaning = new Meaning();
                    Log.e("%%%%%%%%%%%%%%", "EXECUTING FOR " + adapter.getItem(position));
                    meaning.execute(adapter.getItem(position));
                    return true;
                }
            });
            progress.setVisibility(View.GONE);
            button.setEnabled(true);
            spinner.setEnabled(true);
        }
    }
}
