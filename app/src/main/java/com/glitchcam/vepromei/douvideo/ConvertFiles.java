package com.glitchcam.vepromei.douvideo;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.text.TextUtils;

import com.meicam.sdk.NvsAVFileInfo;
import com.meicam.sdk.NvsMediaFileConvertor;
import com.meicam.sdk.NvsStreamingContext;
import com.glitchcam.vepromei.douvideo.bean.RecordClip;
import com.glitchcam.vepromei.douvideo.bean.RecordClipsInfo;
import com.glitchcam.vepromei.utils.Logger;
import com.glitchcam.vepromei.utils.PathUtils;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by ms on 2018/9/7.
 * 将拍摄的视频片段转换为倒放片段
 * Convert captured video clips to reverse clips
 */

public class ConvertFiles implements NvsMediaFileConvertor.MeidaFileConvertorCallback {

    private static final String TAG = "ConvertFiles";
    private static final int MESSAGE_CONVERT_START = 1;
    private static final int MESSAGE_CONVERT_FINISH = 2;
    private static final int MESSAGE_CONVERT_CANCEL = 3;
    private static final int MESSAGE_CONVERT_CANCEL_TEMP = 4;
    private int mIndex = 0;
    private int mFinishCode;
    private long mCurrentTaskId;
    private String mDstPath;
    private String mDir;
    private RecordClipsInfo mClipsInfo;
    private ArrayList<RecordClip> mClipList;
    private ArrayList<RecordClip> mReverseClipList;
    private ConvertThread mConvertThread;
    private Handler mConvertHandler;
    private Handler mUIHandler;
    private NvsMediaFileConvertor mMediaFileConvertor;
    private OnCancelListener mOnCancelListener;

    @Override
    public void onProgress(long taskId, float progress) {
        Logger.d(TAG, "onProgress -> taskId = "+ taskId+ ", progress = "+progress );
    }

