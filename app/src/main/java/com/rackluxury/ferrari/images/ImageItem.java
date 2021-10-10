package com.rackluxury.ferrari.images;

import java.util.Comparator;

public class ImageItem {
    private final String mImageUrl;
    private final String mCreator;
    private final int mViews;
    private final int mLikes;
    private final int mComments;
    private final int mDownloads;
    public ImageItem(String imageUrl, String creator, int views, int likes, int comments, int downloads) {
        mImageUrl = imageUrl;
        mCreator = creator;
        mViews = views;
        mLikes = likes;
        mComments = comments;
        mDownloads = downloads;
    }
    public String getImageUrl() {
        return mImageUrl;
    }
    public String getCreator() {
        return mCreator;
    }
    public int getViewCount() {
        return mViews;
    }
    public int getLikeCount() {
        return mLikes;
    }
    public int getCommentCount() {
        return mComments;
    }
    public int getDownloadCount() {
        return mDownloads;
    }

    public static final Comparator<ImageItem> ByViews = new Comparator<ImageItem>() {
        @Override
        public int compare(ImageItem one, ImageItem two) {
            return -Integer.compare(one.mViews, two.mViews);
        }
    };
    public static final Comparator<ImageItem> ByLikes = new Comparator<ImageItem>() {
        @Override
        public int compare(ImageItem one, ImageItem two) {
            return -Integer.compare(one.mLikes, two.mLikes);
        }
    };
    public static final Comparator<ImageItem> ByComments = new Comparator<ImageItem>() {
        @Override
        public int compare(ImageItem one, ImageItem two) {
            return -Integer.compare(one.mComments, two.mComments);
        }
    };
    public static final Comparator<ImageItem> ByDownloads = new Comparator<ImageItem>() {
        @Override
        public int compare(ImageItem one, ImageItem two) {
            return -Integer.compare(one.mDownloads, two.mDownloads);
        }
    };
}