package com.hassan.moviestreamclientapp;


import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hassan.moviestreamclientapp.Adapter.MovieShowAdapter;
import com.hassan.moviestreamclientapp.Adapter.SliderPagerAdapterNew;
import com.hassan.moviestreamclientapp.Model.GetVideoDetails;
import com.hassan.moviestreamclientapp.Model.MovieItemClickListenerNew;
import com.hassan.moviestreamclientapp.Model.SliderSide;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements MovieItemClickListenerNew {

    private MovieShowAdapter moviesShowAdapter;
    DatabaseReference mDatabaserefence ;
    private List<GetVideoDetails> uploads, uploadsListlatest,uploadsListpopular;
    private List<GetVideoDetails> actionmovies, sportmovies,comedymovies,romanticmovies,advanturemovies,
                                  scienceFiction;

    private ViewPager sliderpager;
    private List<SliderSide> uploadsslider ;

    private TabLayout indicator,tabActionMovies;
    private RecyclerView MoviesRV ,moviesRvWeek ,tab;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        getSupportActionBar(). setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        getSupportActionBar().setCustomView(R.layout.actionbar);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);



        progressDialog = new ProgressDialog(this);
        iniViews();
        addAllMovies();
        System.out.println("All Movies Added");
        iniWeekMovies();
        iniPopularMovies();
        getActionMovies();
        System.out.println("Movies tab updated again");
        iniSlider();
       // askPermission();



    }

    private void iniViews() {

        tabActionMovies = findViewById(R.id.tabActionMovies);
        sliderpager = findViewById(R.id.slider_pager) ;
        indicator = findViewById(R.id.indicator);
        MoviesRV = findViewById(R.id.Rv_movies);
        moviesRvWeek = findViewById(R.id.rv_movies_week);
        tab = findViewById(R.id.tabrecyler);

    }


