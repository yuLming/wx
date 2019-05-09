package com.example.demo.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * 图片消息
 * @author xzl
 *
 */
@XStreamAlias("xml")
public class ImageMessage extends BaseMessage {

    // 图片链接
    private String PicUrl;
    //图片消息媒体id
    private String MediaId;


    public String getPicUrl() {
        return PicUrl;
    }


    public void setPicUrl(String picUrl) {
        PicUrl = picUrl;
    }



    public String getMediaId() {
        return MediaId;
    }



    public void setMediaId(String mediaId) {
        MediaId = mediaId;
    }
}