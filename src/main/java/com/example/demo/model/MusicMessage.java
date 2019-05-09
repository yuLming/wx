package com.example.demo.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * 音乐消息
 * @author xzl
 *
 */
@XStreamAlias("xml")
public class MusicMessage extends BaseMessage {

    // 音乐
    private Music Music;

    public Music getMusic() {
        return Music;
    }


    public void setMusic(Music music) {
        Music = music;
    }
}