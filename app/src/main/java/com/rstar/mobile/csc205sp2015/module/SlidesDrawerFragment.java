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

package com.rstar.mobile.csc205sp2015.module;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.RecyclerListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.rstar.mobile.csc205sp2015.app.AppSettings;
import com.rstar.mobile.csc205sp2015.R;
import com.rstar.mobile.csc205sp2015.course.Course;
import com.rstar.mobile.csc205sp2015.io.Savelog;

import java.lang.ref.WeakReference;

public class SlidesDrawerFragment extends Fragment {
	private static final String TAG = SlidesDrawerFragment.class.getSimpleName()+"_class";
	private static final boolean debug = AppSettings.defaultDebug;
	
	private static final String EXTRA_ModuleNumber = SlidesDrawerFragment.class.getSimpleName()+".ModuleNumber";

	private static final int BitmapLoadAttempts =  3; // Allow more than 1 attempt. See if this can help avoid empty bitmap in case of OOM
    private static final String slideLabel = Module.SlideLabel + " ";

	private int mModuleNumber;
    private int mNumberOfPages;

    private TextView mTextView;
	private ListView mListView;
	private BitmapListAdapter mBitmapListAdapter = null;
	private Bitmap mPlaceHolderBitmap;

    private int bitmapRatio;
    private int bitmapWidth;
    private int bitmapHeight;
	
	// Supply the module number as an argument to the newly created fragment.
	public static SlidesDrawerFragment newInstance(int moduleNumber) {
		Bundle args = new Bundle();
		args.putInt(EXTRA_ModuleNumber, moduleNumber);

		SlidesDrawerFragment fragment = new SlidesDrawerFragment();
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		Savelog.d(TAG, debug, "onCreate() entered");

		mModuleNumber = getArguments().getInt(EXTRA_ModuleNumber, Module.DefaultModuleNumber);
        mNumberOfPages = Course.get(getActivity()).getModule(mModuleNumber).getNumberOfPages();


        int fragmentWidth = ((DrawerActivity)getActivity()).getRightDrawerWidth();
        if (fragmentWidth==0)
            fragmentWidth = getResources().getDimensionPixelSize(R.dimen.navigation_drawer_width);

        computeScaledBitmapSize(fragmentWidth);

        Bitmap.Config config = Bitmap.Config.ARGB_8888;

		mPlaceHolderBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, config);
        Savelog.d(TAG, debug, "placeholderBitmap size=" + bitmapWidth + "x" + bitmapHeight);

