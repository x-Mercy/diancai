package com.sziit.diancai.activity;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import com.sziit.diancai.R;

public class MenuActivity extends Activity {
    private Bundle bundle;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);


//        SpinnerAdapter adapter = ArrayAdapter.createFromResource(
//                getActionBarThemedContextCompat(), R.array.language,
//                android.R.layout.simple_spinner_dropdown_item);

        ActionBar actionBar = getActionBar();

//        actionBar.setDisplayShowTitleEnabled(false);

//        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

//        actionBar.setListNavigationCallbacks(adapter, new DropDownListenser());

        bundle = this.getIntent().getExtras();
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//    private Context getActionBarThemedContextCompat() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
//            return getActionBar().getThemedContext();
//        } else {
//            return this;
//        }
//    }


    class DropDownListenser implements OnNavigationListener {

        String[] listNames = getResources().getStringArray(R.array.language);


        @Override
        public boolean onNavigationItemSelected(int itemPosition, long itemId) {

            MenuFragment menuFragment = new MenuFragment();
            FragmentManager manager = getFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            menuFragment.setArguments(bundle);

            transaction.replace(R.id.container, menuFragment,
                    listNames[itemPosition]);
            transaction.commit();
            return true;
        }
    }
}