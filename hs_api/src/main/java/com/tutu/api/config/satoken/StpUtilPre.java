package com.tutu.api.config.satoken;

import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.stp.StpLogic;

/**
 * Sa-Token temporary login (pre-auth) utility for "login -> choose tenant -> issue formal token" flow.
 *
 * <p>Rules:
 * - short TTL
 * - header-only token
 * - should be usable only on tenant selection endpoints</p>
 */
public final class StpUtilPre {

    private StpUtilPre() {
    }

    public static final String LOGIN_TYPE = "pre";

    private static final StpLogic STP_LOGIC = initLogic();

    private static StpLogic initLogic() {
        StpLogic logic = new StpLogic(LOGIN_TYPE);
        SaTokenConfig cfg = new SaTokenConfig();
        cfg.setTokenName("Pre-Token");
        cfg.setTimeout(300); // 5 min
        cfg.setActiveTimeout(-1);
        cfg.setIsConcurrent(true);
        cfg.setIsShare(false); // every pre-login issues a new token
        cfg.setIsReadCookie(false);
        cfg.setIsReadBody(false);
        cfg.setIsReadHeader(true);
        cfg.setIsLog(false);
        logic.setConfig(cfg);
        return logic;
    }

    public static void login(Object loginId) {
        STP_LOGIC.login(loginId);
    }

    public static void logout() {
        STP_LOGIC.logout();
    }

    public static void checkLogin() {
        STP_LOGIC.checkLogin();
    }

    public static String getLoginIdAsString() {
        return STP_LOGIC.getLoginIdAsString();
    }

    public static String getTokenValue() {
        return STP_LOGIC.getTokenValue();
    }
}

