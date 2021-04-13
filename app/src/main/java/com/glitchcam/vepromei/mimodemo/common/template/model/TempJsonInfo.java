package com.glitchcam.vepromei.mimodemo.common.template.model;

import java.util.List;

public class TempJsonInfo {
    private List<JsonInfo> jsonList;
    public class JsonInfo {
        public String jsonPath;
    }

    public List<JsonInfo> getJsonList() {
        return jsonList;
    }

    public void setJsonList(List<JsonInfo> jsonList) {
        this.jsonList = jsonList;
    }
}
