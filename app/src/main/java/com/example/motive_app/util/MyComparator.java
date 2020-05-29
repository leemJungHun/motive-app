package com.example.motive_app.util;

import com.example.motive_app.network.vo.VideoListVO;

import java.util.Comparator;

public class MyComparator implements Comparator<VideoListVO> {
    @Override
    public int compare(VideoListVO o1, VideoListVO o2) {
        return o2.getIdx().compareTo(o1.getIdx());
    }
}