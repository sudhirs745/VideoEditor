package com.glitchcam.vepromei.bean;

/**
 * @author LiFei
 * @version 1.0
 * @title
 * @description 该类主要功能描述
 * @company 美摄
 * @created 2020/12/1 14:40
 * @changeRecord [修改记录] <br/>
 */
public class AdjustRation {
    private int id;
    private String name;
    private int selectedIcon;
    private int unSelectedIcon;
    private boolean isSelectd = false;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSelectedIcon() {
        return selectedIcon;
    }

    public void setSelectedIcon(int selectedIcon) {
        this.selectedIcon = selectedIcon;
    }

    public int getUnSelectedIcon() {
        return unSelectedIcon;
    }

    public void setUnSelectedIcon(int unSelectedIcon) {
        this.unSelectedIcon = unSelectedIcon;
    }

    public boolean isSelectd() {
        return isSelectd;
    }

    public void setSelectd(boolean selectd) {
        isSelectd = selectd;
    }

    @Override
    public String toString() {
        return "AdjustRation{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", selectedIcon=" + selectedIcon +
                ", unSelectedIcon=" + unSelectedIcon +
                ", isSelectd=" + isSelectd +
                '}';
    }
}