		setRetainInstance(true);

	} // end to implementing onCreate()
	
	
	
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v;
		Savelog.d(TAG, debug, "onCreateView() entered");
		
		v = inflater.inflate(R.layout.fragment_slides_drawer, parent, false);

        mTextView = (TextView) v.findViewById(R.id.fragmentSlidesDrawer_label);
        mTextView.setText(Module.ModuleLabel + " " + mModuleNumber);

		mListView = (ListView) v.findViewById(android.R.id.list);
        SlidesDrawerItemClickListener drawerItemClickListener = new SlidesDrawerItemClickListener((ModuleActivity)getActivity());
        mListView.setOnItemClickListener(drawerItemClickListener);

        onFetchingCompleted();
		return v;
	} // end to implementing onCreateView() 
	
	

	
	
	public void onFetchingCompleted() {
       
		mListView.setVisibility(View.VISIBLE);
		mListView.setAlpha(1f);
        
        Savelog.d(TAG, debug, "bitmap page length  = " + mNumberOfPages);
        if (mBitmapListAdapter==null) { // Don't reset adapter if already exists


            String keys[] = new String[mNumberOfPages];
            for (int index=0; index<mNumberOfPages; index++) {
                int pageNumber=index+1;
                keys[index] = Module.getSlideLabel(mModuleNumber, pageNumber);
            }
            mBitmapListAdapter = new BitmapListAdapter(this, keys);
        }
        mListView.setAdapter(mBitmapListAdapter);
        mListView.setRecyclerListener(new RecyclerListener() {
            @Override
            public void onMovedToScrapHeap(View view) {
                final ImageView imageView = (ImageView) view.findViewById(R.id.listItem_slidesDrawer_bitmap_id);
                if (imageView!=null) {
                    if (imageView.getTag()!=null) {
                        Savelog.d(TAG, debug, "Called recycler for tag " + ((ViewHolder)imageView.getTag()).key);
                    }
                    else {
                        Savelog.d(TAG, debug, "Called recycler for a tag-less imageview " + (String) (imageView.getTag()));
                    }
                    imageView.setImageBitmap(null);
                    imageView.setTag(null);
                }
            }
        });

	}

	


	
	public int getmoduleNumber() {
		return mModuleNumber;
	}

	
	private static class BitmapListAdapter extends BaseAdapter {
		SlidesDrawerFragment hostFragment;
		String[] renderedKey;

		
		public BitmapListAdapter(SlidesDrawerFragment hostFragment, String[] renderedKey) {
			super();
			this.hostFragment = hostFragment;
			this.renderedKey = renderedKey;
		}
		@Override
		public int getCount() {
			return renderedKey.length;
		}
		@Override
		public Object getItem(int position) {
			return renderedKey[position];
		}
		@Override
		public long getItemId(int position) {
			return position;
		}
		
		public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;

			if (convertView==null) {
				convertView = hostFragment.getActivity().getLayoutInflater()
				.inflate(R.layout.list_item_slides_drawer, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.listItem_slidesDrawer_bitmap_id);
                viewHolder.textView = (TextView) convertView.findViewById(R.id.listItem_slidesDrawer_pageNumber_id);
                convertView.setTag(viewHolder);
			}
            else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
			Savelog.d(TAG, debug, "png listview getView " + position + " of " + getCount());

            viewHolder.textView.setText(getSlideLabel(position));
			viewHolder.key = (String)getItem(position);

			Savelog.d(TAG, debug, "Setting tag " + viewHolder.key + " to image view.");
			

			loadBitmap(hostFragment, viewHolder.key, position, viewHolder.imageView);

			return convertView;
		}

        String getSlideLabel(int position) {
            return slideLabel + (position + 1);
        }
	}

    private static class ViewHolder {
        String key;
        ImageView imageView;
        TextView textView;
    }


    private static class SlidesDrawerItemClickListener implements ListView.OnItemClickListener {
        DrawerActivity hostActivity;
        public SlidesDrawerItemClickListener(DrawerActivity hostActivity) {
            super();
            this.hostActivity = hostActivity;
        }
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            int newPageNumber = position+1;
            hostActivity.onFinishSlideDrawerSelection(newPageNumber);
        }
    }





    public static void loadBitmap(SlidesDrawerFragment hostFragment, String key, int position, ImageView imageView) {
		if (cancelPotentialWork(key, imageView)) {
			Savelog.d(TAG, debug, "calling loadBitmap() for key="+key);
			final BitmapWorkerTask task = new BitmapWorkerTask(hostFragment, imageView);
			final AsyncDrawable asyncDrawable = new AsyncDrawable(hostFragment.getResources(), hostFragment.mPlaceHolderBitmap, task);

            Savelog.d(TAG, debug, "Ready to set imageDrawable to asyncDrawable");
			imageView.setImageDrawable(asyncDrawable);
			
			if (debug) {	// for debugging
				int imgWidth = imageView.getWidth();
				int imgHeight = imageView.getHeight();
				Savelog.d(TAG, debug, "initial imageView size="+imgWidth+"x"+imgHeight);
				int bmpWidth = hostFragment.mPlaceHolderBitmap.getWidth();
				int bmpHeight = hostFragment.mPlaceHolderBitmap.getHeight();
				Savelog.d(TAG, debug, "using bitmap size="+bmpWidth+"x"+bmpHeight);
			}

			task.execute(key, position);

            Savelog.d(TAG, debug, "just called task.execute() for background job");
		}
		
	}
	
	
	
	static class AsyncDrawable extends BitmapDrawable {
		private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

		public AsyncDrawable(Resources res, Bitmap placeHolderBitmap, BitmapWorkerTask bitmapWorkerTask) {
			super(res, placeHolderBitmap);
			bitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
		}
		public BitmapWorkerTask getBitmapWorkerTask() {
			return bitmapWorkerTaskReference.get();
		}
	}
	
	
	public static boolean cancelPotentialWork(String key, ImageView imageView) {
		final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
		if (bitmapWorkerTask != null) {
			final String bitmapKey = bitmapWorkerTask.key;
			if (bitmapKey!=key) { // ok to compare pointers
				// Cancel previous task
				Savelog.d(key, debug, "canceling previous work for key="+bitmapKey);
				bitmapWorkerTask.cancel(true);
			} else {
				// The same work is already in progress
				Savelog.d(key, debug, "previous work is for the same key="+bitmapKey);
				return false;
			}
		}

		// No task associated with the ImageView, or an existing task was cancelled
		return true;
	}
	
	private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
		if (imageView != null) {
			final Drawable drawable = imageView.getDrawable();
			if (drawable instanceof AsyncDrawable) {
				final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
				return asyncDrawable.getBitmapWorkerTask();
			}
		}
		return null;
	}
	
	
	
	private static class BitmapWorkerTask extends AsyncTask<Object, Void, Bitmap> {
		private WeakReference<ImageView> imageViewReference;
		private final WeakReference<SlidesDrawerFragment> hostFragmentReference;
		private Context appContext;
        private int moduleNumber;
        private int pageNumber;
        private int bitmapWidth;
        private int bitmapHeight;
        private int bitmapRatio;

		private String key = null;
		
		public BitmapWorkerTask(SlidesDrawerFragment hostFragment, ImageView imageView) {
			imageViewReference = new WeakReference<ImageView>(imageView);
			this.appContext = hostFragment.getActivity().getApplicationContext();
			this.hostFragmentReference = new WeakReference<SlidesDrawerFragment>(hostFragment);
            this.moduleNumber = hostFragment.mModuleNumber;
            this.bitmapWidth = hostFragment.bitmapWidth;
            this.bitmapHeight = hostFragment.bitmapHeight;
            this.bitmapRatio = hostFragment.bitmapRatio;
		}
		
		@Override
		protected Bitmap doInBackground(Object... params) {
			Bitmap bitmap = null;
			key = (String) params[0];
			int position = (Integer) params[1];
			
			for (int attempt=0; attempt<BitmapLoadAttempts && bitmap==null; attempt++) {
				Savelog.d(TAG, debug, "AsyncTask trying to load bitmap from cache. Attempt " + attempt);
				if (attempt>0) { Savelog.w(TAG, "Load bitmap for key="+ key + " attempt " + attempt); }
				
				pageNumber = position+1;

                Module module = Course.get(appContext).getModule(moduleNumber);
                String filename = module.getSlideFile(appContext, pageNumber).getAbsolutePath();

                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

                bitmapOptions.inSampleSize = bitmapRatio;
                Savelog.d(TAG, debug, "calling BitmapFactory.decodeFile");

                Bitmap bitmapOriginal = BitmapFactory.decodeFile(filename, bitmapOptions);
                bitmap = Bitmap.createScaledBitmap(bitmapOriginal, bitmapWidth, bitmapHeight, true);
                // TODO: need to call gc on bitmapOriginal???

			}
			
			// bitmap could be null if oom error or cache flushed out.
			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (isCancelled()) {
				bitmap = null;
			}
			if (imageViewReference != null && bitmap != null) {
				final ImageView imageView = imageViewReference.get();
				final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
				if (this == bitmapWorkerTask && imageView != null) {
                    Savelog.d(TAG, debug, "onPostExecute(): Preparing to set image bitmap");
					imageView.setImageBitmap(bitmap);
					
					
					if (debug) {	// for debugging
						int imgWidth = imageView.getWidth();
						int imgHeight = imageView.getHeight();
						Savelog.d(TAG, debug, "final imageView size="+imgWidth+"x"+imgHeight);
						int bmpWidth = bitmap.getWidth();
						int bmpHeight = bitmap.getHeight();
						Savelog.d(TAG, debug, "using bitmap size="+bmpWidth+"x"+bmpHeight);
					}

				}
				
			}
			else {
				Savelog.e(TAG, "Unable to load " + key);
				if (hostFragmentReference!=null) {
					SlidesDrawerFragment hostFragment = hostFragmentReference.get();
					Activity activity = hostFragment.getActivity();
					if (activity!=null) {
						if (bitmap==null) {
							Savelog.e(TAG, "bitmap is null. Possibly due to OOM error");
						}
						else {
							Savelog.d(TAG, debug, "bitmap is not null for key " + key);
						}
					}
				}
			}
		}
	}


    private void computeScaledBitmapSize(int maxWidth) {
        int defaultWidth = AppSettings.defaultSlideWidth;
        int defaultHeight = AppSettings.defaultSlideHeight;

        int width = defaultWidth;
        while (width>0 && width>maxWidth) {
            width = width / 2;
        }
        bitmapRatio = defaultWidth/width;
        bitmapWidth = width;
        bitmapHeight = defaultHeight/bitmapRatio;
    }


}
