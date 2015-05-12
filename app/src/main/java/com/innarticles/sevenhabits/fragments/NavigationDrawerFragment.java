package com.innarticles.sevenhabits.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.innarticles.learningmat.R;
import com.example.innarticles.learningmat.adapters.Information;
import com.example.innarticles.learningmat.adapters.NavAdapter;

import java.util.ArrayList;
import java.util.List;

// addd this "implements NavAdapter.ClickListener" to make it implement technique two on Recyclicks

public class NavigationDrawerFragment extends Fragment {
    public ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    public static final String PREF_FILE_NAME = "testpref";
    public static final String KEY_USER_LEARNED_DRAWER = "user_learned_drawer";
    private boolean mUserLearnedDrawer;
    private boolean mFromSavedInstanceState;
    private View containerView;
    private RecyclerView recyclerView;
    private NavAdapter adapter;
    public NavigationDrawerFragment() {

        // Required empty public constructor
    }

    public List<Information> getData(){
        List<Information> data = new ArrayList<>();
        String[] title = {"Home","About","Help"};
        String[] icons = {"2","2","3"};
        for (int i = 0; i< title.length; i++){
            Information current = new Information();
            current.iconId = icons[i];
            current.title = title[i];
            data.add(current);
        }
    return data;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserLearnedDrawer=   Boolean.valueOf(readFromPreferences(getActivity(), KEY_USER_LEARNED_DRAWER, "false")) ;

        if(savedInstanceState!=null){
            mFromSavedInstanceState = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout;
        layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        recyclerView = (RecyclerView)layout.findViewById(R.id.reListview);
        adapter = new NavAdapter(getActivity(), getData());
//        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchLister(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Toast.makeText(getActivity(), "Clicked from Activity " + position, Toast.LENGTH_SHORT).show();
//                Log.d("Innarticles", )
            }

            @Override
            public void onLongClick(View view, int position) {
                Toast.makeText(getActivity(), "Clicked from Activity " + position, Toast.LENGTH_SHORT).show();
            }
        }));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return  layout;
    }


    public void setup(int fragMentId , DrawerLayout drawer, final Toolbar toolbar) {
        containerView = getActivity().findViewById(fragMentId);
        mDrawerLayout = drawer;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                //make my activity draw the actionbar again

                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if(!mUserLearnedDrawer){
                    mUserLearnedDrawer=true;
                    saveToPreferences(getActivity(),KEY_USER_LEARNED_DRAWER,mUserLearnedDrawer + "");
                }
                //make my activity draw the actionbar again
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if(slideOffset<0.6){
                    toolbar.setAlpha(1 - slideOffset);
                }
            }
        };

        if(!mUserLearnedDrawer && !mFromSavedInstanceState){
            mDrawerLayout.openDrawer(containerView);
        }
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
    }

    public static void saveToPreferences(Context context, String preferenceName, String preferenceValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(preferenceName, preferenceValue);
        editor.apply();
    }
    public static String readFromPreferences(Context context, String preferenceName, String defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(preferenceName, defaultValue);
    }
//    @Override
//    public void itemClicked(View view, int position) {
//        startActivity(new Intent(getActivity(), DetectorActivity.class));
//        Toast.makeText(getActivity(),"Clicked from Activity " + position, Toast.LENGTH_SHORT).show();
//    }

    class RecyclerTouchLister implements RecyclerView.OnItemTouchListener{

        GestureDetector gestureDetector;
        ClickListener clickListener;

        public RecyclerTouchLister(Context context, final RecyclerView recyclerView, final ClickListener clickListener){

            this.clickListener = clickListener;
            gestureDetector  = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    Log.d("saves", "OnSingle " + e);

                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if(child!=null && clickListener!=null){
                        clickListener.onLongClick(child,recyclerView.getChildPosition(child));
                    }
                    Log.d("saves", "onLongPress " + e);
                    super.onLongPress(e);
                }
            });


        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if(child!=null && clickListener!=null && gestureDetector.onTouchEvent(e)){
                clickListener.onClick(child,rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
            Log.d("Jesus Saves", "OnTouchEvent " + e);
        }
    }

    public interface ClickListener {
        public void onClick(View view, int position);
        public void onLongClick(View view, int position);
    }
}


