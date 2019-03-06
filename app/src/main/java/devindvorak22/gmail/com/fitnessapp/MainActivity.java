package devindvorak22.gmail.com.fitnessapp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
//import android.content.res.XmlResourceParser;


public class MainActivity extends AppCompatActivity {

    private Context applicationContext;

    // This scroll view contains the linearlayout workoutview
    private ScrollView workoutScrollView;  // May never need to use this variable, if so delete from oncreate
    private LinearLayout workoutView;

    // Navigation bar at the bottom
    private BottomNavigationView bottomNavigationView;
    private Button startWorkoutButton;

    // Bool that keeps track of the back button (in action bar) and what it should come back to
    private boolean backFromWorkout = false;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    //mTextMessage.setText(R.string.title_home);
                    setTitle(R.string.title_home);
                    SwitchToHomeView();
                    return true;
                case R.id.navigation_dashboard:
                    //mTextMessage.setText(R.string.title_workouts);
                    setTitle(R.string.title_workouts);
                    SwitchToWorkoutView();
                    return true;
                case R.id.navigation_notifications:
                    setTitle(R.string.title_settings);
                    SwitchToSettingsView();
                    return true;
            }
            return false;
        }
    };

    private void SwitchToWorkoutView() {
        //view.setText("Add your text here");
        workoutView.setVisibility(View.VISIBLE);
    }

    private void SwitchToHomeView() {
        //view.setText("Add your text here");
        workoutView.setVisibility(View.INVISIBLE);
    }

    private void SwitchToSettingsView() {
        //view.setText("Add your text here");
        workoutView.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        workoutScrollView = findViewById(R.id.workoutScroll);
        workoutView = findViewById(R.id.workoutLinearLayout);

        bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        startWorkoutButton = findViewById(R.id.startWorkoutButton);
        startWorkoutButton.setVisibility(View.INVISIBLE);

        // Call the switch to home view function to hide and show everything that should be shown at the start
        SwitchToHomeView();

        // Load up the exercises and workouts for the workout view
        applicationContext = getApplicationContext();

        // Load up all of the workout data
        LoadWorkouts();
    }




    // This function loads the workout data and then creates the views for each workout
    public void LoadWorkouts() {
        // Read the json data for the workouts
        InputStream is = getResources().openRawResource(R.raw.workouts);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        // Lol da fuq is all this shit
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // This is the json file converted to a string
        String jsonString = writer.toString();
        // Use google gson to convert json string to the workouts class
        Gson gson = new Gson();
        Workouts workoutFromJSON = gson.fromJson(jsonString, Workouts.class);

        // Layout parameters for the workout container
        ViewGroup.MarginLayoutParams containerParams = new ViewGroup.MarginLayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        );
        // If i no longer need margins then i can change back to layoutsparams
        // instead of marginlayoutparams
        containerParams.setMargins(-5, 0, -5, 0);

        // Layout parameters for the workout name text
        ViewGroup.MarginLayoutParams nameParams = new ViewGroup.MarginLayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        );
        nameParams.setMargins(20, 25, 0, 0);

        // Layout parameters for the workout sets text
        ViewGroup.MarginLayoutParams setsParams = new ViewGroup.MarginLayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        );
        setsParams.setMargins(30, 100, 0, 0);

        RelativeLayout[] workoutContainer = new RelativeLayout[workoutFromJSON.getLength()];
        TextView[] workoutTextName = new TextView[workoutFromJSON.getLength()];
        TextView[] workoutTextSets = new TextView[workoutFromJSON.getLength()];
        Drawable workoutBorder = getResources().getDrawable(R.drawable.workout_border);

        // format workout container and its contents then add to workout view
        for (int i = 0; i < workoutFromJSON.getLength(); i++) {
            // First format workout container
            workoutContainer[i] = new RelativeLayout(applicationContext);
            workoutContainer[i].setLayoutParams(containerParams);
            workoutContainer[i].setBackground(workoutBorder);
            workoutContainer[i].setClickable(true);
            // When this is clicked we call the workoutonclicklistener function
            //workoutContainer[i].setOnClickListener(new WorkoutOnClickListener(i, workoutFromJSON));
            final Workouts.Workout workout = workoutFromJSON.workouts[i];
            workoutContainer[i].setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    workoutView.removeAllViews();

                    final Workouts.Workout.Set[] sets = workout.getSets();
                    TextView[] tvs = new TextView[sets.length * 2 - 1];
                    for (int i = 0; i < tvs.length; i++) {
                        int setsIndex = i / 2;
                        if (i % 2 == 0) {
                            tvs[i] = new TextView(applicationContext);
                            tvs[i].setText(sets[setsIndex].getName() + " " + i);
                            tvs[i].setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
                            workoutView.addView(tvs[i]);
                        }
                        else {
                            tvs[i] = new TextView(applicationContext);
                            tvs[i].setText("Rest " + sets[setsIndex].getRest() + " seconds");
                            tvs[i].setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                            workoutView.addView(tvs[i]);
                        }
                    }
                    //bottomNavigationView.setVisibility(View.INVISIBLE);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    setTitle(workout.getName());
                    backFromWorkout = true;
                    startWorkoutButton.setVisibility(View.VISIBLE);

                    // If the workout is selected then the workout button will have to be set up to
                    // Start that workout
                    startWorkoutButton.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            WorkoutButton(sets, 0);
                        }
                    });
                }
            });

            // Then format workout name
            workoutTextName[i] = new TextView(applicationContext);
            workoutTextName[i].setLayoutParams(nameParams);
            //workoutTextName[i].setPaddingRelative(10, 10, 10, 10);
            workoutTextName[i].setText(workoutFromJSON.getWorkoutName(i));
            workoutTextName[i].setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
            //workoutTextName[i].setGravity(Gravity.CENTER);
            workoutTextName[i].setTextColor(getResources().getColor(R.color.colorPrimary));
            //workoutTextName[i].setTypeface(null, Typeface.BOLD);
            workoutTextName[i].setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
            //workoutTextName[i].setY(i * 10);
            //workoutTextName[i].setClickable(false);
            workoutTextName[i].setDuplicateParentStateEnabled(true);

            // Format sets text
            workoutTextSets[i] = new TextView(applicationContext);
            workoutTextSets[i].setLayoutParams(setsParams);
            workoutTextSets[i].setText(workoutFromJSON.getSetsLength(i) + " sets");
            workoutTextSets[i].setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
            //workoutTextSets[i].setGravity(Gravity.CENTER);
            workoutTextSets[i].setTextColor(getResources().getColor(R.color.colorAccent));
            workoutTextSets[i].setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
            //workoutTextSets[i].setClickable(false);
            workoutTextSets[i].setDuplicateParentStateEnabled(true);

            // Add name/sets to container
            workoutContainer[i].addView(workoutTextName[i]);
            workoutContainer[i].addView(workoutTextSets[i]);
            // Add container to the main workout view
            workoutView.addView(workoutContainer[i]);
        }
    }

    // Function that is called when the workout button is clicked
    public boolean WorkoutButton(final Workouts.Workout.Set[] sets, final int index) {
        // Clear workout view
        workoutView.removeAllViews();

        // Create a text view for the set name/ rest name
        TextView name = new TextView(applicationContext);
        // Timer text view
        final TextView timerTextView;
        CountDownTimer timer = null;


        int setsIndex = index / 2;
        // Set
        if (index % 2 == 0) {
            // Set the text of the set name
            name.setText(sets[setsIndex].getName());
            name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
            workoutView.addView(name);
            // Add a text edit (numerical input only)
            // To track the number or reps
            EditText textInput = new EditText(applicationContext);
            //textInput.setInputType(InputType.);
            //textInput.setInputType(InputType.TYPE_CLASS_NUMBER);
            textInput.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
            //textInput.setTransformationMethod(null);
            workoutView.addView(textInput);

            // Change text of the workout button
            startWorkoutButton.setText("Finished set!");
        }
        // Rest
        else {
            name.setText("Rest " + sets[setsIndex].getRest() + " seconds");
            name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
            workoutView.addView(name);
            // Change text of the workout button
            startWorkoutButton.setText("Skip rest!");

            // Create rest timer and a textview to show it
            timerTextView = new TextView(applicationContext);
            timer = new CountDownTimer(sets[setsIndex].getRest() * 1000, 100) {

                public void onTick(long millisUntilFinished) {
                    timerTextView.setText("seconds remaining: " + millisUntilFinished / 1000);
                }

                public void onFinish() {
                    // When the timer runs out we switch to the next set (essentially click
                    // the button for the user automatically)
                    MediaPlayer mp = MediaPlayer.create(applicationContext, R.raw.alert2);
                    mp.start();
                    WorkoutButton(sets, index + 1);
                }
            }.start();
            workoutView.addView(timerTextView);
        }

        final CountDownTimer timer2 = timer;
        // Setup the workout button to be clicked again for the next stage of the workout
        startWorkoutButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                WorkoutButton(sets, index + 1);
                if(timer2 != null) {
                    timer2.cancel();
                }
            }
        });
        return true;
    }

    // This is the function that is called by the back button from the action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (backFromWorkout) {
            workoutView.removeAllViews();
            backFromWorkout = false;
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            LoadWorkouts();
            //bottomNavigationView.setVisibility(View.VISIBLE);
            startWorkoutButton.setVisibility(View.INVISIBLE);
            setTitle(R.string.title_workouts);
            return true;
        }
        return true;
    }
}
