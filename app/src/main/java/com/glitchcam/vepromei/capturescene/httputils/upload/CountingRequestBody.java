package com.glitchcam.vepromei.capturescene.httputils.upload;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * Created by CaoZhiChao on 2018/11/30 19:07
 */
public class CountingRequestBody extends RequestBody {
    /*
    * 请求体的代理
    * Request body proxy
    * */
    protected RequestBody delegate;
    /*
    * 进度监听
    * Progress monitoring
    * */
    private Listener mListener;

    public CountingRequestBody(RequestBody delegate, Listener listener) {
        this.delegate = delegate;
        mListener = listener;
    }

    protected final class CountingSink extends ForwardingSink {
        /*
        * 已经写入数据值
        * Data value has been written
        * */
        private long byteWritten;

        private CountingSink(Sink delegate) {
            super(delegate);
        }

        @Override
        public void write(@NonNull Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);
            byteWritten += byteCount;
            /*
            * 每次写入触发回调函数
            * Every write triggers a callback function
            * */
            mListener.onReqProgress(byteWritten, contentLength());//
        }
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return delegate.contentType();
    }

    @Override
    public long contentLength() {
        try {
            return delegate.contentLength();
//            return file.length();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public void writeTo(@NonNull BufferedSink sink) throws IOException {
        CountingSink countingSink = new CountingSink(sink);
        BufferedSink buffer = Okio.buffer(countingSink);
        delegate.writeTo(buffer);
        buffer.flush();
    }

    public interface Listener {
        void onReqProgress(long byteWritten, long contentLength);
    }
}
