/*
 * Copyright (C) 2014 神马才注册（Invisible God） <373575012@qq.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * 部分代码源自互联网
 */
package com.jackwong025.qgrouprobot.Utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * 进行http访问的基本类
 *
 * @author chenhetong(chenhetong@baidu.com)
 * @author 神马才注册
 */
public class HttpUtil {

    private HttpUtil() {
    }

    public static final String DEFAULT_CHARSET = "UTF-8";

    public static final int CONNECT_TIMEOUT = 2500;

    public static final int READ_TIMEOUT = 2500;

    private static final String METHOD_POST = "POST";

    private static final String METHOD_GET = "GET";

    private static final String CHECK_IP = "http://ddns.oray.com/checkip";

    private static class DefaultTrustManager implements X509TrustManager {

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        @Override
        public void checkClientTrusted(X509Certificate[] cert, String oauthType)
                throws java.security.cert.CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] cert, String oauthType)
                throws java.security.cert.CertificateException {
        }
    }

    private static HttpURLConnection getConnection(URL url, String method, String ctype)
            throws IOException {

        HttpURLConnection conn = null;
        if ("https".equals(url.getProtocol())) {
            SSLContext ctx = null;
            try {
                ctx = SSLContext.getInstance("TLS");
                ctx.init(new KeyManager[0], new TrustManager[]{new DefaultTrustManager()},
                        new SecureRandom());
            } catch (KeyManagementException e) {
                throw new IOException(e);
            } catch (NoSuchAlgorithmException e) {
                throw new IOException(e);
            }
            HttpsURLConnection connHttps = (HttpsURLConnection) url.openConnection();
            connHttps.setSSLSocketFactory(ctx.getSocketFactory());
            connHttps.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;// 默认都认证通过
                }
            });
            conn = connHttps;
        } else {
            conn = (HttpURLConnection) url.openConnection();
        }
        conn.setRequestMethod(method);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        //conn.setRequestProperty("User-Agent", "god-restclient-java-2.0");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.17 Safari/537.36");
        conn.setRequestProperty("Content-Type", ctype);
        conn.setRequestProperty("Connection", "Keep-Alive");
        return conn;

    }

    /**
     * 通过get方法访问，无参数，默认编码为utf-8
     *
     * @param url 访问的url地址
     * @return 返回请求响应的数据
     * @throws IOException
     */
    public static String doGet(String url) throws IOException {
        return doGet(url, null);
    }

    /**
     * 通过get方法访问，默认编码为utf-8
     *
     * @param url 访问的url地址
     * @param params 请求需要的参数
     * @return 返回请求响应的数据
     * @throws IOException
     */
    public static String doGet(String url, Map<String, String> params)
            throws IOException {
        return doGet(url, params, null);
    }

    /**
     *
     * 通过get方法请求数据，默认字符编码为utf-8
     *
     * @param url 请求的url地址
     * @param params 请求的参数
     * @param properties
     * @return 请求响应
     * @throws IOException
     */
    public static String doGet(String url, Map<String, String> params, Map<String, String> properties)
            throws IOException {
        return doGet(url, params, properties, DEFAULT_CHARSET, CONNECT_TIMEOUT, READ_TIMEOUT);
    }

    /**
     * 通过get方法访问
     *
     * @param url 访问的url地址
     * @param params 请求需要的参数
     * @param charset 字符编码
     * @param connectTimeOut
     * @param readTimeOut
     * @param properties
     * @return 返回请求响应的数据
     * @throws IOException
     */
    public static String doGet(String url, Map<String, String> params, Map<String, String> properties,
            String charset, int connectTimeOut, int readTimeOut)
            throws IOException {
        if (StringUtil.isEmpty(url)) {
            return null;
        }
        HttpURLConnection conn = null;
        String response = "";
        String ctype = "application/x-www-form-urlencoded;charset=" + charset;
        if (params != null && !params.isEmpty()) {
            url += "?" + buildQuery(params, charset);
        }
        conn = getConnection(new URL(url), METHOD_GET, ctype);
        conn.setConnectTimeout(connectTimeOut);
        conn.setReadTimeout(readTimeOut);
        if (properties != null && !properties.isEmpty()) {
            for (Entry<String, String> p : properties.entrySet()) {
                conn.setRequestProperty(p.getKey(), p.getValue());
            }
        }
        response = getResponseAsString(conn);
        conn.disconnect();
        return response;
    }

    /**
     * 通过post方法请求数据，无参数，默认编码为utf-8
     *
     * @param url api请求的权路径url地址
     * @return
     * @throws IOException
     */
    public static String doPost(String url) throws IOException {
        return doPost(url, null);
    }

    /**
     * 通过post方法请求数据，默认编码为utf-8
     *
     * @param url api请求的权路径url地址
     * @param params api请求的业务级参数
     * @return
     * @throws IOException
     */
    public static String doPost(String url, Map<String, String> params)
            throws IOException {
        return doPost(url, params, null);
    }

    /**
     *
     * 通过post方法请求数据，默认字符编码为utf-8
     *
     * @param url 请求的url地址
     * @param params 请求的参数
     * @param properties
     * @return 请求响应
     * @throws IOException
     */
    public static String doPost(String url, Map<String, String> params, Map<String, String> properties)
            throws IOException {
        return doPost(url, params, properties, DEFAULT_CHARSET, CONNECT_TIMEOUT, READ_TIMEOUT);
    }

    /**
     *
     * 通过post方法请求数据
     *
     * @param url 请求的url地址
     * @param params 请求的参数
     * @param charset 字符编码格式
     * @param connectTimeOut 请求连接过期时间
     * @param readTimeOut 请求读取过期时间
     * @param properties
     * @return 请求响应
     * @throws IOException
     */
    public static String doPost(String url, Map<String, String> params, Map<String, String> properties,
            String charset, int connectTimeOut, int readTimeOut)
            throws IOException {
        if (StringUtil.isEmpty(url)) {
            return null;
        }
        HttpURLConnection conn = null;
        String response = "";
        String ctype = "application/x-www-form-urlencoded;charset=" + charset;
        conn = getConnection(new URL(url), METHOD_POST, ctype);
        conn.setConnectTimeout(connectTimeOut);
        conn.setReadTimeout(readTimeOut);
        if (properties != null && !properties.isEmpty()) {
            for (Entry<String, String> p : properties.entrySet()) {
                conn.setRequestProperty(p.getKey(), p.getValue());
            }
        }
        if (params != null && !params.isEmpty()) {
            conn.getOutputStream().write(buildQuery(params, charset).getBytes(charset));
        }
        response = getResponseAsString(conn);
        conn.disconnect();
        return response;
    }

    /**
     *
     * @param params 请求参数
     * @param charset
     * @return 构建query
     */
    public static String buildQuery(Map<String, String> params, String charset) {
        StringBuilder sb = new StringBuilder(params.size() * 10);
        boolean first = true;
        for (Entry<String, String> entry : params.entrySet()) {
            if (first) {
                first = false;
            } else {
                sb.append("&");
            }
            String key = entry.getKey();
            String value = entry.getValue();
            if (StringUtil.areNotEmpty(key, value)) {
                try {
                    sb.append(key).append("=").append(URLEncoder.encode(value, charset));
                } catch (UnsupportedEncodingException e) {
                }
            }
        }
        return sb.toString();

    }

    public static Map<String, String> splitQuery(String query, String charset) {
        Map<String, String> ret = new HashMap<String, String>();
        if (!StringUtil.isEmpty(query)) {
            String[] splits = query.split("\\&");
            for (String split : splits) {
                String[] keyAndValue = split.split("\\=");
                if (StringUtil.areNotEmpty(keyAndValue) && keyAndValue.length == 2) {
                    try {
                        ret.put(keyAndValue[0], URLDecoder.decode(keyAndValue[1], charset));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return ret;
    }

    private static String getResponseAsString(HttpURLConnection conn)
            throws IOException {
        String charset = getResponseCharset(conn.getContentType());
        InputStream es = conn.getErrorStream();
        if (es == null) {
            return getStreamAsString(conn.getInputStream(), charset);
        } else {
            String msg = getStreamAsString(es, charset);
            if (StringUtil.isEmpty(msg)) {
                throw new IOException(conn.getResponseCode() + " : " + conn.getResponseMessage());
            } else {
                throw new IOException(msg);
            }
        }
    }

    private static byte[] getResponseAsByteArray(HttpURLConnection conn)
            throws IOException {
        String charset = getResponseCharset(conn.getContentType());
        InputStream es = conn.getErrorStream();
        if (es == null) {
            return getStreamAsByteArray(conn.getInputStream());
        } else {
            String msg = getStreamAsString(es, charset);
            if (StringUtil.isEmpty(msg)) {
                throw new IOException(conn.getResponseCode() + " : " + conn.getResponseMessage());
            } else {
                throw new IOException(msg);
            }
        }
    }

    private static byte[] getStreamAsByteArray(InputStream input)
            throws IOException {
        ByteArrayOutputStream bos = null;
        BufferedInputStream bis = null;
        byte[] buffer = null;
        try {
            bos = new ByteArrayOutputStream();
            bis = new BufferedInputStream(input);
            buffer = new byte[1024];
            int length = -1;
            while ((length = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, length);
                bos.flush();
            }
            return bos.toByteArray();
        } finally {
            if (bos != null) {
                bos.close();
                bos = null;
            }
            if (bis != null) {
                bis.close();
                bis = null;
            }
            buffer = null;
        }
    }

    private static String getStreamAsString(InputStream input, String charset)
            throws IOException {
        StringBuilder sb = new StringBuilder(100);
        BufferedReader bf = null;
        try {
            bf = new BufferedReader(new InputStreamReader(input, charset));
            String str;
            while ((str = bf.readLine()) != null) {
                sb.append(str);
            }
            return sb.toString();
        } finally {
            if (bf != null) {
                bf.close();
                bf = null;
            }
        }
    }

    private static String getResponseCharset(String ctype) {
        String charset = DEFAULT_CHARSET;
        if (!StringUtil.isEmpty(ctype)) {
            String[] params = ctype.split("\\;");
            for (String param : params) {
                param = param.trim();
                if (param.startsWith("charset")) {
                    String[] pair = param.split("\\=");
                    if (pair.length == 2) {
                        charset = pair[1].trim();
                    }
                }
            }
        }
        return charset;
    }

    /**
     * 下载文件
     *
     * @param url
     * @return
     * @throws java.io.IOException
     */
    public static byte[] downloadFile(String url) throws IOException {
        return downloadFile(url, null, null);
    }

    /**
     * 下载文件
     *
     * @param url
     * @param params
     * @return
     * @throws java.io.IOException
     */
    public static byte[] downloadFile(String url, Map<String, String> params)
            throws IOException {
        return downloadFile(url, params, null);
    }

    /**
     * 下载文件
     *
     * @param url
     * @param params
     * @param properties
     * @return
     * @throws java.io.IOException
     */
    public static byte[] downloadFile(String url, Map<String, String> params,
            Map<String, String> properties) throws IOException {
        if (StringUtil.isEmpty(url)) {
            return null;
        }
        HttpURLConnection conn = null;
        byte[] response = null;
        String ctype = "application/x-www-form-urlencoded;charset=" + DEFAULT_CHARSET;
        conn = getConnection(new URL(url), METHOD_GET, ctype);
        conn.setConnectTimeout(CONNECT_TIMEOUT);
        if (properties != null && !properties.isEmpty()) {
            for (Entry<String, String> p : properties.entrySet()) {
                conn.setRequestProperty(p.getKey(), p.getValue());
            }
        }
        if (params != null && !params.isEmpty()) {
            conn.getOutputStream().write(buildQuery(params, DEFAULT_CHARSET).getBytes());
        }
        response = getResponseAsByteArray(conn);
        conn.disconnect();
        return response;
    }

    /**
     * 获得目标大小
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static int getLength(String url) throws IOException {
        return getLength(url, null);
    }

    /**
     * 获得目标大小
     *
     * @param url
     * @param params
     * @return
     * @throws IOException
     */
    public static int getLength(String url, Map<String, String> params)
            throws IOException {
        if (StringUtil.isEmpty(url)) {
            return 0;
        }
        HttpURLConnection conn = null;
        String ctype = "application/x-www-form-urlencoded;charset=" + DEFAULT_CHARSET;
        conn = getConnection(new URL(url), METHOD_GET, ctype);
        conn.setConnectTimeout(CONNECT_TIMEOUT);
        if (params != null && !params.isEmpty()) {
            conn.getOutputStream().write(buildQuery(params, DEFAULT_CHARSET).getBytes());
        }
        int length = conn.getContentLength();
        conn.disconnect();
        return length;
    }

    /**
     * 获得网络IP地址
     *
     * @return
     * @throws IOException
     */
    public static String getCurrentIPAddress() throws IOException {
        String result = HttpUtil.doGet(CHECK_IP);
        return StringUtil.getMiddle(result, "IP Address: ", "</body></html>").trim();
    }

    public static void main(String[] args) throws IOException {
        System.out.println(getCurrentIPAddress());
    }
}