//   public void add_all_movies extends AsyncTask<Void>{
//
//    }


    private void addAllMovies(){

        uploads = new ArrayList<>();
        uploadsListlatest = new ArrayList<>();
        uploadsListpopular = new ArrayList<>();
        actionmovies = new ArrayList<>();
        sportmovies = new ArrayList<>();
        uploadsslider = new ArrayList<>();
        advanturemovies = new ArrayList<>();
        comedymovies = new ArrayList<>();
        romanticmovies = new ArrayList<>();
        scienceFiction = new ArrayList<>();


        mDatabaserefence = FirebaseDatabase.getInstance().getReference("videos");
        progressDialog.setMessage("Loading... Please Wait !");
        progressDialog.show();


        mDatabaserefence.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    GetVideoDetails upload = postSnapshot.getValue(GetVideoDetails.class);
                    SliderSide slide = postSnapshot.getValue(SliderSide.class);

                    if(Objects.equals(upload.getVideo_type(), "latest movies")){
                        uploadsListlatest.add(upload);
                        System.out.println("Latest movies Uploaded");

                    }else if(Objects.equals(upload.getVideo_type(), "Best popular movies"))
                    {
                        uploadsListpopular.add(upload);
                        System.out.println("Popular movies Uploaded");
                    }
                    if(Objects.equals(upload.getVideos_category(), "Action")){
                        actionmovies.add(upload);
                        System.out.println("Action movies Uploaded");
                    }else if(Objects.equals(upload.getVideos_category(), "Sports")){
                        sportmovies.add(upload);
                        System.out.println("Sports movies Uploaded");
                    }if(Objects.equals(upload.getVideos_category(), "Adventure")){
                        advanturemovies.add(upload);
                        System.out.println("Adventure movies Uploaded");
                    } else if(Objects.equals(upload.getVideos_category(), "Comedy")){
                        comedymovies.add(upload);
                        System.out.println("Comedy movies Uploaded");
                    }
                    if(Objects.equals(upload.getVideos_category(), "Romantic")){
                        romanticmovies.add(upload);
                        System.out.println("Romantic movies Uploaded");
                    }
                    if(Objects.equals(upload.getVideos_category(), "ScienceFiction")){
                        scienceFiction.add(upload);
                        System.out.println("Sci-Fi movies Uploaded");
                    }

                    if(upload.getVideo_slide().equals("Slide movies")){
                        uploadsslider.add(slide);
                        System.out.println("Slider movies Uploaded");
                    }
                    uploads.add(upload);

                }
                iniViews();
                iniWeekMovies();
                iniPopularMovies();
                iniSlider();
                movieviewtab();
                progressDialog.dismiss();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                progressDialog.dismiss();
            }
        });

    }

    public void iniWeekMovies() {

        moviesShowAdapter = new MovieShowAdapter(this, uploadsListlatest,this);
        moviesRvWeek.setAdapter(moviesShowAdapter);
        moviesRvWeek.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false));
        moviesShowAdapter.notifyDataSetChanged();

    }
    public void iniPopularMovies() {
        moviesShowAdapter = new MovieShowAdapter(this, uploadsListpopular,this);
        //adding adapter to recyclerview
        MoviesRV.setAdapter(moviesShowAdapter);
        MoviesRV.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false));
        moviesShowAdapter.notifyDataSetChanged();


    }

    private void movieviewtab() {

        tabActionMovies.addTab(tabActionMovies.newTab().setText("Action"));
        tabActionMovies.addTab(tabActionMovies.newTab().setText("Adventure"));
        tabActionMovies.addTab(tabActionMovies.newTab().setText("Comedy"));
        tabActionMovies.addTab(tabActionMovies.newTab().setText("Romantic"));
        tabActionMovies.addTab(tabActionMovies.newTab().setText("Sports"));
        tabActionMovies.addTab(tabActionMovies.newTab().setText("Sci-Fi"));
        tabActionMovies.setTabGravity(TabLayout.GRAVITY_FILL);
        tabActionMovies.setTabTextColors(ColorStateList.valueOf(Color.WHITE));

        tabActionMovies.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                switch (tab.getPosition()) {
                    case 0:
                        getActionMovies();
                        System.out.println("Tab Selected");
                        break;
                    case 1:
                        getAdvantureMovies();
                        break;
                    case 2:
                        getComedyMovies();
                        break;
                    case 3:
                        getRomanticMovies();
                        break;
                    case 4:
                        getSportMovies();
                        break;
                    case 5:
                        getScienceFiction();
                        break;
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
 }

    private void iniSlider() {
        //uploadsslider = new ArrayList<>();
        SliderPagerAdapterNew adapterslider = new SliderPagerAdapterNew(this,uploadsslider);
        sliderpager.setAdapter(adapterslider);
        adapterslider.notifyDataSetChanged();
        // setup timer
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new SliderTimer(),4000,6000);
        indicator.setupWithViewPager(sliderpager,true);

    }

    @Override
    public void onMovieClick(GetVideoDetails movie, ImageView movieImageView) {

        Intent intent = new Intent(this, MovieDetailsActivity.class);
        // send movie information to deatilActivity
        intent.putExtra("title",movie.getVideo_name());
        intent.putExtra("imgURL",movie.getVideo_thumb());
        intent.putExtra("imgCover",movie.getVideo_thumb());
        intent.putExtra("movieDetails",movie.getVideo_description());
        intent.putExtra("movieUrl",movie.getVideo_url());
        intent.putExtra("movieCategory",movie.getVideos_category());

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,
                movieImageView,"sharedName");

        startActivity(intent,options.toBundle());
    }

    public class SliderTimer extends TimerTask {
        @Override
        public void run() {

            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (sliderpager.getCurrentItem()<uploadsslider.size()-1) {
                        sliderpager.setCurrentItem(sliderpager.getCurrentItem()+1);
                    }
                    else
                        sliderpager.setCurrentItem(0);
                }
            });


        }
    }

    private void getActionMovies(){
        MovieShowAdapter newAdapter = new MovieShowAdapter(this, actionmovies,this);
        //adding adapter to recyclerview
        tab.setAdapter(newAdapter);
        tab.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false));
        newAdapter.notifyDataSetChanged();
        System.out.println("Action Movies Updates");

    }

    private void getSportMovies(){

        moviesShowAdapter = new MovieShowAdapter(this, sportmovies,this);
        //adding adapter to recyclerview
        tab.setAdapter(moviesShowAdapter);
        tab.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false));
        moviesShowAdapter.notifyDataSetChanged();

    }

    private void getScienceFiction(){

        moviesShowAdapter = new MovieShowAdapter(this, scienceFiction,this);
        //adding adapter to recyclerview
        tab.setAdapter(moviesShowAdapter);
        tab.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false));
        moviesShowAdapter.notifyDataSetChanged();

    }

    private void getAdvantureMovies(){
        moviesShowAdapter = new MovieShowAdapter(this, advanturemovies,this);
        //adding adapter to recyclerview
        tab.setAdapter(moviesShowAdapter);
        tab.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false));
        moviesShowAdapter.notifyDataSetChanged();

    }

    private void getRomanticMovies(){
        moviesShowAdapter = new MovieShowAdapter(this, romanticmovies,this);
        //adding adapter to recyclerview
        tab.setAdapter(moviesShowAdapter);
        tab.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false));
        moviesShowAdapter.notifyDataSetChanged();

    }

    private void getComedyMovies(){
        moviesShowAdapter = new MovieShowAdapter(this, comedymovies,this);
        //adding adapter to recyclerview
        tab.setAdapter(moviesShowAdapter);
        tab.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false));
        moviesShowAdapter.notifyDataSetChanged();

    }

    public void askPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 2004);

        }
    }

}