package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.model.AesException;
import com.example.demo.util.HttpUtil;
import com.example.demo.util.WXPublicUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RequestMapping("/wx")
@RestController
public class TestController {

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
