package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.model.*;
import com.example.demo.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@RequestMapping("/wx")
@RestController
public class WxController {
    private static final Logger log = LoggerFactory.getLogger(WxController.class);

    @RequestMapping(value = "/verify", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String receiveMessage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean isGet = request.getMethod().toLowerCase().equals("get");

        if (isGet) {
            String msgSignature = request.getParameter("signature");
            String msgTimestamp = request.getParameter("timestamp");
            String msgNonce = request.getParameter("nonce");
            if (WXPublicUtils.verifyUrl(msgSignature, msgTimestamp, msgNonce)) {
                String echostr = request.getParameter("echostr");
                //response.getWriter().write(echostr);// 将回应发送给微信服务器
                return echostr;
            }
        } else {
            return postMsg(request);
        }
        return "";
    }

    @RequestMapping(value = "/verify_wx_token", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String verifyWXToken(HttpServletRequest request, HttpServletResponse response) throws Exception {

        boolean isGet = request.getMethod().toLowerCase().equals("get");

        if (isGet) {
            String msgSignature = request.getParameter("signature");
            String msgTimestamp = request.getParameter("timestamp");
            String msgNonce = request.getParameter("nonce");
            String echostr = request.getParameter("echostr");
            if (WXPublicUtils.verifyUrl(msgSignature, msgTimestamp, msgNonce)) {
                return echostr;
            }
        } else {
            // 接收消息并返回消息
            return postMsg(request);
        }
        return "";
    }

    @RequestMapping("/tqyb/{city}")
    @ResponseBody
    public Object tqyb(@PathVariable String city) throws Exception {
        String url = "http://op.juhe.cn/onebox/weather/query";//请求接口地址
        String key = "abf8df53efdc5866d33e409fa242becc";
        Map params = new HashMap();//请求参数
        params.put("cityname", city);//要查询的城市，如：温州、上海、北京
        params.put("key", key);//应用APPKEY(应用详细页查询)
        params.put("dtype", "json");//返回数据的格式,xml或json，默认json
        String returnStr = HttpUtil.getStringDataByHttp(url, params, "UTF-8");
        JSONObject jsonObject = JSONObject.parseObject(returnStr);
        //String reason = jsonObject.getString("reason");
        JSONObject result = jsonObject.getJSONObject("result");
        JSONObject data = result.getJSONObject("data");
        JSONObject realtime = data.getJSONObject("realtime");
        JSONObject weather = realtime.getJSONObject("weather");

        return weather;

    }


    public String postMsg(HttpServletRequest request) {
        String respMessage = "";
        try {
            Map<String, String> map = MessageUtil.xmlToMap(request);
            String toUserName = map.get("ToUserName");
            String fromUserName = map.get("FromUserName");
            String msgType = map.get("MsgType");
            String content = map.get("Content");
            log.info("接收消息：\n" + map.toString());
            TextMeaasge text = new TextMeaasge();
            // 发送和回复是反向的
            text.setToUserName(fromUserName);
            text.setFromUserName(toUserName);
            text.setCreateTime(new Date().getTime());
            text.setMsgType(MessageUtil.REQ_MESSAGE_TYPE_TEXT);
            //text.setFuncFlag(0);

            //WeiXinUserInfo weiXinUserInfo = WeixinUtil.getUserInfo(WeixinUtil.getAccessToken().getAccessToken(),fromUserName);

            respMessage = "";

            // 文本消息
            if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_TEXT)) {

                // 创建图文消息
                NewsMessage newsMessage = new NewsMessage();
                newsMessage.setToUserName(fromUserName);
                newsMessage.setFromUserName(toUserName);
                newsMessage.setCreateTime(new Date().getTime());
                newsMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_NEWS);

                List<Article> articleList = new ArrayList<>();

                // 指定消息回复
                if ("1".equals(content)) {
                    text.setContent("今天的天气真不错！");
                    respMessage = MessageUtil.messageToXml(text);
                }
                //单图文消息
                else if ("2".equals(content)) {
                    Article article = new Article();
                    article.setTitle("微信公众帐号开发教程Java版");
                    article.setDescription("第一张图片");
                    article.setPicUrl("http://pic.qiantucdn.com/58pic/26/10/40/58c04e46c2ffa_1024.jpg!/fw/780/watermark/url/L3dhdGVybWFyay12MS4zLnBuZw==/align/center");
                    article.setUrl("http://www.cnblogs.com/x-99/");
                    articleList.add(article);
                    // 设置图文消息个数
                    newsMessage.setArticleCount(articleList.size());
                    // 设置图文消息包含的图文集合
                    newsMessage.setArticles(articleList);
                    // 将图文消息对象转换成xml字符串
                    respMessage = MessageUtil.messageToXml(newsMessage);
                }
                //多图文消息
                else if ("3".equals(content)) {
                    Article article1 = new Article();
                    article1.setTitle("微信公众帐号开发教程Java版");
                    article1.setDescription("");
                    article1.setPicUrl("http://pic.qiantucdn.com/58pic/26/10/40/58c04e46c2ffa_1024.jpg!/fw/780/watermark/url/L3dhdGVybWFyay12MS4zLnBuZw==/align/center");
                    article1.setUrl("http://www.cnblogs.com/x-99/");
                    Article article2 = new Article();
                    article2.setTitle("微信公众帐号开发教程.NET版");
                    article2.setDescription("");
                    article2.setPicUrl("http://pic.qiantucdn.com/58pic/26/10/40/58c04e46c2ffa_1024.jpg!/fw/780/watermark/url/L3dhdGVybWFyay12MS4zLnBuZw==/align/center");
                    article2.setUrl("http://www.cnblogs.com/x-99/");
                    Article article3 = new Article();
                    article3.setTitle("微信公众帐号开发教程C版");
                    article3.setDescription("");
                    article3.setPicUrl("http://pic.qiantucdn.com/58pic/26/10/40/58c04e46c2ffa_1024.jpg!/fw/780/watermark/url/L3dhdGVybWFyay12MS4zLnBuZw==/align/center");
                    article3.setUrl("http://www.cnblogs.com/x-99/");

                    articleList.add(article1);
                    articleList.add(article2);
                    articleList.add(article3);
                    // 设置图文消息个数
                    newsMessage.setArticleCount(articleList.size());
                    // 设置图文消息包含的图文集合
                    newsMessage.setArticles(articleList);
                    // 将图文消息对象转换成xml字符串
                    respMessage = MessageUtil.messageToXml(newsMessage);
                }

            }

            log.info(respMessage);
            //response.getWriter().write(respMessage);

        } catch (Exception e) {
            log.error("异常", e);
        }
        return respMessage;
    }

}
