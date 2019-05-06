package com.example.demo.model;


import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

/**
 *  文本消息
 */

@Data
@NoArgsConstructor
public class TextMessage extends WxMessage {

    private String content;// 文本消息内容

    //用来把基类的属性值复制给子类
    public static TextMessage adapt(WxMessage msg){
        TextMessage textMessage = new TextMessage();
        BeanUtils.copyProperties(msg, textMessage);
        return textMessage;
    }
}