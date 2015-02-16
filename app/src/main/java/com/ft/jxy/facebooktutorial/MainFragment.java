package com.ft.jxy.facebooktutorial;

import android.support.v4.app.Fragment;
//import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestBatch;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by jxy on 15/01/15.
 */
public class MainFragment extends Fragment {

    private UiLifecycleHelper uiHelper;
    private static final String TAG = "MainFragment";

    private TextView userInfoTextView;

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, container, false);

        LoginButton authButton = (LoginButton) view.findViewById(R.id.authButton);
        authButton.setFragment(this);
        authButton.setReadPermissions(Arrays.asList("user_location", "user_birthday", "user_photos"));

        userInfoTextView = (TextView) view.findViewById(R.id.userInfoTextView);


        return view;
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Log.i(TAG, "Logged in...");
            userInfoTextView.setVisibility(View.VISIBLE);


            /*
            // Request user data and show the results
            Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

                @Override
                public void onCompleted(GraphUser user, Response response) {
                    if (user != null) {
                        // Display the parsed user info
                        String string = buildUserInfoDisplay(user);
                        userInfoTextView.setText(string);
                    }
                }
            });
            */


            new Request(session, "me/photos",getRequestParameters(), HttpMethod.GET, new Request.Callback()
            {
                @Override
                public void onCompleted(Response response)
                {

                    Log.i(TAG, "Result: " + response.toString());
                    // Process the returned response
                    GraphObject graphObject = response.getGraphObject();
                    FacebookRequestError error = response.getError();
                    if (graphObject != null) {
                        Log.d(TAG, "GraphObject get data :success");
                        if (graphObject.getProperty("data") != null) {
                            Log.d(TAG, "found data object");

                            // Get the data, parse info to get the key/value info

                            JSONObject jsonObject = graphObject
                                    .getInnerJSONObject();
                            Log.d(TAG, jsonObject.toString());


                            try {
                                JSONArray array = jsonObject
                                        .getJSONArray("data");
                                Log.d(TAG, array.toString());

                                for(int i = 0; i < array.length(); i++)
                                {
                                    JSONObject object1 = (JSONObject) array.get(i);
                                    Log.d(TAG, object1.toString());
                                    JSONObject object2 = (JSONObject) object1.get("tags");
                                    Log.d(TAG, object2.toString());
                                    JSONArray array2 =  object2.getJSONArray("data");
                                    Log.d(TAG, "tag data" + array2.toString());

                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    }

                }
            }).executeAsync();




        } else if (state.isClosed()) {
            Log.i(TAG, "Logged out...");
            userInfoTextView.setVisibility(View.INVISIBLE);
        }
    }

    private Bundle getRequestParameters()
    {
        Bundle parameters = new Bundle(2);
        parameters.putString("fields", "tags");
        return parameters;
    }

    @Override
    public void onResume() {
        super.onResume();

        // For scenarios where the main activity is launched and user
        // session is not null, the session state change notification
        // may not be triggered. Trigger it if it's open/closed.
        Session session = Session.getActiveSession();
        if (session != null &&
                (session.isOpened() || session.isClosed()) ) {
            onSessionStateChange(session, session.getState(), null);
        }

        uiHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    private String buildUserInfoDisplay(GraphUser user) {
        StringBuilder userInfo = new StringBuilder("");

        // Example: typed access (name)
        // - no special permissions required
        userInfo.append(String.format("Name: %s\n\n",
                user.getName()));

        // Example: typed access (birthday)
        // - requires user_birthday permission
        userInfo.append(String.format("Birthday: %s\n\n",
                user.getBirthday()));

        // Example: partially typed access, to location field,
        // name key (location)
        // - requires user_location permission
        userInfo.append(String.format("Location: %s\n\n",
                user.getLocation().getProperty("name")));

        // Example: access via property name (locale)
        // - no special permissions required
        userInfo.append(String.format("Locale: %s\n\n",
                user.getProperty("locale")));


        // Example: access via key for array (languages)
        // - requires user_likes permission
        JSONArray languages = (JSONArray)user.getProperty("languages");
        if (languages.length() > 0) {
            ArrayList<String> languageNames = new ArrayList<String>();
            for (int i=0; i < languages.length(); i++) {
                JSONObject language = languages.optJSONObject(i);
                // Add the language name to a list. Use JSON
                // methods to get access to the name field.
                languageNames.add(language.optString("name"));
            }
            userInfo.append(String.format("Languages: %s\n\n",
                    languageNames.toString()));
        }

        return userInfo.toString();
    }

    private String photoInfo(GraphUser user){
        StringBuilder string = new StringBuilder("");
        return String.valueOf(string.toString());
    }

}
