package com.louisgeek.checkappupdatebyfirim;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by louisgeek on 2016/6/7.
 */
public class MyDialogFragmentProgress extends DialogFragment {

    private final static String TITLE_KEY = "TitleKey";

    private ProgressBar mProgressBar;
    private TextView id_my_message;

    public static MyDialogFragmentProgress newInstance(String title) {
        MyDialogFragmentProgress myDialogFragment = new MyDialogFragmentProgress();
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
        View view = inflater.inflate(R.layout.dialogfrag_content_progress, container, false);

        mProgressBar = (ProgressBar) view.findViewById(R.id.id_pb);
        mProgressBar.setMax(100);

        id_my_message = (TextView) view.findViewById(R.id.id_my_message);

        String title = getArguments().getString(TITLE_KEY);
        this.getDialog().setTitle(title);
       //### this.getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        //return super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }

    public void updateProgress(int progress) {
        mProgressBar.setProgress(progress);
        if (progress==100){
            id_my_message.setText("下载完成，点我安装");
            id_my_message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onFinishClickListener!=null){
                        onFinishClickListener.onFinishClick(view);
                    }
                }
            });
        }else {
            id_my_message.setText("当前完成:"+String.valueOf(progress)+"%");
        }

    }

    public interface OnFinishClickListener{
       void onFinishClick(View view);
    }

    public void setOnFinishClickListener(OnFinishClickListener onFinishClickListener) {
        this.onFinishClickListener = onFinishClickListener;
    }

    private  OnFinishClickListener onFinishClickListener;
}
