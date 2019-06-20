package com.sofn.sys.util.shiro;

import com.sofn.common.constants.Constants;
import com.sofn.common.utils.JsonUtils;
import com.sofn.common.utils.JwtUtils;
import com.sofn.common.utils.RedisHelper;
import com.sofn.common.utils.RedisUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 认证
 */
@Component
public class UserRealm extends AuthorizingRealm {
	
    @Autowired
	RedisHelper redisHelper;

	@Override
	public boolean supports(AuthenticationToken token) {
		return token instanceof JWTToken;
	}
    
    /**
     * 授权(验证权限时调用)
     */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		String token = (String) principals.getPrimaryPrincipal();

		Set<String> permsSet = new HashSet<>();
		Object object = redisHelper.hget(token,Constants.UserSession.permissions);
		if (object != null) {
			List<String> list = JsonUtils.json2List(object.toString(),String.class);
			if (list != null && list.size()>0) {
				permsSet.addAll(list);
			}
		}

		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		info.setStringPermissions(permsSet);
		return info;
	}

	/**
	 * 认证(登录时调用)
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(
			AuthenticationToken authcToken) throws AuthenticationException {
		String token = (String) authcToken.getCredentials();
		String username = JwtUtils.getUsername(token);
		if (StringUtils.isEmpty(username) || !JwtUtils.verify(token)) {
			throw new UnauthorizedException("token invalid");
		}

		return new SimpleAuthenticationInfo(token, token, "sofn_realm");
	}

	@Override
	public void setCredentialsMatcher(CredentialsMatcher credentialsMatcher) {
		//HashedCredentialsMatcher shaCredentialsMatcher = new HashedCredentialsMatcher();
		//shaCredentialsMatcher.setHashAlgorithmName(ShiroUtils.hashAlgorithmName);
		//shaCredentialsMatcher.setHashIterations(ShiroUtils.hashIterations);
		super.setCredentialsMatcher(credentialsMatcher);
	}
}
