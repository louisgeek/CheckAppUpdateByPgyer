package com.louisgeek.checkappupdatelib.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.louisgeek.checkappupdatelib.R;

/**
 * Created by louisgeek on 2016/6/7.
 */
public class MyDialogFragmentProgress4Down extends DialogFragment {

    private final static String TITLE_KEY = "TitleKey";

    private ProgressBar mProgressBar;
    private TextView id_my_message;
    private Button id_btn_hide_or_install;

    public static MyDialogFragmentProgress4Down newInstance(String title) {
        MyDialogFragmentProgress4Down myDialogFragment = new MyDialogFragmentProgress4Down();
        myDialogFragment.setCancelable(false);
        Bundle args = new Bundle();
        // 自定义的标题
        args.putString(TITLE_KEY, title);
        myDialogFragment.setArguments(args);
        return myDialogFragment;
    }

    /**
     * 在onCreateView中使用  getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);即可去掉
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialogfrag_content_progress_4_down, container, false);

        mProgressBar = (ProgressBar) view.findViewById(R.id.id_pb);
        mProgressBar.setMax(100);

        id_my_message = (TextView) view.findViewById(R.id.id_my_message);
        id_btn_hide_or_install = (Button) view.findViewById(R.id.id_btn_hide_or_install);
        id_btn_hide_or_install.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyDialogFragmentProgress4Down.this.dismiss();
            }
        });

        String title = getArguments().getString(TITLE_KEY);
        if (title == null) {
            this.getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        } else {
            this.getDialog().setTitle(title);
        }
        //return super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }

    public void updateProgress(int progress) {
        if (mProgressBar != null) {
            mProgressBar.setProgress(progress);
        }
    }


    public void setMessageText(String messageText) {
        if (id_my_message != null) {
            id_my_message.setText(messageText);
        }
    }

    public void finishDownload(final FinishDownloadListener finishDownloadListener) {
        id_btn_hide_or_install.setText("点击安装");
        id_btn_hide_or_install.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyDialogFragmentProgress4Down.this.dismiss();
                if (finishDownloadListener != null) {
                    finishDownloadListener.finishDown();
                }
            }
        });
    }

    public interface FinishDownloadListener {
        void finishDown();
    }

    FinishDownloadListener finishDownloadListener;

}
