package com.glitchcam.vepromei.edit.clipEdit.util;

import android.content.Context;
import androidx.core.content.ContextCompat;


import com.glitchcam.vepromei.R;
import com.glitchcam.vepromei.edit.data.ChangeSpeedCurveInfo;
import com.glitchcam.vepromei.utils.asset.NvAsset;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class CurveSpeedUtil {
    private final static String TAG = "CurveSpeedUtil";

    /**
     * 获取曲线变速
     *
     * @param context
     * @return
     */
    public static List<ChangeSpeedCurveInfo> listSpeedFromJson(Context context) {
        List<ChangeSpeedCurveInfo> fileList = new ArrayList<>();
        try {
            InputStreamReader isr = new InputStreamReader(context.getAssets().open("curve_speed/speed.json"), "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
            br.close();
            isr.close();
            JSONObject data = new JSONObject(builder.toString());
            JSONArray array = data.getJSONArray("speed_fx");
            for (int i = 0; i < array.length(); i++) {
                JSONObject role = array.getJSONObject(i);
                ChangeSpeedCurveInfo audioFxListItem = new ChangeSpeedCurveInfo();
                audioFxListItem.mEffectType = NvAsset.ASSET_CHANGE_SPEED_CURVE;
                audioFxListItem.mName = role.getString("name");
                audioFxListItem.speed = role.getString("speedOriginal");
                audioFxListItem.index = role.getInt("rank");
                audioFxListItem.speedOriginal = role.getString("speedOriginal");
                audioFxListItem.imagePath = role.getString("image_path");
                int drawableId = context.getResources().getIdentifier(audioFxListItem.imagePath, "mipmap", context.getPackageName());
                audioFxListItem.image_drawable = ContextCompat.getDrawable(context, drawableId);
                fileList.add(audioFxListItem);
            }
            Collections.sort(fileList, new RecordFxIndexComparator()); // 排序
            //添加无选项
            ChangeSpeedCurveInfo audioFxListItem = new ChangeSpeedCurveInfo();
            audioFxListItem.mEffectType = NvAsset.ASSET_CHANGE_SPEED_CURVE;
            audioFxListItem.mName = context.getString(R.string.timeline_fx_none);
            audioFxListItem.imagePath = "icon_curve_none";
            audioFxListItem.speed = "";
            int drawableId = context.getResources().getIdentifier(audioFxListItem.imagePath, "mipmap", context.getPackageName());
            audioFxListItem.image_drawable = ContextCompat.getDrawable(context, drawableId);
            fileList.add(0,audioFxListItem);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return fileList;
    }



    /**
     * 根据rank排序
     * Sort by sound rank
     */
    public static class RecordFxIndexComparator implements Comparator<ChangeSpeedCurveInfo> {

        @Override
        public int compare(ChangeSpeedCurveInfo bean1, ChangeSpeedCurveInfo bean2) {
            return bean1.index - bean2.index;
        }
    }

}
