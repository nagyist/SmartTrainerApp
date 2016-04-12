package com.smarttrainer.smarttrainer;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.smarttrainer.smarttrainer.models.GetByID;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainPageFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainPageFrag extends Fragment {
    private int pageN;
    private static String ARG_PAGE = "ARG_PAGE";
    private View indicatorInSum = null;

    private int pushUpRepReq = 15;
    private float secPerPushReq = 2;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MainPageFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static MainPageFrag newInstance(int page) {
        MainPageFrag fragment = new MainPageFrag();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pageN = getArguments().getInt(ARG_PAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (pageN == 1)
        {
            View view = inflater.inflate(R.layout.fragment_to_workout, container, false);
            //TODO: customize ListView to support reading from db && display text+image accordingly as in workout_exer_item
            //https://www.learn2crack.com/2013/10/android-custom-listview-images-text-example.html
            //http://www.androidinterview.com/android-custom-listview-with-image-and-text-using-arrayadapter/

            LinearLayout startChallenge = (LinearLayout) view.findViewById(R.id.start_challenge);
            startChallenge.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {

                    String url = "http://52.3.117.15:8000/user/get_challenges_created_for_user?user_id=1575536576102601";
//                    + ExistingUser.getUserName(MainPageFrag.this.getContext());
                    // Instantiate the RequestQueue.
                    RequestQueue queue = Volley.newRequestQueue(getActivity());
                    Log.d("Volley", "URL: " + url);
                    // Request a string response from the provided URL.
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.d("Volley", "Get_USER_SUCCESS");
                                    ArrayList<String> form_id = new ArrayList<>();
                                    ArrayList<String> challenger = new ArrayList<>();
                                    ArrayList<String> maxFreq = new ArrayList<>();
                                    ArrayList<String> rep = new ArrayList<>();
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        JSONArray challenges = jsonObject.getJSONArray("challenges");
                                        for (int i = 0; i < challenges.length(); i++) {
                                            Log.d("JSON", "DEBUG");
                                            form_id.add(challenges.getJSONObject(i).getString("form_id"));
                                            challenger.add(challenges.getJSONObject(i).getString("challenger"));
                                            maxFreq.add(challenges.getJSONObject(i).getString("max_frequency"));
                                            rep.add(challenges.getJSONObject(i).getString("repetition"));
                                        }
                                    } catch (Exception e) {

                                    }
                                    DialogFragment newFragment = new ChallengeListFragment();
                                    Bundle args = new Bundle();
                                    args.putStringArrayList("form_id", form_id);
                                    args.putStringArrayList("challenger", challenger);
                                    args.putStringArrayList("maxFreq", maxFreq);
                                    args.putStringArrayList("rep", rep);
                                    newFragment.setArguments(args);
                                    newFragment.show(getFragmentManager(), "dialog");
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d("Volley", "GET_USER_FAILURE");
                                }
                            });
                    // Add the request to the RequestQueue.
                    queue.add(stringRequest);
                    /**
                    Intent toInstr = new Intent();
                    toInstr.putExtra("ID", 3); // form ID
                    toInstr.putExtra("freq", 0.5);
                    toInstr.putExtra("creator", "");
                    toInstr.setClass(getActivity(), InstrActivity.class);
                    startActivity(toInstr);
                     */
                }
            });

            LinearLayout benchPress = (LinearLayout) view.findViewById(R.id.bench_press_button);
            benchPress.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    Intent toInstr = new Intent();
                    toInstr.putExtra("ID", 0);
                    toInstr.setClass(getActivity(), InstrActivity.class);
                    startActivity(toInstr);
                }
            });

            LinearLayout curl = (LinearLayout) view.findViewById(R.id.dumbbell_lifting_button);
            curl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent toInstr = new Intent();
                    toInstr.putExtra("ID", 1);
                    toInstr.setClass(getActivity(), InstrActivity.class);
                    startActivity(toInstr);
                }
            });


            TextView pushUpReq = (TextView) view.findViewById(R.id.push_up_required);
            pushUpReq.setText("1 set  " + DBHelper.selectReq(getContext(), 3).getInt(0) + " reps");

            LinearLayout pushUp = (LinearLayout) view.findViewById(R.id.push_up_button);
            pushUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent toInstr = new Intent();
                    toInstr.putExtra("ID", 3);
                    toInstr.setClass(getActivity(), InstrActivity.class);
                    startActivity(toInstr);
                }
            });

            LinearLayout sitUp = (LinearLayout) view.findViewById(R.id.sit_up_button);
            sitUp.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    Intent toInstr = new Intent();
                    toInstr.putExtra("ID", 2);
                    toInstr.setClass(getActivity(), InstrActivity.class);
                    startActivity(toInstr);
                }
            });
            return view;
        }

        if (pageN == 2) {
            View view = inflater.inflate(R.layout.fragment_setting, container, false);

            final TextView repsPerSet = (TextView) view.findViewById(R.id.reps_per_set);
            SeekBar pushUpSeek = (SeekBar) view.findViewById(R.id.press_up_requirement);
            pushUpSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar thisSeek, int progress, boolean fromUser) {
                    repsPerSet.setText("Reps per set: " + progress);
                    pushUpRepReq = progress;
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            final TextView freq = (TextView) view.findViewById(R.id.freq);
            SeekBar freqSeek = (SeekBar) view.findViewById(R.id.frequency_requirement);
            freqSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar thisSeek, int progress, boolean fromUser) {
                    freq.setText("Seconds per rep: " + progress);
                    secPerPushReq = progress;
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            Button saveRep = (Button) view.findViewById(R.id.push_up_save_button);
            saveRep.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), "Successfully saved in database", Toast.LENGTH_SHORT).show();
                    final DBHelper dbHelper = new DBHelper(getContext());
                    SQLiteDatabase db = dbHelper.getReadableDatabase();
                    db.execSQL("UPDATE form_setting set repsReq=?, freq=? WHERE formID=3", new Object[]{pushUpRepReq, 1 / secPerPushReq});
                }
            });

            Button sharePressUp = (Button) view.findViewById(R.id.share_press_up);

            sharePressUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = "http://52.3.117.15:8000/user/get_all_users";
                    // Instantiate the RequestQueue.
                    RequestQueue queue = Volley.newRequestQueue(getActivity());
                    Log.d("Volley", "URL: " + url);
                    // Request a string response from the provided URL.
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.d("Volley", "Get_USER_SUCCESS");
                                    ArrayList<String> id = new ArrayList<>();
                                    ArrayList<String> name = new ArrayList<>();
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        JSONArray users = jsonObject.getJSONArray("users");
                                        for (int i = 0; i < users.length(); i++) {
                                            Log.d("JSON", "DEBUG");
                                            id.add(users.getJSONObject(i).getString("user_id"));
                                            name.add(users.getJSONObject(i).getString("name"));
                                        }
                                    } catch (Exception e) {

                                    }
                                    DialogFragment newFragment = new FriendListFragment();
                                    Bundle args = new Bundle();
                                    args.putInt("rep", pushUpRepReq);
                                    args.putDouble("fre", 1.0 / secPerPushReq);
                                    args.putStringArrayList("id", id);
                                    args.putStringArrayList("name", name);
                                    newFragment.setArguments(args);
                                    newFragment.show(getFragmentManager(), "dialog");
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d("Volley", "GET_USER_FAILURE");
                                }
                            });
                    // Add the request to the RequestQueue.
                    queue.add(stringRequest);
                }
            });

            Button getFinishedList = (Button) view.findViewById(R.id.get_finished_list);
            getFinishedList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = "http://52.3.117.15:8000/user/get_challenges_created_by_user?user_id="+
                            ExistingUser.getUserName(v.getContext());
                    // Instantiate the RequestQueue.
                    RequestQueue queue = Volley.newRequestQueue(getActivity());
                    Log.d("Volley", "URL: " + url);
                    // Request a string response from the provided URL.
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.d("Volley", "Get_USER_SUCCESS");
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        JSONArray challenges = jsonObject.getJSONArray("challenges");
//                                        ArrayList<String> form_names = new ArrayList<String>();
                                        ArrayList<String> friend_names = new ArrayList<String>();
