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

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.rstar.mobile.csc205sp2015.app.AppSettings;
import com.rstar.mobile.csc205sp2015.R;
import com.rstar.mobile.csc205sp2015.io.IO;
import com.rstar.mobile.csc205sp2015.io.Savelog;

import java.io.IOException;

public class CopyrightDialogFragment extends DialogFragment {
    private static final String TAG = CopyrightDialogFragment.class.getSimpleName()+"_class";
    private static final boolean debug = AppSettings.defaultDebug;
    private static final int copyrightId = R.raw.copyright;
    
    public static final String dialogTag = CopyrightDialogFragment.class.getSimpleName()+"_tag";
    
    private CharSequence mCopyright = null;
    private TextView mTextView = null;
    private Button mOkButton = null;

    
    public static CopyrightDialogFragment newInstance() {
        Bundle args = new Bundle();
        CopyrightDialogFragment fragment = new CopyrightDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCopyright = Html.fromHtml(loadCopyright(getActivity()));
        setRetainInstance(true);
        Savelog.d(TAG, debug, "This dialog fragment is retained.");
    }

    
    /* This dialog has a title, a TextView and one button (OK).
     */
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_copyright, null);
        mTextView = (TextView) v.findViewById(R.id.dialogCopyright_content_id);
        mTextView.setText(mCopyright);
        /* Use the Builder class for convenient dialog construction.
         * The dialog builder just needs to handle OK.
         */
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v)
            .setPositiveButton(R.string.button_OK, null);
        
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
        /* As of Aug 2013, Dialog Fragment has a bug with its 
         * SetRetainedInstance() method. Therefore, the following
         * need to be done to retain the dialog fragment
         */
        if (getDialog()!=null && getRetainInstance()) {
            getDialog().setDismissMessage(null);
        }
        super.onDestroyView();
        if (mOkButton!=null) {
            mOkButton.setOnClickListener(null);
            mOkButton = null;
        }
    }

    
    private String loadCopyright(Context context) {
        try {
            return IO.getRawResourceAsString(context, copyrightId);
        } catch (IOException e) {
            Savelog.e(TAG, "Copyright message not available.");
            return "";
        }
    }
}
