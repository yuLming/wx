package com.example.demo.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * 视频消息
 * @author xzl
 *
 */
@XStreamAlias("xml")
public class VideoMessage extends BaseMessage {

    // 媒体ID
    private String MediaId;
    // 语音格式
    private String ThumbMediaId;

    public String getMediaId() {
        return MediaId;
    }

    public void setMediaId(String mediaId) {
        MediaId = mediaId;
    }

    public String getThumbMediaId() {
        return ThumbMediaId;
    }

    public void setThumbMediaId(String thumbMediaId) {
        ThumbMediaId = thumbMediaId;
    }

}