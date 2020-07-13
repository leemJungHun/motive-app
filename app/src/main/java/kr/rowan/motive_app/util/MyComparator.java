package kr.rowan.motive_app.util;

import java.util.Comparator;

import kr.rowan.motive_app.network.vo.VideoListVO;

public class MyComparator implements Comparator<VideoListVO> {
    @Override
    public int compare(VideoListVO o1, VideoListVO o2) {
        return o2.getIdx().compareTo(o1.getIdx());
    }
}