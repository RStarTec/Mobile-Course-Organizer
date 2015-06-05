/*
 * Copyright (c) 2015. Annie Hui @ RStar Technology Solutions
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rstar.mobile.csc205sp2015.search;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rstar.mobile.csc205sp2015.app.AppSettings;
import com.rstar.mobile.csc205sp2015.R;
import com.rstar.mobile.csc205sp2015.course.Course;
import com.rstar.mobile.csc205sp2015.io.Message;
import com.rstar.mobile.csc205sp2015.io.Savelog;
import com.rstar.mobile.csc205sp2015.module.Module;

import java.lang.ref.WeakReference;
import java.util.ArrayList;


/* The search fragment must be retained. Otherwise, the asynctask may be
 * called multiple times when the user rotates the device. 
 * 
 * All display settings are updated inside onStart().
 *
 * Assume that the module must be installed before it can be search.
 */
public class SearchFragment extends ListFragment {
	private static final String TAG = SearchFragment.class.getSimpleName()+"_class";
	private static final boolean debug = AppSettings.defaultDebug;
	
	private static final String EXTRA_ModuleNumber = SearchFragment.class.getSimpleName() + ".ModuleNumber";
    private static final String EXTRA_Keyword = SearchFragment.class.getSimpleName() + ".Keyword";

    public static final String EXTRA_newModuleNumber = SearchFragment.class.getSimpleName() + ".newModuleNumber";
    public static final String EXTRA_newPageNumber = SearchFragment.class.getSimpleName() + ".newPageNumber";

    private static final int allModules = Module.DefaultModuleNumber;
    private static final String label = Module.ModuleLabel + " ";

    private Search mSearch;
	private ArrayList<Search.Item> matchList;
	private SearchAdapter mSearchAdapter;
	private String mKeyword = "";
	private int mModuleNumber = allModules;

	private FrameLayout mListContainer;
	private ProgressBar mLoadingView;

	private MenuItem mSearchItem;
	private SearchView mSearchView;
    private SegmentQueryTextListener mQueryTextChangeListener = null;

    private String mModuleLabels[];
    private int mModuleChoice = 0;
	private MenuItem mModuleItem;
	private Spinner mModuleSpinner;

	private ArrayAdapter<String> mModuleAdapter = null;
	private FilterModuleListener mFilterModuleListener = null;
	
	private SearchLoaderAsyncTask mSearchLoaderAsyncTask;

	public static SearchFragment newInstance(int moduleNumber, String keyword) {
		Bundle args = new Bundle();

        if (moduleNumber>=0)
            args.putInt(EXTRA_ModuleNumber, moduleNumber);

		if (keyword==null) keyword = "";
		args.putString(EXTRA_Keyword, keyword);

		SearchFragment fragment = new SearchFragment();
		fragment.setArguments(args);
		return fragment;
	}

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Savelog.d(TAG, debug, "onCreate Search fragment.");

		mKeyword = getArguments().getString(EXTRA_Keyword);
        mModuleNumber = getArguments().getInt(EXTRA_ModuleNumber, allModules);
		
		// Make sure to retain the fragment so that search is
		// not restarted at every rotation
		setRetainInstance(true);


		mSearchLoaderAsyncTask = new SearchLoaderAsyncTask(this);
		mSearchLoaderAsyncTask.execute();


        mModuleLabels = new String[Course.get(getActivity()).getNumberOfModules()+1]; // add 1 to include allModules
        mModuleLabels[0] = "All"; // When this is selected, it means search everything
        mModuleChoice = 0;
        for (int index=0; index< mModuleLabels.length-1; index++) {
            int pos = index+1;
            int moduleNumber = Course.get(getActivity()).getModuleList().get(index).getModuleNumber();
            mModuleLabels[pos] = label + moduleNumber;
            if (mModuleNumber== moduleNumber) {
                mModuleChoice = pos;
            }
        }

