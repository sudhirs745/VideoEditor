package com.glitchcam.vepromei.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.glitchcam.vepromei.R;
import com.zhouyou.view.seekbar.SignSeekBar;

public class DurationDialog extends Dialog {

    SignSeekBar mSeekBar;
    TextView tv_ok, tv_cancel;
    long initDuration;

    DurationDlgCallback onClickListener;

    public DurationDialog(Context context, long _initDuration, DurationDlgCallback _onClickListener){
        super(context);
        initDuration = _initDuration;
        onClickListener = _onClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_duration);

        initUIView();
        initEvents();
    }

    private void initUIView() {
        mSeekBar = (SignSeekBar) findViewById(R.id.sv_time_duration);
        mSeekBar.setProgress((float) initDuration);

        tv_ok = findViewById(R.id.tv_dur_dlg_ok);
        tv_cancel = findViewById(R.id.tv_dur_dlg_cancel);
    }

    private void initEvents() {
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onClickOk((long) mSeekBar.getProgress());
                dismiss();
            }
        });
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onCancel();
                dismiss();
            }
        });
    }

    public interface DurationDlgCallback {
        void onClickOk(long process);
        void onCancel();
    }
}
