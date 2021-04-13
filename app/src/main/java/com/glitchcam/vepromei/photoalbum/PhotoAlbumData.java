package com.glitchcam.vepromei.photoalbum;

public class PhotoAlbumData {
    public String photosAlbumName;
    public String photosAlbumTips;
    public String photosAlbumReplaceMax;
    public String photosAlbumReplaceMin;
    public String captionReplaceMax;

    public String getPhotosAlbumName() {
        return photosAlbumName;
    }

    public void setPhotosAlbumName(String photosAlbumName) {
        this.photosAlbumName = photosAlbumName;
    }

    public String getPhotosAlbumTips() {
        return photosAlbumTips;
    }

    public String getPhotosAlbumReplaceMin() {
        return photosAlbumReplaceMin;
    }

    public void setPhotosAlbumReplaceMin(String photosAlbumReplaceMin) {
        this.photosAlbumReplaceMin = photosAlbumReplaceMin;
    }

    public void setPhotosAlbumTips(String photosAlbumTips) {
        this.photosAlbumTips = photosAlbumTips;
    }

    public String getPhotosAlbumReplaceMax() {
        return photosAlbumReplaceMax;
    }

    public void setPhotosAlbumReplaceMax(String photosAlbumReplaceMax) {
        this.photosAlbumReplaceMax = photosAlbumReplaceMax;
    }

    public String getCaptionReplaceMax() {
        return captionReplaceMax;
    }

    public void setCaptionReplaceMax(String captionReplaceMax) {
        this.captionReplaceMax = captionReplaceMax;
    }

    public int id;
    public String coverImageUrl;
    public String coverVideoUrl;
    public String packageUrl;
    public boolean isLocal;
    public String filePath;
    public String licPath;
    public String sourceDir;

    public boolean isExist() {
//        if (filePath != null && !filePath.isEmpty( )
//                && licPath != null && !licPath.isEmpty( )
//                && coverImageUrl != null && !coverImageUrl.isEmpty( )
//                && coverVideoUrl != null && !coverVideoUrl.isEmpty( )) {

        if (filePath != null && !filePath.isEmpty( )) {
            return true;
        }
        return false;
    }
}
