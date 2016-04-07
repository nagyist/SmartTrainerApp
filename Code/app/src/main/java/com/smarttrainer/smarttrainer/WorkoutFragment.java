package com.smarttrainer.smarttrainer;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class WorkoutFragment extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String selectSet = "SELECT timestamp, reps, score FROM workout_history WHERE timestamp > ? AND formID = ?";
    private String curMonth;
    private SQLiteDatabase db;

    ListView workoutList;
    TextView weeklyRep;
    TextView weeklyDay;
    TextView weeklyTime;

    public WorkoutFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment WorkoutFragment.

    // TODO: Rename and change types and number of parameters
    public static WorkoutFragment newInstance(String param1, String param2) {
        WorkoutFragment fragment = new WorkoutFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_workout, container, false);
        Spinner spinner = (Spinner) view.findViewById(R.id.spinner_workout);
        workoutList = (ListView) view.findViewById(R.id.WorkoutSumList);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.workout_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        final TextView month = (TextView) view.findViewById(R.id.month);
        month.setText(Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US));

        weeklyRep = (TextView) view.findViewById(R.id.month_rep);
        weeklyDay = (TextView) view.findViewById(R.id.weeklyDay);
        weeklyTime = (TextView) view.findViewById(R.id.week_time);

        final DBHelper dbHelper = new DBHelper(getContext());
        db = dbHelper.getReadableDatabase();
        curMonth = getThisMonth();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                String[] forms = getResources().getStringArray(R.array.workout_array);
                Cursor cursor = db.rawQuery(selectSet, new String[]{curMonth, String.valueOf(id)});
                //Toast.makeText(getActivity(), "pos: " + pos + " id: " + id, Toast.LENGTH_SHORT).show();

                Timestamp timeStart = null, timeEnd = null;
                int firstSet = 0;
                int dailySet = 0;
                int dailyRep = 0;
                int weeklyDays = 0;
                int weeklyReps = 0;
                long weeklyTotalTime = 0;
                double totalScore = 0;

                ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
                Calendar thisWeek = Calendar.getInstance();
                thisWeek.set(Calendar.HOUR, 0);
                thisWeek.set(Calendar.MINUTE, 0);
                thisWeek.set(Calendar.SECOND, 0);
                thisWeek.add(Calendar.DAY_OF_YEAR, -6);

                if (cursor != null)
                    if (cursor.moveToFirst())
                    {
                        do
                        {
                            Timestamp curItem = Timestamp.valueOf(cursor.getString(cursor.getColumnIndex("timestamp")));

                            if (timeStart != null && curItem.getDate() != timeStart.getDate())
                            {
                                HashMap<String, String> map = new HashMap<String, String>();
                                putDay(timeStart, map);
                                map.put("sets", String.valueOf(dailySet));
                                map.put("reps", String.valueOf(dailyRep / dailySet));
                                weeklyReps += dailyRep;
                                weeklyTotalTime += putDuration(timeStart, timeEnd, firstSet, map);
                                putScore(totalScore, dailySet, map);
                                mylist.add(map);
                                weeklyDays ++;
                            }

                            if (timeStart == null && curItem.getTime() > thisWeek.getTime().getTime())
                            {
                                //Log.d("found", curItem.toString());
                                timeStart = curItem;
                                firstSet = cursor.getInt(cursor.getColumnIndex("reps"));
                                dailyRep = 0;
                                dailySet = 0;
                                totalScore = 0;
                            }
                            timeEnd = curItem;
                            dailyRep += cursor.getInt(cursor.getColumnIndex("reps"));
                            dailySet ++;
                            totalScore += cursor.getFloat(cursor.getColumnIndex("score"));
                        } while (cursor.moveToNext());

                        HashMap<String, String> map = new HashMap<String, String>();
                        putDay(timeStart, map);
                        map.put("sets", String.valueOf(dailySet));
                        map.put("reps", String.valueOf(dailyRep / dailySet));
                        putScore(totalScore, dailySet, map);
                        weeklyReps += dailyRep;
                        weeklyTotalTime += putDuration(timeStart, timeEnd, firstSet, map);
                        mylist.add(map);
                        weeklyDays ++;

                        weeklyRep.setText(String.valueOf(weeklyReps));
                        weeklyDay.setText(String.valueOf(weeklyDays));
                        weeklyTime.setText(String.format("%02d:%02d:%02d",
                                TimeUnit.MILLISECONDS.toHours(weeklyTotalTime),
                                TimeUnit.MILLISECONDS.toMinutes(weeklyTotalTime) -
                                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(weeklyTotalTime)),
                                TimeUnit.MILLISECONDS.toSeconds(weeklyTotalTime) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(weeklyTotalTime))));
                    }

                /*HashMap<String, String> anthmap = new HashMap<String, String>();
                anthmap.put("Day", "Monday");
                mylist.add(anthmap);*/

                Collections.reverse(mylist);
                //生成适配器，数组===》ListItem  */
                SimpleAdapter mSchedule = new SimpleAdapter(getActivity(), mylist,//数据来源
                        R.layout.workout_sum_item,//ListItem的XML实现
                        new String[] {"Day", "sets", "reps", "duration", "integer", "decimal"},     //动态数组与ListItem对应的子项
                        new int[] {R.id.weekDay, R.id.exer_sets_sum, R.id.exer_reps_sum, R.id.exer_duration,
                            R.id.scoreInt, R.id.scoreDec});//ListItem的XML文件里面的multimple TextView ID
                workoutList.setAdapter(mSchedule);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
        return view;
    }

    private String getThisMonth()
    {
        Timestamp today = new Timestamp(System.currentTimeMillis());
        today.setDate(1);
        today.setHours(0);
        today.setMinutes(0);
        today.setSeconds(0);
        return today.toString();
    }

    private void putScore(double totalScore, int dailySet, HashMap<String, String> map)
    {
        double avg = totalScore / dailySet;
        int intPart = (int) avg;
        map.put("integer", String.valueOf(intPart));
        int decimal = (int)((avg - intPart) * 10);
        map.put("decimal", "." + decimal + '%');
    }

    private void putDay(Timestamp timeStart, HashMap<String, String> map)
    {
        Calendar now = Calendar.getInstance();
        if (timeStart.getDate() == now.get(Calendar.DAY_OF_MONTH))
            map.put("Day", "Today");
        else {
            Calendar item = Calendar.getInstance();
            item.setTime(timeStart);
            map.put("Day", item.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US));
        }
        timeStart = null;
    }

    private long putDuration(Timestamp timeStart, Timestamp timeEnd, int firstSet, HashMap<String, String> map)
    {
        long millis = timeEnd.getTime() - timeStart.getTime();
        millis += (3000 * firstSet);
        map.put("duration", String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis))));
        return millis;
    }
    /*
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    *
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }*/
}
