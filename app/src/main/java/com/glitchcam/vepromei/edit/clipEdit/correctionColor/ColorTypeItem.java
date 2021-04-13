package com.glitchcam.vepromei.edit.clipEdit.correctionColor;

/**
 * Created by Administrator on 2018/10/11 0011.
 */

public class ColorTypeItem {
    private String colorAtrubuteText;
    private String colorTypeName;
    private String fxName;
    private boolean selected;
    private int icon;
    private int selectedIcon;
    //默认值，进入页面时 的值，重置时恢复到这个值 ，进入页面时 如果有值，覆盖这个默认值 如果没有则为默认值
    //返回编辑主页也根据这个值来修改
    private float  defaultValue;
    //这个参数目前的适用范围是噪点调节，default代表噪点的程度，extra代表噪点的密度
    private float extra;
    public ColorTypeItem() {
        selected = false;
    }

    public String getColorAtrubuteText() {
        return colorAtrubuteText;
    }

    public void setColorAtrubuteText(String colorAtrubuteText) {
        this.colorAtrubuteText = colorAtrubuteText;
    }

    public String getColorTypeName() {
        return colorTypeName;
    }

    public void setColorTypeName(String colorTypeName) {
        this.colorTypeName = colorTypeName;
    }

    public String getFxName() {
        return fxName;
    }

    public void setFxName(String fxName) {
        this.fxName = fxName;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getIcon() {
        return icon;
    }

    public void setSelectedIcon(int selectedIcon) {
        this.selectedIcon = selectedIcon;
    }

    public int getSelectedIcon() {
        return selectedIcon;
    }

    public void setDefaultValue(float defaultValue) {
        this.defaultValue = defaultValue;
    }

    public float getDefaultValue() {
        return defaultValue;
    }

    public float getExtra() {
        return extra;
    }

    public void setExtra(float extra) {
        this.extra = extra;
    }
}
