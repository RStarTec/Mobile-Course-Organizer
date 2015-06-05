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

package com.rstar.mobile.csc205sp2015.developer;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.rstar.mobile.csc205sp2015.app.AppSettings;
import com.rstar.mobile.csc205sp2015.R;
import com.rstar.mobile.csc205sp2015.io.Savelog;

import java.io.File;


public class DeveloperDialogFragment extends DialogFragment {
	private static final String TAG = DeveloperDialogFragment.class.getSimpleName()+"_class";
	private static final boolean debug = AppSettings.defaultDebug;

	private static final String EXTRA_resume = DeveloperDialogFragment.class.getSimpleName()+".resume";

    public static final String dialogTag = DeveloperDialogFragment.class.getSimpleName()+"_tag";


    private static final int list_count = DeveloperOptions.BinaryOptions_count;
    private static final int radiogroup_count = DeveloperOptions.MultipleChoiceOptions_count;
    private static final int seekbar_count = DeveloperOptions.GradientOptions_count;
    private static final String tmpFilename = "DeveloperOptions.tmp";
    private DeveloperOptions mOptions;

    // The following are for memory management to avoid leaks
	private Button mOkButton = null;
	private OkOnClickListener mOkOnClickListener = null;

	private BinaryOptionsAdapter mBinaryOptionsAdapter;
	
	private RadioGroup mRadioGroup[] = new RadioGroup[radiogroup_count];
	private RadioButton mRadioButtons[][] = new RadioButton[radiogroup_count][];
	private RadioGroupOnCheckedChangeListener mRadioGroupListeners[] = new RadioGroupOnCheckedChangeListener[radiogroup_count];
    private SeekBar mSeekbars[] = new SeekBar[seekbar_count];