    @Override
    public void onFinish(long taskId, String srcFile, String dstFile, int errorCode) {
        Logger.d(TAG, "onFinish-> taskId = " +taskId + ", currentTaskId = "+mCurrentTaskId);
        if (mCurrentTaskId < 0) {
            mUIHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mOnCancelListener != null) {
                        mOnCancelListener.onCanceled();
                        mOnCancelListener = null;
                    }
                }
            });
            return;
        }
        if (mCurrentTaskId != taskId) {
            return;
        }
        clipConvertComplete(dstFile,
                errorCode == NvsMediaFileConvertor.CONVERTOR_ERROR_CODE_NO_ERROR);
    }

    @Override
    public void notifyAudioMuteRage(long l, long l1, long l2) {

    }

    private class ConvertThread extends HandlerThread {

        public ConvertThread(String name) {
            super(name);
        }

        public ConvertThread(String name, int priority) {
            super(name, priority);
        }

        @Override
        protected void onLooperPrepared() {
            super.onLooperPrepared();
        }
    }

    public ConvertFiles(RecordClipsInfo info, String dir, Handler handler, int finishCode) {
        mFinishCode = finishCode;
        mUIHandler = handler;
        mClipsInfo = info;
        mClipList = mClipsInfo.getClipList();
        mReverseClipList = mClipsInfo.getReverseClipList();
        mReverseClipList.clear();
        mDir = dir;
        File fileDir = new File(dir);
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
        mConvertThread = new ConvertThread("convert thread");
        mConvertThread.start();
        mConvertHandler = new Handler(mConvertThread.getLooper()) {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch (msg.what) {
                    case MESSAGE_CONVERT_START: {
                        if (!convertFile()) {
                            clipConvertComplete(null, false);
                        }
                        break;
                    }
                    case MESSAGE_CONVERT_FINISH: {
                        finishConvert();
                        mConvertThread.quit();
                        break;
                    }
                    case MESSAGE_CONVERT_CANCEL: {
                        cancelConvert();
                        mConvertThread.quit();
                        break;
                    }
                    case MESSAGE_CONVERT_CANCEL_TEMP: {
                        cancelConvert();
                        break;
                    }
                }
            }
        };
    }

    public void sendConvertFileMsg() {
        mConvertHandler.sendEmptyMessage(MESSAGE_CONVERT_START);
    }

    public void sendFinishConvertMsg() {
        mConvertHandler.sendEmptyMessage(MESSAGE_CONVERT_FINISH);
    }

    public void sendCancelConvertMsg() {
        /**
         * 这里通过反射解决异常
         * Handler sending message to a Handler on a dead thread
         */
        Field messageQueueField = null;
        try {
            messageQueueField = Looper.class.getDeclaredField("mQueue");
            messageQueueField.setAccessible(true);
            Class<MessageQueue> messageQueueClass = (Class<MessageQueue>) Class.forName("android.os.MessageQueue");
            Constructor<MessageQueue>[] messageQueueConstructor = (Constructor<MessageQueue>[]) messageQueueClass.getDeclaredConstructors();
            for (Constructor<MessageQueue> constructor : messageQueueConstructor) {
                constructor.setAccessible(true);
                Class[] types = constructor.getParameterTypes();
                for (Class clazz : types) {
                    if (clazz.getName().equalsIgnoreCase("boolean")) {
                        messageQueueField.set(mConvertHandler.getLooper(), constructor.newInstance(true));
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mConvertHandler.sendEmptyMessage(MESSAGE_CONVERT_CANCEL);
    }

    public void sendCancelConvertMsg(boolean needQuit) {
        if (!mConvertThread.isAlive()) {
            return;
        }
        /**
         * 这里通过反射解决异常
         * Handler sending message to a Handler on a dead thread
         */
        Field messageQueueField = null;
        try {
            messageQueueField = Looper.class.getDeclaredField("mQueue");
            messageQueueField.setAccessible(true);
            Class<MessageQueue> messageQueueClass = (Class<MessageQueue>) Class.forName("android.os.MessageQueue");
            Constructor<MessageQueue>[] messageQueueConstructor = (Constructor<MessageQueue>[]) messageQueueClass.getDeclaredConstructors();
            for (Constructor<MessageQueue> constructor : messageQueueConstructor) {
                constructor.setAccessible(true);
                Class[] types = constructor.getParameterTypes();
                for (Class clazz : types) {
                    if (clazz.getName().equalsIgnoreCase("boolean")) {
                        messageQueueField.set(mConvertHandler.getLooper(), constructor.newInstance(true));
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (needQuit) {
            mConvertHandler.sendEmptyMessage(MESSAGE_CONVERT_CANCEL);
        } else {
            mConvertHandler.sendEmptyMessage(MESSAGE_CONVERT_CANCEL_TEMP);
        }
    }

    private void finishConvert() {
        if (mMediaFileConvertor != null) {
            mMediaFileConvertor.release();
        }
        mMediaFileConvertor = null;
    }

    private void cancelConvert() {
        if (mMediaFileConvertor == null) {
            mUIHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mOnCancelListener != null) {
                        mOnCancelListener.onCanceled();
                        mOnCancelListener = null;
                    }
                }
            });
            return;
        }
        mMediaFileConvertor.cancelTask(mCurrentTaskId);
        mCurrentTaskId = -1;
        Logger.d(TAG, "cancelConvert" );
        mMediaFileConvertor = null;
        if (!TextUtils.isEmpty(mDstPath)) {
            File file = new File(mDstPath);
            if (file.exists()) {
                file.delete();
            }
        }
    }


    public void cancelConvert(OnCancelListener listener) {
        mOnCancelListener = listener;
        sendCancelConvertMsg(false);
    }

    public boolean convertFile() {
        Logger.d(TAG, "convertFile: index = "+mIndex );
        if (mIndex > mClipList.size() - 1 || mClipList.isEmpty()) {
            return false;
        }
        if (mMediaFileConvertor == null) {
            mMediaFileConvertor = new NvsMediaFileConvertor();
        }
        mMediaFileConvertor.setMeidaFileConvertorCallback(this, null);
        String mSrcPath = mClipList.get(mIndex).getFilePath();
        mDstPath = mDir + File.separator + PathUtils.getFileName(mSrcPath);
        Logger.e(TAG, "video convertStart =  " + mClipList.get(mIndex).getTrimIn() + "-->convertEnd" + mClipList.get(mIndex).getTrimOut());
        mCurrentTaskId = mMediaFileConvertor.convertMeidaFile(mSrcPath, mDstPath, true,
                mClipList.get(mIndex).getTrimIn(), mClipList.get(mIndex).getTrimOut(), null);
        return true;
    }

    public void clipConvertComplete(String newPath, boolean isSuccess) {
        RecordClip clip = mClipList.get(mIndex);
        clip.setIsConvertSuccess(isSuccess);
        RecordClip reverseClip = new RecordClip();
        boolean isCaptureVideo = clip.isCaptureVideo();
        reverseClip.setFilePath(isSuccess ? newPath : clip.getFilePath());
        reverseClip.setIsConvertSuccess(isSuccess);
        long duration = clip.getDuration();
        if (isSuccess) {
            /*
             * 转码成功,获取转码后视频时长
             * Successful transcoding, get video duration after transcoding
             * */
            NvsAVFileInfo info = NvsStreamingContext.getInstance().getAVFileInfo(newPath);
            if (info != null) {
                duration = info.getDuration();
            }
        }
        reverseClip.setDuration(duration);
        reverseClip.setCaptureVideo(isCaptureVideo);
        if (!isCaptureVideo && !isSuccess) {
            /*
             * 本地素材视频,如果转码失败，则设置裁剪点
             * Local material video, if the transcoding fails, set the crop point
             * */
            reverseClip.setTrimIn(clip.getTrimIn());
            reverseClip.setTrimOut(clip.getTrimOut());
        }
        reverseClip.setDurationBySpeed(clip.getDurationBySpeed());
        reverseClip.setSpeed(clip.getSpeed());
        reverseClip.setRotateAngle(clip.getRotateAngle());
        mReverseClipList.add(reverseClip);
        mIndex++;
        if (mIndex < mClipList.size()) {
            sendConvertFileMsg();
            Logger.e(TAG, "当前转码视频Index-->" + mIndex);
        } else {
            sendFinishConvertMsg();
            mUIHandler.sendEmptyMessage(mFinishCode);
        }
    }

    public void onConvertDestroy() {
        if (null != mConvertHandler) {
            mConvertHandler.removeCallbacksAndMessages(null);
        }
    }

    public interface OnCancelListener {
        void onCanceled();
    }
}
