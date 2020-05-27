package tech.berjis.lateral;

import android.os.Bundle;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class SchoolsActivity extends AppCompatActivity {

    ViewPager schoolsViewPger;
    TabLayout schoolTabs;
    SearchView searchSchool;

    HighSchoolFragment highSchoolFragment = new HighSchoolFragment(this);
    UniversitySchoolFragment universitySchoolFragment = new UniversitySchoolFragment(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schools);

        searchSchool = findViewById(R.id.searchSchool);
        schoolsViewPger = findViewById(R.id.schoolsViewPger);
        schoolTabs = findViewById(R.id.schoolTabs);

        initViewPager();
        initTabLayout();

        searchSchool.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                new PrimarySchoolFragment(SchoolsActivity.this, query);

                highSchoolFragment.searchSchools(query);
                universitySchoolFragment.searchSchools(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void initViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new PrimarySchoolFragment(this, searchSchool.getQuery().toString()), "Primary");
        adapter.addFrag(new HighSchoolFragment(this, searchSchool.getQuery().toString()), "High School");
        adapter.addFrag(new UniversitySchoolFragment(this, searchSchool.getQuery().toString()), "University");
        schoolsViewPger.setAdapter(adapter);
    }

    private void initTabLayout() {
        schoolTabs.setupWithViewPager(schoolsViewPger);
        schoolTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                schoolsViewPger.setCurrentItem(tab.getPosition());
                switch (tab.getPosition()) {
                    case 0:

                        break;
                    case 1:

                        break;
                    case 2:

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

    private static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
