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
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
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

    // This is the exit button that is part of the action bar at the top
    private MenuItem exitButton;

    // Bool that keeps track of the back button (in action bar) and what it should come back to
    private boolean backFromWorkout = false;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    //mTextMessage.setText(R.string.title_home);
                    //setTitle(R.string.title_home);
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
        LoadWorkouts();
    }

    private void SwitchToHomeView() {
        //view.setText("Add your text here");
        setTitle(R.string.title_home);
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
        // Clear workout view
        workoutView.removeAllViews();

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
                            // Hide back button, show exit button, disable nav bar
                            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                            exitButton.setVisible(true);
                            bottomNavigationView.setOnNavigationItemSelectedListener(null);
                            WorkoutButton(sets, 0, new int[sets.length]);
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

            // Edit workout button text for when user clicks on a workout
            startWorkoutButton.setText("Start Workout!");
        }
    }

    // Function that is called when the workout button is clicked
    // The workout button being at the bottom of the workout during sets/rest
    public boolean WorkoutButton(final Workouts.Workout.Set[] sets, final int index, final int[] reps) {

        // Clear workout view
        workoutView.removeAllViews();

        // Create a text view for the set name/ rest name
        TextView name = new TextView(applicationContext);
        // Timer text view
        final TextView timerTextView;
        CountDownTimer timer = null;

        final int setsIndex = index / 2;

        // If the final set was completed then index will be equal to sets.Length * 2
        // When this happens we should display the final workout (completed) view
        //System.out.println("Sets length = " + sets.length + "    index = " + index);
        if (sets.length * 2 == index + 1) {
            EndOfWorkout(sets, reps);
        }

        // If not the final set then move on to the next set/rest
        // Set
        else if (index % 2 == 0) {
            // Set the text of the set name
            name.setText(sets[setsIndex].getName());
            name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
            workoutView.addView(name);
            // Add a text edit (numerical input only)
            // To track the number or reps
            final EditText textInput = new EditText(applicationContext);
            //textInput.setInputType(InputType.);
            //textInput.setInputType(InputType.TYPE_CLASS_NUMBER);
            textInput.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
            textInput.setHint("# reps");
            //textInput.setTransformationMethod(null);
            workoutView.addView(textInput);

            // Change text of the workout button
            startWorkoutButton.setText("Finished set!");

            // Setup the workout button to be clicked again for the next stage of the workout
            startWorkoutButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int repsThisSet = 0;
                    if (!TextUtils.isEmpty(textInput.getText().toString().trim())) {
                    //if (textInput.getText() != null) {
                        repsThisSet = Integer.parseInt(textInput.getText().toString());
                    }

                    reps[setsIndex] = repsThisSet;
                    WorkoutButton(sets, index + 1, reps);
                }
            });
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
                    WorkoutButton(sets, index + 1, reps);
                }
            }.start();
            workoutView.addView(timerTextView);

            // Try and get around creating this second timer
            final CountDownTimer timer2 = timer;
            // Setup the workout button to be clicked again for the next stage of the workout
            startWorkoutButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    WorkoutButton(sets, index + 1, reps);
                    if(timer2 != null) {
                        timer2.cancel();
                    }
                }
            });
        }
        return true;
    }

    // Call this when the final set has been completed
    public void EndOfWorkout(Workouts.Workout.Set[] sets, int[] reps) {
        // Clear workout view
        workoutView.removeAllViews();

        for (int i = 0; i < reps.length; i++) {
            // Create a text view for each number of reps
            TextView name = new TextView(applicationContext);
            name.setText(sets[i].getName() + " : " + reps[i]);
            workoutView.addView(name);
        }

        // Setup workout button to
        startWorkoutButton.setText("Finished!");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        startWorkoutButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ExitWorkout();
            }
        });
    }

    // Call this when done reviewing 'end of workout' or when exiting from the workout
    public void ExitWorkout() {
        // Hide workout button
        startWorkoutButton.setVisibility(View.INVISIBLE);
        // Hide exit button
        exitButton.setVisible(false);
        // Escape workout and return to home view
        SwitchToHomeView();
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        //bottomNavigationView.setSelectedItemId(0);
        bottomNavigationView.getMenu().getItem(0).setChecked(true);
    }

    // Add exit button to action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.exit_button, menu);


        // Get exitButton, we can only do that in this function
        exitButton = menu.findItem(R.id.exit_button);
        // Hide the exitButton for now
        exitButton.setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    // This is the function that is called by the back button from the action bar
    // Actually it calls all buttons from the action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        System.out.println("App bar item id : " + item.getItemId());
        if (item.getItemId() == R.id.exit_button) {
            ExitWorkout();
        }
        else if (backFromWorkout) {
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

    // Helper functions
    // Enable/disable nav bar
    // Switch setCheckable to setChecked for a result that unchecks all of nav buttons
    public void EnableNavBar(boolean state) {
        int size = bottomNavigationView.getMenu().size();
        for (int i = 0; i < size; i++) {
            bottomNavigationView.getMenu().getItem(i).setCheckable(state);
        }
    }
}
