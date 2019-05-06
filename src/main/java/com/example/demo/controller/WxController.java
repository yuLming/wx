package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.model.AesException;
import com.example.demo.model.TextMessage;
import com.example.demo.model.WxMessage;
import com.example.demo.util.HttpUtil;
import com.example.demo.util.MessageUtil;
import com.example.demo.util.WXPublicUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RequestMapping("/wx")
@RestController
public class WxController {

    @RequestMapping(value = "/verify_wx_token", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String receiveMessage(HttpServletRequest request){

        //1. 获取微信服务器发送的消息，转换成map对象
        Map<String, String> map = MessageUtil.parseXmlToMap(request);
        // 2. 获取详细的信息
        // 发送方帐号（open_id）
        String fromUserName = map.get("FromUserName");
        // 公众帐号
        String toUserName = map.get("ToUserName");
        // 消息类型
        String msgType = map.get("MsgType");
        // 消息内容
        String content = map.get("Content");
        // 消息id
        String msgId = map.get("MsgId");

        //3. 定义回复消息对象
        String respMsg = "";

        // 也可以用new，然后一个属性一个属性的set
        // 微信消息的基类
        //set属性值的时候，注意：ToUserName 和 FromUserName的值要反过来！是坑!是坑!是坑!
        WxMessage msg = WxMessage.builder().FromUserName(toUserName).ToUserName(fromUserName).MsgType(msgType).MsgId(Long.parseLong(msgId))
                .CreateTime(new Date().getTime()).build();

        if ("<![CDATA[text]]>".equals(msgType)){//文本消息
            //要回复的消息内容
            String resultContent = "";
            if ("python".equals(content)){
                resultContent = "人生苦短，我用python";
            }else if ("php".equals(content) || "PHP".equals(content)){
                resultContent = "PHP是世界上最好的语言";
            }else if ("java".equals(content) || "JAVA".equals(content)){
                resultContent = "JAVA太特么复杂了";
            }else if ("js".equals(content) || "javascript".equals(content)){
                resultContent = "老子是脚本！跟java没半毛钱关系！";
            }else {
                resultContent = "您的开发语言是："+content;
            }
            TextMessage textMessage = TextMessage.adapt(msg);
            textMessage.setContent(resultContent);
            respMsg = MessageUtil.parseMsgToXml(textMessage, TextMessage.class);
        }

        return respMsg;
    }

    @RequestMapping("/verify_wx_token")
    @ResponseBody
    public String verifyWXToken(HttpServletRequest request) throws AesException {
        String msgSignature = request.getParameter("signature");
        String msgTimestamp = request.getParameter("timestamp");
        String msgNonce = request.getParameter("nonce");
        String echostr = request.getParameter("echostr");
        if (WXPublicUtils.verifyUrl(msgSignature, msgTimestamp, msgNonce)) {
            return echostr;
        }
        return null;
    }

    @RequestMapping("/tqyb/{city}")
    @ResponseBody
    public Object tqyb(@PathVariable String city) throws Exception {
        String url ="http://op.juhe.cn/onebox/weather/query";//请求接口地址
        String key = "abf8df53efdc5866d33e409fa242becc";
        Map params = new HashMap();//请求参数
        params.put("cityname",city);//要查询的城市，如：温州、上海、北京
        params.put("key", key);//应用APPKEY(应用详细页查询)
        params.put("dtype","json");//返回数据的格式,xml或json，默认json
        String returnStr = HttpUtil.getStringDataByHttp(url, params, "UTF-8");
        JSONObject jsonObject = JSONObject.parseObject(returnStr);
        //String reason = jsonObject.getString("reason");
        JSONObject result = jsonObject.getJSONObject("result");
        JSONObject  data = result.getJSONObject("data");
        JSONObject realtime = data.getJSONObject("realtime");
        JSONObject weather = realtime.getJSONObject("weather");

        return weather;

    }

}