	public static DeveloperDialogFragment newInstance() {
		Bundle args = new Bundle();
		DeveloperDialogFragment fragment = new DeveloperDialogFragment();
        args.putBoolean(EXTRA_resume, false);
		fragment.setArguments(args);
		return fragment;
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        boolean resume = false;
        resume = getArguments().getBoolean(EXTRA_resume, false);

        // If we have saved a version before, then use the saved version.
        File tmpFile = DeveloperOptions.getFile(getActivity(), tmpFilename);
        if (resume && tmpFile!=null && tmpFile.exists())
            mOptions = new DeveloperOptions(getActivity(), tmpFilename);
        else
            mOptions = new DeveloperOptions(getActivity());
	}

	
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_developer, null);
		
		ListView listView = (ListView) v.findViewById(R.id.dialogDeveloper_list_id);
		mBinaryOptionsAdapter = new BinaryOptionsAdapter(this);
		listView.setAdapter(mBinaryOptionsAdapter);

        {   // Currently only one radio group.
            String labels[][] = DeveloperOptions.getMultipleChoiceOptionLabels();
            int values[] = mOptions.getMultipleChoiceOptions();

            int index = 0;
            int count = mOptions.getMultipleChoiceOptionsRanges()[index];

            mRadioGroup[index] = (RadioGroup) v.findViewById(R.id.dialogDeveloper_RadioGroup);
            mRadioGroupListeners[index] = new RadioGroupOnCheckedChangeListener(this, index);
            mRadioGroup[index].setOnCheckedChangeListener(mRadioGroupListeners[index]);
            mRadioButtons[index] = new RadioButton[count];

            for (int j=0; j<count; j++) {
                // Create radio buttons on the fly
                mRadioButtons[index][j] = new RadioButton(getActivity());
                mRadioButtons[index][j].setId(j); // use j as id
                mRadioButtons[index][j].setText(labels[index][j]);
                boolean value = values[index]==j;
                mRadioButtons[index][j].setChecked(value);
                mRadioGroup[index].addView(mRadioButtons[index][j], j);
                Savelog.d(TAG, debug, "Initialize button[" + index + "][" + j + "] value=" + value);
            }

        }

        {
            String labels[] = DeveloperOptions.getGradientOptionLabels();
            double ranges[][] = mOptions.getGradientOptionsRanges();
            double values[] = mOptions.getGradientOptions();

            TextView seekbarText[] = new TextView[seekbar_count];
            seekbarText[0] = (TextView) v.findViewById(R.id.dialogDeveloper_Seekbarlabel1);
            seekbarText[1] = (TextView) v.findViewById(R.id.dialogDeveloper_SeekbarLabel2);
            mSeekbars[0] = (SeekBar) v.findViewById(R.id.dialogDeveloper_Seekbar1);
            mSeekbars[1] = (SeekBar) v.findViewById(R.id.dialogDeveloper_Seekbar2);

            for (int index=0; index<seekbar_count; index++) {
                seekbarText[index].setText(labels[index]);
            }
            // Now set up seekbar

            // We set value of the seekbar to the latest available value on default file.
            // hook up seekbars with listeners
            for (int index=0; index<seekbar_count; index++) {
                double min = ranges[index][0];
                double max = ranges[index][1];
                double value = values[index];
                int progress = (int) (100*(value-min)/(max-min));
                mSeekbars[index].setProgress(progress);
                mSeekbars[index].setOnSeekBarChangeListener(new SeekBarOnChangedListener(this, index, min, max));
                Savelog.d(TAG, debug, "Initialize seekbar" + index + "(min,max,val)=(" + min +","+max+","+value+") showing:"+progress);
            }

        }
		mOkOnClickListener = new OkOnClickListener(this);
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(v)
			.setPositiveButton(R.string.button_OK, mOkOnClickListener)
			.setNegativeButton(R.string.button_cancel, null);

		
		Dialog dialog = builder.create();
		return dialog;
		
	} // end to onCreateDialog()
	
	
	@Override
	public void onStart() {
		super.onStart();
		AlertDialog d = (AlertDialog) getDialog();
		if (d!=null) {
			mOkButton = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (mOkButton!=null) {
			mOkButton.setOnClickListener(null);
			mOkButton = null;
		}
		if (mOkOnClickListener!=null) {
			mOkOnClickListener.cleanup();
			mOkOnClickListener = null;
		}

        for (int index=0; index< radiogroup_count; index++) {
            if (mRadioGroupListeners[index] != null) {
                mRadioGroupListeners[index] = null;
            }
        }
	}


	static private class OkOnClickListener implements DialogInterface.OnClickListener {
		DeveloperDialogFragment hostFragment;
		public OkOnClickListener(DeveloperDialogFragment hostFragment) {
			super();
			this.hostFragment = hostFragment;
		}
		public void cleanup() { hostFragment = null; }
		public void onClick(DialogInterface dialog, int which) {
            // Save to default file.
            hostFragment.mOptions.save(hostFragment.getActivity());
            // delete temp file
            File tmpFile = DeveloperOptions.getFile(hostFragment.getActivity(), tmpFilename);
            if (tmpFile!=null && tmpFile.exists()) tmpFile.delete();
            // Close activity.
            // Attention: In order for the developer's options to take effect, this fragment
            // must reside on the bottom activity of a stack. Once this activity is closed,
            // the entire app should be closed.
            hostFragment.getActivity().finish();
		}
	}
	

	private static class SwitchOnCheckedChangeListener implements OnCheckedChangeListener {
		DeveloperDialogFragment hostFragment;
		int index;
		public SwitchOnCheckedChangeListener(DeveloperDialogFragment hostFragment, int index) {
			super();
			this.hostFragment = hostFragment;
			this.index = index;
		}
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			Savelog.d(TAG, debug, "OnCheckedChanged for item " + index + " is now " + isChecked);
            hostFragment.mOptions.setBinaryOptions(index, isChecked);
            // save data to temp file
            hostFragment.mOptions.save(hostFragment.getActivity(), tmpFilename);
            hostFragment.getArguments().putBoolean(EXTRA_resume, true);
		}
	}

	
	private static class SeekBarOnChangedListener implements SeekBar.OnSeekBarChangeListener {
        DeveloperDialogFragment hostFragment;
        int index;
        double min;
        double max;
        public SeekBarOnChangedListener(DeveloperDialogFragment hostFragment, int index, double min, double max) {
            this.hostFragment = hostFragment;
            this.index = index;
            this.min = min;
            this.max = max;
        }
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            double newValue = (double)(progress)*0.01*(max-min)+min;
            hostFragment.mOptions.setGradientOptions(index, newValue);
            hostFragment.mOptions.save(hostFragment.getActivity(), tmpFilename);
            hostFragment.getArguments().putBoolean(EXTRA_resume, true);
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}
    }
	
	
	private static class BinaryOptionsAdapter extends ArrayAdapter<String> {
        String[] labels = DeveloperOptions.getBinaryOptionLabels();
        boolean[] values;
		DeveloperDialogFragment hostFragment;
		Context appContext;

		public BinaryOptionsAdapter(DeveloperDialogFragment hostFragment) {
			super(hostFragment.getActivity(), R.layout.list_item_developer_onoff, DeveloperOptions.getBinaryOptionLabels());
			this.hostFragment = hostFragment;
			appContext = hostFragment.getActivity().getApplicationContext();
            values = hostFragment.mOptions.getBinaryOptions();
		}
 		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			if (convertView==null) {
				LayoutInflater inflater = (LayoutInflater) appContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
				convertView = inflater.inflate(R.layout.list_item_developer_onoff, parent, false);
			}
			TextView nameView = (TextView) convertView.findViewById(R.id.listItem_developerOnOff_name_id);
            nameView.setText(labels[position]);
            
			Switch toggle = (Switch) convertView.findViewById(R.id.listItem_developerOnOff_toggle_id);

			// ATTENTION: 
			// The toggle might already have a listener if the view has been recycled, 
			// This pre-existing listener may cause the toggle to modify its own value.
			// It is very important to remove any old listener before 
			// setting the value of the toggle. 
			toggle.setOnCheckedChangeListener(null);
            
			toggle.setChecked(values[position]);
			
			SwitchOnCheckedChangeListener toggleOnCheckedChangeListener = new SwitchOnCheckedChangeListener(hostFragment, position);
			toggle.setOnCheckedChangeListener(toggleOnCheckedChangeListener);



			return convertView;
		}
	}
	
	
	
	private static class RadioGroupOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {
		DeveloperDialogFragment hostFragment;
        int index;
		public RadioGroupOnCheckedChangeListener(DeveloperDialogFragment hostFragment, int index) {
			super();
			this.hostFragment = hostFragment;
            this.index = index;
            Savelog.d(TAG, debug, "Initialize radiogroup" + index);
		}
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			Savelog.d(TAG, debug, "checked id=" + checkedId);
            hostFragment.mOptions.setMultipleChoiceOptions(index, checkedId);
            // save to temp file
            hostFragment.mOptions.save(hostFragment.getActivity(), tmpFilename);
            hostFragment.getArguments().putBoolean(EXTRA_resume, true);
        }
	}

	

}
