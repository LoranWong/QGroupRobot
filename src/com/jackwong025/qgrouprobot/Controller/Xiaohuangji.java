
package com.jackwong025.qgrouprobot.Controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.jackwong025.qgrouprobot.Utils.HttpUtil;
import com.jackwong025.qgrouprobot.Utils.StringUtil;
import com.jackwong025.qgrouprobot.View.Constant;


public class Xiaohuangji {

    private static final String URL = "http://www.simsimi.com/func/reqN";

    private static final int REPEAT_TIME = 3;
    private static final String[] AD = {"微", "信号", "sharejokes","新浪", "关注", "微博", "www.", ".com", "扣扣" ,"搞笑","腐女","share" ,"聊","信"};

    private static final Map<String, String> PROPERTIES = new HashMap<String, String>() {
        {
            put("Cookie",
                    "simsimi_uid=51678750; "
                    + "Filtering=0.0; "
                    + "sid=s%3ArYalXpIcOh3zAEo0SDWep7TQ.SjEbAoTeYsXMLm9TgzyIGHpge1yrSBzNbcj8Cvvp87A; "
                    + "AWSELB=150F676708F2639057F41EA6CD9115064C58E864E4D5FE3F62AF683EB3CA54C1A44837308BAB86F4F48D2BA2A2B01B0AEA34FBA3D92BA7AB89083051C189504CF5589F0BF7; "
                    + "sim_name=%u5BA2%u4EBA; "
                    + "teach_btn_url=talk; "
                    + "selected_nc=ch;"
            );
            put("Host", "www.simsimi.com");
            put("Referer", "http://www.simsimi.com/talk.htm");
        }
    };

    public static synchronized String chat(String text) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("req", text.trim());
        params.put("lc", "ch");
        params.put("ft", "0.0");
        params.put("fl", "http://www.simsimi.com/talk.htm");
        return chat(params);
    }

    private static String chat(Map<String, String> params) {
        String response = null;
        try {
            response = HttpUtil.doGet(URL, params, PROPERTIES);
            response = getResultFromJson(response);
        }catch (Exception ex) {
            return Constant.XHJ_ERROR;
        }
        if (StringUtil.isEmpty(response)) {
            return Constant.XHJ_ERROR;
        }
        if (isAd(response)) {
            System.out.println("发现广告: " + response);
            return Constant.XHJ_ERROR;
        }
        return delTrash(response);
    }

    private static String getResultFromJson(String json) throws JSONException {
        System.out.println(json);    
        JSONObject jo = new JSONObject(json);
        return jo.getString("sentence_resp");

    }

    private static String delTrash(String text) {
        text = StringUtil.repaceTabs(text);
        return text.replace("\\n", "");
    }
    private static boolean isAd(String text) {
        for (String ad : AD) {
            if (text.contains(ad)) {
                return true;
            }
        }
        return false;
    }
}
