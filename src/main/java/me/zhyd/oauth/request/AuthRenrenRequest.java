package me.zhyd.oauth.request;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.enums.AuthUserGender;
import me.zhyd.oauth.exception.AuthException;
import me.zhyd.oauth.model.*;
import me.zhyd.oauth.utils.UrlBuilder;

import java.util.Objects;

import static me.zhyd.oauth.config.AuthSource.RENREN;
import static me.zhyd.oauth.model.AuthResponseStatus.SUCCESS;

/**
 * 人人登录
 *
 * @author hongwei.peng (pengisgood(at)gmail(dot)com)
 * @version 1.9.0
 * @since 1.8
 */
public class AuthRenrenRequest extends AuthDefaultRequest {

    public AuthRenrenRequest(AuthConfig config) {
        super(config, RENREN);
    }

    @Override
    protected AuthToken getAccessToken(AuthCallback authCallback) {
        return this.getToken(accessTokenUrl(authCallback.getCode()));
    }

    @Override
    protected AuthUser getUserInfo(AuthToken authToken) {
        HttpResponse response = doGetUserInfo(authToken);
        JSONObject userObj = JSONObject.parseObject(response.body()).getJSONObject("response");

        return AuthUser.builder()
            .uuid(userObj.getString("id"))
            .avatar(getAvatarUrl(userObj))
            .nickname(userObj.getString("name"))
            .company(getCompany(userObj))
            .gender(getGender(userObj))
            .token(authToken)
            .source(source)
            .build();
    }

    @Override
    public AuthResponse refresh(AuthToken authToken) {
        return AuthResponse.builder()
            .code(SUCCESS.getCode())
            .data(getToken(this.refreshTokenUrl(authToken.getRefreshToken())))
            .build();
    }

    private AuthToken getToken(String url) {
        HttpResponse response = HttpRequest.post(url).execute();
        JSONObject jsonObject = JSONObject.parseObject(response.body());
        if (jsonObject.containsKey("error")) {
            throw new AuthException("Failed to get token from Renren: " + jsonObject);
        }

        return AuthToken.builder()
            .tokenType(jsonObject.getString("token_type"))
            .expireIn(jsonObject.getIntValue("expires_in"))
            .accessToken(jsonObject.getString("access_token"))
            .refreshToken(jsonObject.getString("refresh_token"))
            .openId(jsonObject.getJSONObject("user").getString("id"))
            .build();
    }

    private String getAvatarUrl(JSONObject userObj) {
        JSONArray jsonArray = userObj.getJSONArray("avatar");
        if (Objects.isNull(jsonArray) || jsonArray.isEmpty()) {
            return null;
        }
        return jsonArray.getJSONObject(0).getString("url");
    }

    private AuthUserGender getGender(JSONObject userObj) {
        JSONObject basicInformation = userObj.getJSONObject("basicInformation");
        if (Objects.isNull(basicInformation)) {
            return AuthUserGender.UNKNOWN;
        }
        return AuthUserGender.getRealGender(basicInformation.getString("sex"));
    }

    private String getCompany(JSONObject userObj) {
        JSONArray jsonArray = userObj.getJSONArray("work");
        if (Objects.isNull(jsonArray) || jsonArray.isEmpty()) {
            return null;
        }
        return jsonArray.getJSONObject(0).getString("name");
    }

    /**
     * 返回获取userInfo的url
     *
     * @param authToken 用户授权后的token
     * @return 返回获取userInfo的url
     */
    @Override
    protected String userInfoUrl(AuthToken authToken) {
        return UrlBuilder.fromBaseUrl(source.userInfo())
            .queryParam("access_token", authToken.getAccessToken())
            .queryParam("userId", authToken.getOpenId())
            .build();
    }
}
