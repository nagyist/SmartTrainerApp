package com.smarttrainer.smarttrainer;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.smarttrainer.smarttrainer.models.GetByID;

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
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (pageN == 1)
        {
            View view = inflater.inflate(R.layout.fragment_to_workout, container, false);
            //TODO: customize ListView to support reading from db && display text+image accordingly as in workout_exer_item
            //https://www.learn2crack.com/2013/10/android-custom-listview-images-text-example.html
            //http://www.androidinterview.com/android-custom-listview-with-image-and-text-using-arrayadapter/

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
            pushUpReq.setText("1 set  " + DBHelper.selectReq(getContext(), 3) + " reps");

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
                    db.execSQL("UPDATE form_setting set repsReq=?, freq=? WHERE formID=3", new Object[]{pushUpRepReq, 1/secPerPushReq});
                }
            });

            Button sharePressUp = (Button) view.findViewById(R.id.share_press_up);
            sharePressUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            Button getFinishedList = (Button) view.findViewById(R.id.get_finished_list);
            getFinishedList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

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
