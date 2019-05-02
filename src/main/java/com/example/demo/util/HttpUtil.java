package com.example.demo.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.*;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

/**
 * @author yu3
 */
public class HttpUtil {
    private static final Logger log = LoggerFactory.getLogger(HttpUtil.class);

    private static class TrustAnyTrustManager implements X509TrustManager {
        // 该方法检查客户端的证书，若不信任该证书则抛出异常。由于我们不需要对客户端进行认证，因此我们只需要执行默认的信任管理器的这个方法。
        // JSSE中，默认的信任管理器类为TrustManager。
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        // 该方法检查服务器的证书，若不信任该证书同样抛出异常。通过自己实现该方法，可以使之信任我们指定的任何证书。在实现该方法时，也可以简单的不做任何处理，即一个空的函数体，由于不会抛出异常，它就会信任任何证书。
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        // 返回受信任的X509证书数组。
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    }

    private static class TrustAnyHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    /*
    public static String uploadFileByHttps(String url, String fileName, String charset) throws NoSuchAlgorithmException, KeyManagementException, IOException {
        log.debug("开始连接" + url);
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, new TrustManager[]{new TrustAnyTrustManager()}, new java.security.SecureRandom());
        URL console = new URL(url);
        HttpsURLConnection conn = (HttpsURLConnection) console.openConnection();
        conn.setSSLSocketFactory(sc.getSocketFactory());
        conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
        conn.setDoOutput(true);
        // 设置请求头multipart/form-data
        conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
        conn.connect();
        DataOutputStream out = new DataOutputStream(conn.getOutputStream());

        FileInputStream fis = new FileInputStream(new File(fileName));
        BASE64Encoder encoder = new BASE64Encoder();
		StringBuilder pictureBuffer = new StringBuilder();
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] temp = new byte[fis.available()];
        fis.read(temp);
        String tempStr = encoder.encode(temp);//BASE64字符文件
        out.write(tempStr.getBytes(charset));

        out.flush();
        out.close();
        fis.close();
        InputStream is = conn.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String strRead;
        while ((strRead = reader.readLine()) != null) {
            sb.append(strRead);
            sb.append("\r\n");
        }
        reader.close();
        log.debug("接收报文：\n" + sb.toString());
        return sb.toString();
    }
    */

    public static String postJsonDataByHttps(String url, String reqObjStr)
            throws NoSuchAlgorithmException, KeyManagementException, IOException {

        log.debug("开始连接" + url);
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, new TrustManager[]{new TrustAnyTrustManager()}, new java.security.SecureRandom());
        URL console = new URL(url);
        HttpsURLConnection conn = (HttpsURLConnection) console.openConnection();
        conn.setSSLSocketFactory(sc.getSocketFactory());
        conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
        conn.setDoOutput(true);
        // 设置请求头
        conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
        conn.connect();
        DataOutputStream out = new DataOutputStream(conn.getOutputStream());

