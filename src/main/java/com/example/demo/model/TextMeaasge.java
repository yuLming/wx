package com.example.demo.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * 文本实体类
 * @author xzl
 *
 */
@XStreamAlias("xml")
public class TextMeaasge extends BaseMessage {

    /**
     * 文本消息内容
     */
    private String Content;

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

}
