package com.example.demo.controller;


import com.alibaba.fastjson.JSONObject;
import com.example.demo.util.Constants;
import com.example.demo.util.WxUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Controller
@RequestMapping("/wx")
public class WxController extends BaseController {

	public String appId = Constants.APP_ID;

	@Resource
	private WxUtil wxUtil;

	/**
	 * 跳转到微信授权页面
	 *
	 * @return 重定向
	 */
	@RequestMapping("/gfs")
	public String index(int skip) {
		log.info("skip-->" + skip);
		return "redirect:" + getWxAuthUrl(skip);
	}

	/**
	 * 微信授权的回调页面
	 *
	 * @param code    获取用户access_token的凭据
	 * @param state                   
	 * @param session
	 * @return 跳转的页面
	 */
	@RequestMapping("/getAccessTokenAndOpenId")
	public String getAccessTokenAndOpenId(String code, int state, HttpSession session) {
		// 如果code为空表示已经登录过,从session中获取access_token和openid.如果不为空则去网络请求
		log.info("skip-->state-->" + state);
		if (!Objects.equals(null, code)) {
			JSONObject jsonObject = wxUtil.getUserInfo(code);

			setAccessTokenSession(session, jsonObject.getString("access_token"));
			setOpenIdSession(session, jsonObject.getString("openid"));
		}
		String openId = getOpenIdFromSession(session);
		String accessToken = getAccessTokenSession(session);

		// 如果没有获取到accessToken,重新获取授权
		if (Objects.equals(null, accessToken) || Objects.equals(null, openId)) {
			return "redirect:" + getWxAuthUrl(state);
		}

		// 获取用户的详细信息
		JSONObject jsonObject = wxUtil.getUserInfo(accessToken, openId);



		//分发 跳转 指定 页面
		switch (state){
			case 1 ://跳转C端
				return "redirect:" + projectWxUrl + "/login?organ=" + state;
		}
		// 跳转到 错误 页面
		return "error";

	}

	public String projectWxUrl = Constants.PROJECT_WX_URL;

	/**
	 * 获取微信授权地址
	 *
	 * @return 微信授权地址
	 */
	private String getWxAuthUrl(int skip) {
		// 微信授权回调接口
		String backUrl = projectWxUrl + "/wx/getAccessTokenAndOpenId";
		//backUrl = projectWxUrl + "/login";
		try {
			backUrl = URLEncoder.encode(backUrl, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.error("授权回调 获取授权地址 encoder url  {} ->", backUrl,e);
		}

		String url = String.format("https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=%s&state=%s#wechat_redirect",
				appId, backUrl, "snsapi_userinfo", skip);

		return url;
	}


	/**
	 * 此接口为为了是本地变为测试公众号的服务器所用的接口
	 *
	 * @param signature signature
	 * @param timestamp timestamp
	 * @param nonce     nonce
	 * @param echostr   echostr
	 * @param response  response
	 */
	@RequestMapping("become")
	public void become(String signature, String timestamp, String nonce, String echostr, String openid,
                       HttpServletRequest request, HttpServletResponse response) {
		System.out.println(signature);
		System.out.println(timestamp);
		System.out.println(nonce);
		System.out.println(echostr);
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter printWriter = response.getWriter();
			// 获取HTTP请求的输入流 接受消息
			// 已HTTP请求输入流建立一个BufferedReader对象
			BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));

			String buffer = null;
			// 存放请求内容
			StringBuffer xml = new StringBuffer();
			while ((buffer = br.readLine()) != null) {
				// 在页面中显示读取到的请求参数
				xml.append(buffer);
			}

			Map map  = new HashMap();
			StringBuffer sb = new StringBuffer("<xml>");
			sb.append("<ToUserName>")
					.append(openid)
					.append("</ToUserName>");
			sb.append("<FromUserName>")
					.append(map.get("ToUserName"))
					.append("</FromUserName>");
			sb.append("<CreateTime>")
					.append(timestamp)
					.append("</CreateTime>");

			sb.append("<MsgType>")
					.append("text")
					.append("</MsgType>");
			sb.append("<Content>")
					.append("")
					.append("</Content>");
			sb.append("</xml>");
			log.debug("发送给公众号->{}",sb.toString());
			printWriter.print(sb.toString());
			printWriter.flush();
		} catch (Exception e) {
			log.error("公众号接收用户微信 信息异常：", e);
		}
	}

}