        out.write(reqObjStr.getBytes("UTF-8"));
        // 刷新、关闭
        out.flush();
        out.close();
        InputStream is = conn.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String strRead;
        while ((strRead = reader.readLine()) != null) {
            sb.append(strRead);
            sb.append("\r\n");
        }
        reader.close();
        log.debug("接收报文：\n" + sb.toString());
        return sb.toString();
    }

    public static JSONObject postJsonDataByHttp(String url, String reqObjStr, String charset) throws IOException {
        return postDataByHttp(url, reqObjStr, charset, "application/json");

    }

    public static JSONObject postDataByHttp(String url, String reqObjStr, String charset, String contentType) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpPost httpPost = new HttpPost(url);

        StringEntity entity = new StringEntity(reqObjStr, charset);
        entity.setContentType(contentType);//application/x-www-form-urlencoded
        entity.setChunked(true);
        httpPost.setEntity(entity);

        httpPost.setConfig(RequestConfig.custom()
                .setConnectTimeout(10000).setConnectionRequestTimeout(10000)
                .setSocketTimeout(10000).build());
        // 发送请求
        CloseableHttpResponse response = httpClient.execute(httpPost);

        JSONObject jsonObject = null;
        HttpEntity httpEntity = response.getEntity();
        String result = EntityUtils.toString(httpEntity);
        log.info("返回值=" + result);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

            jsonObject = JSONObject.parseObject(result);
        } else {
            log.error("连接" + url + "失败,返回码=" + response.getStatusLine().getStatusCode());
        }
        if (response != null) {
            response.close();
        }
        return jsonObject;

    }

    public static JSONObject postDataByHttp(String url, Map<String, String> headers, String reqObjStr, String charset) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        for (String key :
                headers.keySet()) {
            httpPost.addHeader(key, headers.get(key));
        }
        StringEntity entity = new StringEntity(reqObjStr);
        entity.setContentType("application/json");
        entity.setChunked(true);
        httpPost.setEntity(entity);
        // 发送请求
        CloseableHttpResponse response = httpClient.execute(httpPost);

        log.debug("statusCode=" + response.getStatusLine().getStatusCode());

        JSONObject jsonObject = null;
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            HttpEntity httpEntity = response.getEntity();
            String result = EntityUtils.toString(httpEntity);
            jsonObject = JSONObject.parseObject(result);
        }
        if (response != null) {
            response.close();
        }
        return jsonObject;

    }


    /**
     * http get请求获取数据
     *
     * @param url
     * @param paramMap
     * @param charset
     * @return
     * @throws
     */
    public static Optional<HttpEntity> getDataByHttp(String url, Optional<Map<String, String>> paramMap, String charset, int timeOut) throws Exception {
        log.info("连接url：" + url);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        List<NameValuePair> pairs = new ArrayList<>();
        if (paramMap.isPresent()) {
            for (Map.Entry<String, String> entry : paramMap.get().entrySet()) {
                log.info(entry.getKey() + "=" + entry.getValue());
                pairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }
        try {
            URIBuilder builder = new URIBuilder(url);
            builder.setParameters(pairs);
            HttpGet httpGet = new HttpGet(builder.build());
            RequestConfig config = RequestConfig.custom().setConnectTimeout(timeOut)
                    .setConnectionRequestTimeout(timeOut).setSocketTimeout(timeOut).build();
            httpGet.setConfig(config);
            // 发送请求
            response = httpClient.execute(httpGet);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity httpEntity = response.getEntity();
                return Optional.ofNullable(httpEntity);
            } else {
                log.error("statusCode=" + response.getStatusLine().getStatusCode());
            }
            response.close();
            return Optional.empty();
        } catch (IOException e) {
            log.error("连接异常" + e.getMessage());
            throw e;
        } catch (URISyntaxException e) {
            log.error("url解析异常", e);
            throw e;
        }
    }

    public static Optional<InputStream> getStreamDataByHttp(String url, Map<String, String> paramMap, String charset) {
        Optional<HttpEntity> httpEntity = Optional.empty();
        try {
            httpEntity = getDataByHttp(url, Optional.ofNullable(paramMap), charset, 10000);
            if (httpEntity.isPresent()) {
                return Optional.ofNullable(httpEntity.get().getContent());
            } else {
                return Optional.empty();
            }
        } catch (Throwable e) {
            log.error(url + "连接异常", e);
            return Optional.empty();
        }
    }

    public static String getStringDataByHttp(String url, Map<String, String> paramMap, String charset) throws Exception {
        Optional<HttpEntity> httpEntity = getDataByHttp(url, Optional.ofNullable(paramMap), charset, 3000);
        if (httpEntity.isPresent()) {
            String result = EntityUtils.toString(httpEntity.get());
            return result;
        } else {
            return "";
        }
    }

    public static void postFileToURL(String url, File file, Map<String, String> params) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost uploadFile = new HttpPost(url);

        MultipartEntityBuilder builder = MultipartEntityBuilder.create()
                .setMode(HttpMultipartMode.RFC6532);//解决文件名乱码
        for (String key : params.keySet()) {
            log.info(key + "=" + params.get(key));
            builder.addTextBody(key, params.get(key), ContentType.create("text/plain", Charset.forName("UTF-8")));
        }

        // 把文件加到HTTP的post请求中
        builder.addBinaryBody(
                "file",
                new FileInputStream(file),
                ContentType.APPLICATION_OCTET_STREAM,
                file.getName()
        );

        HttpEntity multipart = builder.build();
        uploadFile.setEntity(multipart);
        CloseableHttpResponse response = httpClient.execute(uploadFile);
        HttpEntity responseEntity = response.getEntity();
        String sResponse = EntityUtils.toString(responseEntity, "UTF-8");
        log.info("Post 返回结果" + sResponse);
    }

    public static void main(String[] args) throws Exception {
        String url ="http://op.juhe.cn/onebox/weather/query";//请求接口地址
        String key = "abf8df53efdc5866d33e409fa242becc";
        Map params = new HashMap();//请求参数
        params.put("cityname","淮安");//要查询的城市，如：温州、上海、北京
        params.put("key", key);//应用APPKEY(应用详细页查询)
        params.put("dtype","json");//返回数据的格式,xml或json，默认json
        String returnStr = HttpUtil.getStringDataByHttp(url, params, "UTF-8");
        log.info(returnStr);
    }

}

