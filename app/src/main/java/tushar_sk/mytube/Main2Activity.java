package tushar_sk.mytube;


import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.List;

public class Main2Activity extends AppCompatActivity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private CharSequence mTitle = "";

    private String[] tabs = {"Search List", "Play list"};

    public static String query = "Ted";

    public static List<VideoData> searchResults;

    public static List<VideoData> playlistResults;

    public static ListView videosFound, playlistFound;

    public static CheckBox checkBox;

    public static Handler handler,handler1;

    public static PlaceholderFragment placeholderFragment = new PlaceholderFragment();

    public static PlayList playList = new PlayList();

    private String addPlaylistResponseCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });


        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }

        handleIntent(getIntent());

        Bundle bundle = new Bundle();
        bundle.putString("query", query);
        placeholderFragment.setArguments(bundle);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main2, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =  (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String q) {
                // perform query here
                query = q;
                Log.v("msg:", query);

                Intent i = new Intent(getApplicationContext(), Main2Activity.class);
                i.putExtra("query",query);
                startActivity(i);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.exit:
                finish();
                System.exit(0);
            case R.id.action_settings:
                startActivity(new Intent(Settings.ACTION_SETTINGS));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return PlaceholderFragment.newInstance(position + 1);
            } else {
                return PlayListFragment.newInstance(position + 1);
            }
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";

            }
            return null;
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = "Section 1";
                break;
            case 2:
                mTitle = "Section 2";
                break;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public void onActivityCreated (Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            Log.v("msg:", "On Activity Created");
        }

        @Override
        public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState){
            View rootView = inflater.inflate(R.layout.fragment_main2, container, false);

            handler = new Handler();
            videosFound = (ListView) rootView.findViewById(R.id.listView_search);

            videosFound.setOnItemClickListener(
                    new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> av, View v, int pos, long id) {

                            Intent intent = new Intent(getActivity(), Player.class);
                            intent.putExtra("video", searchResults.get(pos).getId());
                            startActivity(intent);
                        }

                    });


            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((Main2Activity) activity).onSectionAttached(1);
            Log.v("msg:", "On Attach Fragment");

        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            if (query!=null) {
                Log.v("msg:","View Created Fragment");
                Log.v("msg:", query);
                searchOnYoutube(query);
            }
        }

        //Create a Thread to initialize a YouTubeConnector instance and run its search method
        private void searchOnYoutube(final String keywords) {
            new Thread(){
                public void run(){
                    Connector yc = new Connector();
                    searchResults = yc.searchQuery(keywords);
                    handler.post(new Runnable() {
                        public void run() {
                            updateVideosFound();
                        }
                    });
                }
            }.start();
        }

        //Use ArrayAdapter and pass it to ListView to display search results
        //in the getView method, inflate the video_item.xml layout and update its view

         private void updateVideosFound() {

             class ViewHolder {
                 CheckBox checkBox;
             }

            ArrayAdapter<VideoData> adapter = new ArrayAdapter<VideoData>(getActivity().getApplicationContext(),
                    R.layout.video_search_item, searchResults) {

                ViewHolder holder = null;

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    if (convertView == null) {

                        convertView = getActivity().getLayoutInflater().inflate(R.layout.video_search_item, parent, false);

                        holder = new ViewHolder();
                        holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkbox_favorite);
                        convertView.setTag(holder);

                        final String id = searchResults.get(position).getId();

                        holder.checkBox.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                CheckBox c = (CheckBox) v;
                                if (c.isChecked()) {
                                    try {
                                        playList.insertPlaylistItem(id);
                                        Intent i = new Intent(getActivity().getApplicationContext(), Main2Activity.class);
                                        i.putExtra("query",query);
                                        startActivity(i);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                            }
                        });

                    }
                    else {
                        holder = (ViewHolder) convertView.getTag();
                    }

                    ImageView thumbnail = (ImageView) convertView.findViewById(R.id.video_thumbnail);
                    TextView title = (TextView) convertView.findViewById(R.id.video_title);
                    TextView views = (TextView) convertView.findViewById(R.id.video_review_no);
                    TextView pub_date = (TextView) convertView.findViewById(R.id.video_date);

                    final VideoData searchResult = searchResults.get(position);

                    Picasso.with(getContext()).load(searchResult.getUri()).into(thumbnail);
                    title.setText(searchResult.getTitle());
                    views.setText(searchResult.getViews().toString() + " views");
                    pub_date.setText(searchResult.getDate().toString().substring(0, 10));

                    return convertView;
                }

            };
            videosFound.setAdapter(adapter);
        }
    }

    public static class PlayListFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlayListFragment newInstance(int sectionNumber) {
            PlayListFragment fragment = new PlayListFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlayListFragment() {
        }

        @Override
        public void onActivityCreated (Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            Log.v("msg:", "On Activity Created");
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main3, container, false);
            handler1 = new Handler();
            playlistFound = (ListView) rootView.findViewById(R.id.listView_playList);

            playlistFound.setOnItemClickListener(
                    new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
                            Intent intent = new Intent(getActivity(), Player.class);
                            intent.putExtra("video", playlistResults.get(pos).getId());
                            startActivity(intent);
                        }
                    });
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((Main2Activity) activity).onSectionAttached(2);
            Log.v("msg:","On Attach Fragment");
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            Log.v("msg:","On View Created");
            searchOnYoutube();
        }

        //Create a Thread to initialize a YouTubeConnector instance and run its search method
        private void searchOnYoutube() {
            new Thread(){
                public void run(){
                    PlayList yc = new PlayList();
                    playlistResults = yc.search();
                    handler1.post(new Runnable() {
                        public void run() {
                            updateVideosFound();
                        }
                    });
                }
            }.start();
        }

        //Use ArrayAdapter and pass it to ListView to display search results
        //in the getView method, inflate the video_item.xml layout and update its view

        private void updateVideosFound() {
            ArrayAdapter<VideoData> adapter = new ArrayAdapter<VideoData>(getActivity().getApplicationContext(),
                    R.layout.video_playlist, playlistResults) {

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    if (convertView == null) {

                        convertView = getActivity().getLayoutInflater().inflate(R.layout.video_playlist, parent, false);
                    }

                    ImageView thumbnail = (ImageView) convertView.findViewById(R.id.video_thumbnail1);
                    TextView title = (TextView) convertView.findViewById(R.id.video_title1);
                    TextView views = (TextView) convertView.findViewById(R.id.video_review_no1);
                    TextView pub_date = (TextView) convertView.findViewById(R.id.video_date1);

                    VideoData searchResult = playlistResults.get(position);

                    Picasso.with(getContext()).load(searchResult.getUri()).into(thumbnail);
                    title.setText(searchResult.getTitle());
                    views.setText(searchResult.getViews().toString() + " views");
                    pub_date.setText(searchResult.getDate().toString().substring(0, 10));

                    return convertView;
                }
            };
            playlistFound.setAdapter(adapter);
        }


    }

}