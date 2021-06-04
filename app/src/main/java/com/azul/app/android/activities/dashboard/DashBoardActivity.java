package com.azul.app.android.activities.dashboard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.azul.app.android.R;
import com.azul.app.android.activities.home.BaseLoggedInActivity;
import com.azul.app.android.adapters.GridViewAdpater;
import com.azul.app.android.adapters.MyAdapter;
import com.azul.app.android.classes.Model;
import com.azul.app.android.globals.AzulApplication;
import com.azul.app.android.globals.Constants;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;


public class DashBoardActivity extends BaseLoggedInActivity {

    ViewPager viewPager;
    MyAdapter myadapter;
    ImageView locationFilterBurgerMenuBtn;
    List<Model> models;
    TabLayout circleIndicator;
    RelativeLayout idParentLayout;
    GridView accessMenu;
    String[] numberWord;
    int[] transportImage = {R.drawable.ic_icon_consulta_dashboard, R.drawable.ic_icon_solicitar_proucto_dashboard, R.drawable.ic_icon_qr_code_dashboard_3, R.drawable.ic_icon_link_de_pago_dashboard};
    TextView tvUserName;
    TextView tvUserProf;
    TextView locationTxt;
    boolean submenuVisibility;
    SharedPreferences sharedPreferences;
    String locationJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_dash_board);

        Intent intent = getIntent();

        if (intent.getStringExtra("LOC") != null) {
            locationJson = intent.getStringExtra("LOC");
        } else {
            locationJson = intent.getStringExtra("LOCATION_RESPONSE");
        }

        models = new ArrayList<>();
        models.add(new Model(getResources().getString(R.string.amount_number), getResources().getString(R.string.date), getResources().getString(R.string.time_frame1), getResources().getString(R.string.net_sale_amount), getResources().getString(R.string.net_discount_amount), getResources().getString(R.string.quantity_number)));
        models.add(new Model(getResources().getString(R.string.amount_number), getResources().getString(R.string.fifteen_day), getResources().getString(R.string.time_frame2), getResources().getString(R.string.net_sale_amount), getResources().getString(R.string.net_discount_amount), getResources().getString(R.string.quantity_number)));
        models.add(new Model(getResources().getString(R.string.amount_number), getResources().getString(R.string.month), getResources().getString(R.string.time_frame3), getResources().getString(R.string.net_sale_amount), getResources().getString(R.string.net_discount_amount), getResources().getString(R.string.quantity_number)));


        myadapter = new MyAdapter(models, this);
        viewPager = findViewById(R.id.viewPager);
        circleIndicator = findViewById(R.id.circleIndicator);

        viewPager.setAdapter(myadapter);
        circleIndicator.setupWithViewPager(viewPager, true);

        numberWord = new String[]{getResources().getString(R.string.consultant), getResources().getString(R.string.order_product), getResources().getString(R.string.qr_code), getResources().getString(R.string.linkPage)};

        accessMenu = findViewById(R.id.accessMenu);
        GridViewAdpater gridViewAdapter = new GridViewAdpater(DashBoardActivity.this, numberWord, transportImage);
        accessMenu.setAdapter(gridViewAdapter);
        accessMenuFunction();
        initControls();
        onClickListners();
        burgerMenu();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListner);
    }

    private void initControls() {
        idParentLayout = findViewById(R.id.idParentLayout);
        tvUserName = findViewById(R.id.user_name);
        tvUserProf = findViewById(R.id.user_prof);
        locationTxt = findViewById(R.id.locationTxt);
        sharedPreferences = ((AzulApplication) this.getApplication()).getPrefs();

        String firstName = sharedPreferences.getString(Constants.FIRST_NAME, "");
        String lastName = sharedPreferences.getString(Constants.LAST_NAME, "");
        String groupName = sharedPreferences.getString(Constants.GROUP_NAME, "");
        String lastLocationNamee = sharedPreferences.getString(Constants.SELECTED_LOCATION_DASHBOARD, "");


        String fullName = firstName + " " + lastName;
        if (!TextUtils.isEmpty(fullName)) {
            tvUserName.setText(fullName);
        }
        if (!TextUtils.isEmpty(groupName)) {
            tvUserProf.setText(groupName);
        }

        if (!TextUtils.isEmpty(lastLocationNamee)) {
            locationTxt.setText(lastLocationNamee);
        }

    }

    private void onClickListners() {
        viewPager.setPageTransformer(false, (page, position) -> {
            if (viewPager.getCurrentItem() == 0) {
                page.setTranslationX(-70);
            } else if (viewPager.getCurrentItem() == myadapter.getCount() - 1) {
                page.setTranslationX(70);
            } else {
                page.setTranslationX(0);
            }
        });

    }

    LocationFilterBottomSheet bottomsheet;

    private void burgerMenu() {
        locationFilterBurgerMenuBtn = findViewById(R.id.locationFilterBurgerMenuBtn);

        locationFilterBurgerMenuBtn.setOnClickListener(btnBurgerMenuView -> {
            locationFilterBurgerMenuBtn.setRotation(submenuVisibility ? 180 : 0);
            bottomsheet = new LocationFilterBottomSheet(locationJson, "Dashboard");
            bottomsheet.show(getSupportFragmentManager(), "bottomSheet");
        });
    }


    public void setContent(String content, int i) {
        locationTxt.setText(content);
        if (i == 1) {
            bottomsheet.dismiss();
        }
    }


    private void accessMenuFunction() {
        viewPager.setOnTouchListener((view, motionEvent) -> {
            fragmentStatusCheck();
            return false;
        });
        circleIndicator.setOnTouchListener((view, motionEvent) -> {
            fragmentStatusCheck();
            return false;
        });


        accessMenu.setOnItemClickListener((parent, view, position, id) -> {
            switch (position) {
                case 0:
                    GenerateStatementFragment myFragment = (GenerateStatementFragment) getSupportFragmentManager().findFragmentByTag(Constants.GENERATE_STATUS);
                    if (myFragment != null && myFragment.isVisible()) {
                        getSupportFragmentManager().beginTransaction().
                                remove(getSupportFragmentManager().findFragmentById(R.id.fragmentContainer)).commit();
                    } else {
                        Consultant consultant = new Consultant(locationJson);
                        consultant.show(getSupportFragmentManager(), "bottomSheet");
                    }

                    break;
                case 1:
                    Log.d("FRAGMENT 1", "");
                    break;
                case 2:
                    GenerateStatementFragment myFragment2 = (GenerateStatementFragment) getSupportFragmentManager().findFragmentByTag(Constants.GENERATE_STATUS);
                    if (myFragment2 != null && myFragment2.isVisible()) {
                        getSupportFragmentManager().beginTransaction().
                                remove(getSupportFragmentManager().findFragmentById(R.id.fragmentContainer)).commit();
                    } else {
                        startActivitywithIntent(new Intent(DashBoardActivity.this, QrCode.class).putExtra("LOCATION_RESPONSE", locationJson));
                    }
                    break;
                case 3:
                    Log.d("FRAGMENT 3", "");
                    break;
                default:
                    break;

            }
        });

    }

    public void fragmentStatusCheck() {
        GenerateStatementFragment myFragment = (GenerateStatementFragment) getSupportFragmentManager().findFragmentByTag(Constants.GENERATE_STATUS);
        if (myFragment != null && myFragment.isVisible()) {
            getSupportFragmentManager().beginTransaction().
                    remove(getSupportFragmentManager().findFragmentById(R.id.fragmentContainer)).commit();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        fragmentStatusCheck();
        return super.onTouchEvent(event);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListner = item -> {
        Fragment selectedFragment = null;

        switch (item.getItemId()) {

            case R.id.add:
                break;
            case R.id.createStatement:
                GenerateStatementFragment myFragment = (GenerateStatementFragment) getSupportFragmentManager().findFragmentByTag(Constants.GENERATE_STATUS);
                if (myFragment != null && myFragment.isVisible()) {
                    getSupportFragmentManager().beginTransaction().
                            remove(getSupportFragmentManager().findFragmentById(R.id.fragmentContainer)).commit();
                } else {
                    selectedFragment = new GenerateStatementFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, selectedFragment, Constants.GENERATE_STATUS).commit();
                }
                break;
            default:
        }
        return true;
    };
}