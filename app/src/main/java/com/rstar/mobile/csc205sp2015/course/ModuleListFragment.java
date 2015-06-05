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

package com.rstar.mobile.csc205sp2015.course;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.rstar.mobile.csc205sp2015.app.AppSettings;
import com.rstar.mobile.csc205sp2015.R;
import com.rstar.mobile.csc205sp2015.io.Savelog;
import com.rstar.mobile.csc205sp2015.module.Module;


public class ModuleListFragment extends Fragment implements AbsListView.OnItemClickListener {
    private static final String TAG = ModuleListFragment.class.getSimpleName()+"_class";
    private static final boolean debug = AppSettings.defaultDebug;

    private Course mCourse;

    private AbsListView mListView;
    private ModuleListAdapter mAdapter;

    public static ModuleListFragment newInstance() {
        ModuleListFragment fragment = new ModuleListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Savelog.d(TAG, debug, "onCreate()");

        mCourse = Course.get(getActivity());
        mAdapter = new ModuleListAdapter(getActivity(), mCourse);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_module_list, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }


    public void refreshList() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Module module = mAdapter.getItem(position);

        // Open a module and go to the default page.
        ((ModuleListActivity)getActivity()).openModule(module, Module.DefaultPageNumber);
    }



    private static class ModuleListAdapter extends ArrayAdapter<Module> {
        Course course;
        Context appContext;

        public ModuleListAdapter(Context context, Course course) {
            super(context, android.R.layout.simple_list_item_1, course.getModuleList());
            this.course = course;
            this.appContext = context.getApplicationContext();
        }
        @Override
        public int getCount() {
            return course.getModuleList().size();
        }
        @Override
        public Module getItem(int position) {
            return course.getModuleList().get(position);
        }
        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Module module = getItem(position);

            if (convertView==null) {
                LayoutInflater inflater = (LayoutInflater) appContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                convertView = inflater.inflate(R.layout.list_item_module, parent, false);
            }

            TextView moduleNumberView = (TextView) convertView.findViewById(R.id.listItem_module_number_id);
            moduleNumberView.setText("Module" + module.getModuleNumber());

            TextView moduleNameView = (TextView) convertView.findViewById(R.id.listItem_module_name_id);
            moduleNameView.setText(module.getTitle());

            TextView modulePageCountView = (TextView) convertView.findViewById(R.id.listItem_module_numberOfPages_id);
            modulePageCountView.setText(module.getContentDescription(this.appContext));

            convertView.setTag(Integer.valueOf(position)); // use position as a tag
            return convertView;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Savelog.d(TAG, debug, "Exiting ModuleListFragment. App closing.");
    }
}