//                                        double[] scores = new double[challenges.length()];
                                        for(int i=0;i<challenges.length();i++){
                                            JSONObject curChallenge = challenges.getJSONObject(i);
                                            String curChallengeStr = "";
                                            curChallengeStr+=(curChallenge.getString("challengee")+":   ");
                                            int form_id = curChallenge.getInt("form_id");
                                            String form_name = "";
                                            if(form_id==0){
                                                form_name = "Bench Press";
                                            } else if(form_id==1){
                                                form_name = "Curl";
                                            } else if(form_id==2){
                                                form_name = "Push-up";
                                            } else{
                                                form_name = "Sit-up";
                                            }
                                            curChallengeStr+=(form_name+"  Score:");
                                            curChallengeStr+=(curChallenge.getString("score"));
                                            friend_names.add(curChallengeStr);
                                        }
                                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainPageFrag.this.getActivity());
                                        LayoutInflater inflater = getLayoutInflater(savedInstanceState);
                                        View convertView = (View) inflater.inflate(R.layout.list, null);
                                        alertDialog.setView(convertView);
                                        alertDialog.setTitle("Challenges");
                                        ListView lv = (ListView) convertView.findViewById(R.id.listView1);
                                        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainPageFrag.this.getContext(),android.R.layout.simple_list_item_1,friend_names);
                                        lv.setAdapter(adapter);
                                        alertDialog.show();
                                    } catch (Exception e) {
                                        Log.d("Get friend's progress", e.toString());
                                    }

                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d("Volley", "GET_USER_FAILURE");
                                }
                            });
                            queue.add(stringRequest);
                }
            });
            return view;
        }

        else if (pageN == 3) {
            View view = inflater.inflate(R.layout.fragment_summary, container, false);
            FragmentTabHost fragTabHost = (FragmentTabHost) view.findViewById(android.R.id.tabhost);
            //FragmentTabHost fragTabHost = new FragmentTabHost(getActivity());
            fragTabHost.setup(getActivity(), getChildFragmentManager(), R.id.sumtabcontent);

            indicatorInSum = inflater.inflate(R.layout.summary_workout, null);
            //indicatorInSum.findViewById(R.id.tabText).setOnClickListener(null);
            fragTabHost.addTab(fragTabHost.newTabSpec("workout")
                    .setIndicator(indicatorInSum), WorkoutFragment.class, null);
            indicatorInSum = inflater.inflate(R.layout.summary_challenge, null);
            fragTabHost.addTab(fragTabHost.newTabSpec("challenge")
                    .setIndicator(indicatorInSum), ChallengeFragment.class, null);
            return view;
        }
        else
            return null;
    }
}
