package com.example.demo.controller;

import javax.servlet.http.HttpSession;
import java.util.Objects;

public class BaseController {

	private static final String userOpenIdSessionKey = "openId";

	private static final String userAccessTokenSessionKey = "access_token";

	public void setOpenIdSession(HttpSession session, String openId) {
		session.setAttribute(userOpenIdSessionKey, openId);
	}

	public String getOpenIdFromSession(HttpSession session) {
		Object object = session.getAttribute(userOpenIdSessionKey);
		return Objects.equals(null, object) ? null : object.toString();
	}

	public void setAccessTokenSession(HttpSession session, String accessToken) {
		session.setAttribute(userAccessTokenSessionKey, accessToken);
	}

	public String getAccessTokenSession(HttpSession session) {
		Object object = session.getAttribute(userAccessTokenSessionKey);
		return Objects.equals(null, object) ? null : object.toString();
	}

}
