package com.example.android.ffxivhelper;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.ffxivhelper.utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText mSearchBoxEditText;
    private TextView mUrlDisplayTextView;
    private TextView mSearchResultsTextView;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSearchBoxEditText = findViewById(R.id.et_search_box);
        mUrlDisplayTextView = findViewById(R.id.tv_url_display);
        mSearchResultsTextView = findViewById(R.id.tv_xivapi_search_results_json);
        mErrorMessageDisplay = findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);
    }

    /**
     * This method retrieves the search text from the EditText, constructs the
     * URL (using {@link NetworkUtils}) for the XIVAPI repository you'd like to find, displays
     * that URL in a TextView, and finally fires off an AsyncTask to perform the GET request using
     * our {@link xivapiQueryTask}
     */
    private void makeXivapiSearchQuery() {
        String xivapiQuery = mSearchBoxEditText.getText().toString();
        URL xivapiSearchUrl = NetworkUtils.buildUrl(xivapiQuery);
        mUrlDisplayTextView.setText(xivapiSearchUrl.toString());
        new xivapiQueryTask().execute(xivapiSearchUrl);
    }

    /**
     * This method will make the View for the JSON data visible and
     * hide the error message.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showJsonDataView() {
        // First, make sure the error is invisible
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        // Then, make sure the JSON data is visible
        mSearchResultsTextView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the error message visible and hide the JSON
     * View.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showErrorMessage() {
        // First, hide the currently visible data
        mSearchResultsTextView.setVisibility(View.INVISIBLE);
        // Then, show the error
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    public class xivapiQueryTask extends AsyncTask<URL, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(URL... params) {
            URL searchUrl = params[0];
            String xivapiSearchResults = null;
            JSONObject jsonResults = null;
            try {
                xivapiSearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
                jsonResults = new JSONObject(xivapiSearchResults);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (final JSONException e) {
                Log.e("FAILED", "Json parsing error: " + e.getMessage());
            }
            return jsonResults;
        }

        @Override
        protected void onPostExecute(JSONObject jsonResults) {
            //TODO: Shift the JSON parsing out to a function (or even class of functions)
            //TODO: Handle multiple pages of results
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (jsonResults != null && !jsonResults.equals("")) {
                showJsonDataView();
                String formattedResults = "";
                try {
                    JSONArray resultsList = jsonResults.getJSONArray("Results");
                    for (int i = 0; i < resultsList.length(); i++) {
                        JSONObject result = resultsList.getJSONObject(i);
                        String name = result.getString("Name");
                        String ID = result.getString("ID");
                        formattedResults += name + ": " + ID + "\n";
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mSearchResultsTextView.setText(formattedResults);
            } else {
                showErrorMessage();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();
        if (itemThatWasClickedId == R.id.action_search) {
            makeXivapiSearchQuery();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