		setHasOptionsMenu(true);

	}

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_search, parent, false);
		
		mListContainer = (FrameLayout) v.findViewById(R.id.fragmentSearch_listContainer_id);
		mLoadingView = (ProgressBar) v.findViewById(R.id.fragmentSearch_loading_id);
		
		if (mSearch==null) {
			// Data not yet ready
			// Use View.GONE so that it will not be rendered anymore.
			mListContainer.setVisibility(View.GONE);
		}
		else {
			// Data ready
			onLoadingCompleted();
		}
		
		return v;
	}
	
		
	public void onLoadingCompleted() {

		if (mSearchAdapter==null) { // adapter not set yet
			if (mSearch!=null && matchList!=null) {
				// Note that the adapter requires the search and matchlist to be available. 
				// So it can only be called after the search is loaded.
				mSearchAdapter = new SearchAdapter(this);
				setListAdapter(mSearchAdapter);
			}
		}
		mListContainer.setVisibility(View.VISIBLE);
		// Now dismiss the progressbar. 
		// Use View.GONE so that it will not be rendered anymore.
		mLoadingView.setVisibility(View.GONE);

        Savelog.d(TAG, debug, "Loading completed.");
	}

	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (mSearchView!=null) {
			mSearchView.setOnQueryTextListener(null);
			mSearchView = null;
		}
		if (mModuleSpinner !=null) {
			mModuleSpinner.setOnItemSelectedListener(null);
			mModuleSpinner = null;
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mSearchLoaderAsyncTask!=null) {
			mSearchLoaderAsyncTask.cancel(true);
			mSearchLoaderAsyncTask = null;
		}
		if (mQueryTextChangeListener!=null) {
			mQueryTextChangeListener = null;
		}
		if (mFilterModuleListener!=null) {
			mFilterModuleListener = null;
		}
		if (mModuleAdapter!=null) {
			mModuleAdapter = null;
		}
		if (mSearch!=null) mSearch.close();
	}



	
	
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		
		inflater.inflate(R.menu.search, menu);
		mSearchItem = menu.findItem(R.id.menu_search_keyword);
		// this item is always expanded
		mSearchView = (SearchView) mSearchItem.getActionView();
		mSearchView.setIconifiedByDefault(false);
		
		// Do not send any search.
		mSearchView.setSearchableInfo(null);
		
		mSearchView.setQuery(mKeyword, false);
		
		mQueryTextChangeListener = new SegmentQueryTextListener(this);
		mSearchView.setOnQueryTextListener(mQueryTextChangeListener);

	
		mModuleItem = menu.findItem(R.id.menu_search_module);
		mModuleSpinner = (Spinner) mModuleItem.getActionView();

        // Note: need to use a custom-made layout for the items in the spinner,
        // because we want to show the items in a specific color that harmonizes
        // with the ActionBar's style.
		mModuleAdapter = new ArrayAdapter<String>(getActivity(), R.layout.actionbar_spinner_item, mModuleLabels);
		mModuleSpinner.setAdapter(mModuleAdapter);
		mFilterModuleListener = new FilterModuleListener(this);
		mModuleSpinner.setOnItemSelectedListener(mFilterModuleListener);

		mModuleSpinner.setSelection(mModuleChoice);

	}
	

	
	private static class FilterModuleListener implements OnItemSelectedListener {
		SearchFragment hostFragment;
		
		public FilterModuleListener(SearchFragment hostFragment) {
			this.hostFragment = hostFragment;
		}
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long itemId) {
			Savelog.d(TAG, debug, "Selected option " + position);
			int oldPos = hostFragment.mModuleChoice;
			int newPos = position;

			if (oldPos!=newPos) {
				hostFragment.mModuleChoice = newPos;
                if (newPos>0) {
                    int index = newPos - 1;
                    hostFragment.mModuleNumber = Course.get(hostFragment.getActivity().getApplicationContext()).getModuleList().get(index).getModuleNumber();
                }
                else {
                    hostFragment.mModuleNumber = allModules;
                }

				// Check if search is available first
				if (hostFragment.mSearch!=null) {
					// redo the search using the same keyword but different option
					String keyword = hostFragment.mKeyword;
					if (keyword.length()>0) {
                        if (hostFragment.mModuleNumber==allModules)
                            hostFragment.matchList = hostFragment.mSearch.getSegmentMatching(keyword);
                        else
    						hostFragment.matchList = hostFragment.mSearch.getSegmentMatching(keyword, hostFragment.mModuleNumber);
					}
                    else {
                        hostFragment.matchList = new ArrayList<Search.Item>();
                    }

					hostFragment.mSearchAdapter.notifyDataSetChanged();
				}
			}
			
		}
		@Override
		public void onNothingSelected(AdapterView<?> parent) {
		}
	}

	private static class SearchLoaderAsyncTask extends AsyncTask<Object, Void, SearchLoaderResults> {
		private Context appContext;
		private int matchByType;

		private final WeakReference<SearchFragment> hostFragmentReference;
		public SearchLoaderAsyncTask(SearchFragment hostFragment) {
			super();
			this.appContext = hostFragment.getActivity().getApplicationContext();
			this.hostFragmentReference = new WeakReference<SearchFragment>(hostFragment);
			this.matchByType = hostFragment.mModuleNumber;
		}

		@Override
		protected SearchLoaderResults doInBackground(Object... arg0) {
			Search catalog=null;
			ArrayList<Search.Item> matchList = new ArrayList<Search.Item>();
			SearchLoaderResults results = null;
			
			try {
				catalog = new Search(appContext);
			} catch (Exception e) {
				Savelog.e(TAG, "Error loading search");
			}
			
			if (catalog!=null) {
				results = new SearchLoaderResults();
				// display nothing???
				results.search = catalog;
				results.matchList = matchList;
			}
			return results;
		}
		
		@Override
		protected void onPostExecute(SearchLoaderResults results) {
			super.onPostExecute(results);
			
			if (isCancelled()) {}
			
			if (hostFragmentReference != null) {
				final SearchFragment hostFragment = hostFragmentReference.get();
				if (hostFragment != null) {
					if (results!=null) {
						hostFragment.matchList = results.matchList;
						hostFragment.mSearch = results.search;
						
						// Loading is completed. Make the listview visible.
						hostFragment.onLoadingCompleted();
						hostFragment.mSearchLoaderAsyncTask = null;
					}
					else {
						Toast.makeText(hostFragment.getActivity(), Message.toastSearchEmptyResult, Toast.LENGTH_SHORT).show();
						hostFragment.mSearchLoaderAsyncTask = null;
						hostFragment.getActivity().finish();
					}
				}
			}
			Savelog.d(TAG, debug, "AsyncTask completed.");
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			Savelog.d(TAG, debug, "AsyncTask canceled.");
		}

	}
	
	private static class SearchLoaderResults {
		Search search;
		ArrayList<Search.Item> matchList;
	}
	
	
	private static class SegmentQueryTextListener implements OnQueryTextListener {
		SearchFragment hostFragment;
		public SegmentQueryTextListener(SearchFragment hostFragment) {
			super();
			this.hostFragment = hostFragment;
		}

		@Override
		public boolean onQueryTextChange(String newText) {
			
			// Must wait until the search is loaded before doing the filtering
			if (hostFragment.mSearch!=null) {
				if (newText==null) {
					hostFragment.mKeyword = "";
				}
				else {
					hostFragment.mKeyword = newText;
					if (newText.length()>0) {
                        if (hostFragment.mModuleNumber==allModules)
    						hostFragment.matchList = hostFragment.mSearch.getSegmentMatching(newText);
                        else
                            hostFragment.matchList = hostFragment.mSearch.getSegmentMatching(newText, hostFragment.mModuleNumber);
					}
					else {
						// If no keyword, then just show nothing
                        hostFragment.matchList = new ArrayList<Search.Item>();
					}
					hostFragment.mSearchAdapter.notifyDataSetChanged();
				}
			}
			return true; 
			// The listener handles the query. No need to send to default search activity
		}

		@Override
		public boolean onQueryTextSubmit(String query) {
			
			return true; 
			/* The listener handles the query. 
			 * No need to send to default search engine.
			 * Since we have set mSearchView.setSearchableInfo(null),
			 * we should handle the query ourselves
			 */
		}
		
	}
	
	
	
	
	public void onListItemClick(ListView l, View v, int position, long id) {
		Search.Item item = ((SearchAdapter) getListAdapter()).getItem(position);
		Savelog.d(TAG, debug, "chosen module number " + item.getModuleNumber() + " page number " + item.getPageNumber());

        Savelog.d(TAG, debug, "Jumping to " + item.getModuleNumber() + " page number " + item.getPageNumber());

        int newModuleNumber = item.getModuleNumber();
        int newPageNumber = item.getPageNumber();

        // Pass result back to the caller activity
        Intent returnIntent = new Intent();
        returnIntent.putExtra(EXTRA_newModuleNumber, newModuleNumber);
        returnIntent.putExtra(EXTRA_newPageNumber, newPageNumber);
        getActivity().setResult(Activity.RESULT_OK, returnIntent);

		getActivity().finish();
	}

	
	/* Since the ListFragment is retained across multiple activity cycles,
	 * the context provided to the adapter should be the application context
	 * instead of an activity's context.
	 */

	private static class SearchAdapter extends ArrayAdapter<Search.Item> {
		SearchFragment hostFragment;

		public SearchAdapter(SearchFragment hostFragment) {
			super(hostFragment.getActivity().getApplicationContext(), 
					android.R.layout.simple_list_item_1, hostFragment.matchList);
			this.hostFragment = hostFragment;
		}
		
		@Override
		public int getCount() {
			if (hostFragment.matchList==null) return 0;
			return hostFragment.matchList.size();
		}
		
		@Override
		public Search.Item getItem(int pos) {
			if (hostFragment.matchList==null) return null;
			return hostFragment.matchList.get(pos);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			Search.Item item = getItem(position);
			
			if (convertView==null) {
				convertView = hostFragment.getActivity().getLayoutInflater()
				.inflate(R.layout.list_item_search, parent, false);
			}
			// Add a tag to every item for identification purpose.
			convertView.setTag(Integer.valueOf(position));
			
			TextView moduleNumberView = (TextView) convertView.findViewById(R.id.listItem_search_moduleNumber_id);
			moduleNumberView.setText("" + item.getModuleNumber());

            TextView pageNumberView = (TextView) convertView.findViewById(R.id.listItem_search_pageNumber_id);
            pageNumberView.setText("" + item.getPageNumber());

			TextView segmentView = (TextView) convertView.findViewById(R.id.listItem_search_segment_id);
			segmentView.setText(item.getSegment());

			return convertView;
		}
		
	}


}
