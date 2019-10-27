package com.example.demo.util;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @Version 1.0
 * @Since JDK1.8
 * @Author HYK
 * @Company 河南艾鹿
 * @Date 2018/5/15 0015 16:26
 */
@Slf4j
@Component
public class WxUtil {

	public String appId = Constants.APP_ID;

	public String appSecret = Constants.APP_SECRET;

	/**
	 * 获取accessToken
	 *
	 * @return accessToken
	 */
	public String getAccessToken() {
		String accessTokenKey = Constants.ACCESS_TOKEN_KEY;
		return StringUtils.isBlank(accessTokenKey) ? "无token" : accessTokenKey;
	}

	/**
	 * 设置最新的accessToken
	 */
	public void setAccessToken() {
		//1.获取最新的accessToken
		String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appId + "&secret=" + appSecret;

		try {
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<JSONObject> responseEntity = restTemplate.getForEntity(url, JSONObject.class);
			JSONObject jsonObject = responseEntity.getBody();
			String accessToken = jsonObject.getString("access_token");
		} catch (Exception e) {
			e.printStackTrace();
			log.error("设置accessToken失败 , 请求地址 {},错误 ", e.getMessage(), url);
		}

	}

	/**
	 * 获取用户信息
	 *
	 * @param code 用户code
	 * @return 用户信息
	 */
	public JSONObject getUserInfo(String code) {
		String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + appId + "&secret=" + appSecret + "&code=" + code + "&grant_type=authorization_code";
		try {
			// 因为此url返回header里的Content-Type为text/plain,所以需要设置restTemplate支持此格式
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(new WxMappingJackson2HttpMessageConverter());

			// 返回值类型为LinkedHashMap,但是不知道为什么无法直接接收,所以先用Object接收,然后强转
			Object object = restTemplate.getForObject(url, Object.class);
			LinkedHashMap<String, String> hashMap = (LinkedHashMap) object;
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("access_token", hashMap.get("access_token"));
			jsonObject.put("expires_in", hashMap.get("expires_in"));
			jsonObject.put("refresh_token", hashMap.get("refresh_token"));
			jsonObject.put("openid", hashMap.get("openid"));
			jsonObject.put("scope", hashMap.get("scope"));
			return jsonObject;
		} catch (Exception e) {
			log.error("获取用户信息失败,请求地址 {} , 异常信息： {} ", url,e);
		}
		return null;
	}

	/**
	 * 根据accessToken和openId获取具体的用户信息
	 *
	 * @param accessToken accessToken
	 * @param openId      openId
	 * @return 详细的用户信息
	 */
	public JSONObject getUserInfo(String accessToken, String openId) {
		String url = "https://api.weixin.qq.com/sns/userinfo?access_token=" + accessToken + "&openid=" + openId + "&lang=zh_CN";
		try {
			// 因为此url返回header里的Content-Type为text/plain,所以需要设置restTemplate支持此格式
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(new WxMappingJackson2HttpMessageConverter());

			// 返回值类型为LinkedHashMap,但是不知道为什么无法直接接收,所以先用Object接收,然后强转
			Object object = restTemplate.getForObject(url, Object.class);
			LinkedHashMap<String, String> hashMap = (LinkedHashMap) object;
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("openid", hashMap.get("openid"));
			jsonObject.put("nickname", hashMap.get("nickname"));
			jsonObject.put("sex", hashMap.get("sex"));
			jsonObject.put("province", hashMap.get("province"));
			jsonObject.put("city", hashMap.get("city"));
			jsonObject.put("country", hashMap.get("country"));
			jsonObject.put("headimgurl", hashMap.get("headimgurl"));
			jsonObject.put("privilege", hashMap.get("privilege"));
			jsonObject.put("unionid", hashMap.get("unionid"));
			return jsonObject;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("获取用户详细信息失败 {} , 请求地址 {} ", e.getMessage(), url);
		}
		return null;
	}


	/**
	 * 此处为微信的一个大坑,使用code去换取用户信息时.文档中描写是json格式,其实Header 里面的 Content-Type 值却是 text/plain
	 */
	public class WxMappingJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {
		public WxMappingJackson2HttpMessageConverter() {
			List<MediaType> mediaTypes = new ArrayList<>();
			mediaTypes.add(MediaType.TEXT_PLAIN);
			setSupportedMediaTypes(mediaTypes);
		}
	}


}
