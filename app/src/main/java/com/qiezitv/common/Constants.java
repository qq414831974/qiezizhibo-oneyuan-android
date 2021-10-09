package com.qiezitv.common;

/**
 * APP常量
 */
public class Constants {

    /**
     * 全局临时token
     */
    public static String ACCESS_TOKEN = null;

    /**
     * 启动app时SplashActivity画面停止时间 milliseconds
     */
    public static final int SPLASH_STOP_TIME = 1000;
    /**
     * sp文件名称
     */
    public static final String SP_FILE_NAME = "com_fjnu_qiezi_sp";

    //--------------------------  http 请求部分 -----------------------------
    /**
     * 服务器默认IP地址
     */
    public static final String SERVER_DEFAULT_IP = "oneyuan.qiezizhibo.com";
    /**
     * http连接超时时间
     */
    public static final int CONNECT_TIMEOUT = 20;
    /**
     * http读超时时间
     */
    public static final int READ_TIMEOUT = 30;
    /**
     * http写超时时间
     */
    public static final int WRITE_TIMEOUT = 30;


    //--------------------------- sp key --------------------
    /**
     * 登录账号
     */
    public static final String SP_LOGIN_USER = "sp_login_user";
    /**
     * 是否记住密码
     */
    public static final String SP_IS_REMEMBER = "sp_remember";
    /**
     * 登录密码
     */
    public static final String SP_LOGIN_PASSWORD = "sp_login_password";
    /**
     * 是否自动登录
     */
    public static final String SP_IS_AUTO_LOGIN = "sp_is_auto_login";
    /**
     * 登录AccessToken
     */
    public static final String SP_ACCESS_TOKEN = "sp_access_token";

    public interface MatchType {
        int STATISTICS = 1;
        int PLAYRLIST = 2;
        int CAHTTINGROOM = 3;
        int CLIP = 4;
    }

    public static class VideoQuality {
        public static final int LOW = 0;
        public static final int MID = 1;
        public static final int HIGH = 2;
    }
    public class ActivityQuality {
        public static final int UNKNOW = -1;
        public static final int NORMAL = 0;
        public static final int BAD = 1;
        public static final int NOTBAD = 2;
    }
    public interface LeagueRuleType {
        int TYPE_XIAO_LANQIU = 1;
        int TYPE_1_x_1 = 2;
        int TYPE_3_x_3 = 3;
        int TYPE_5_x_5 = 4;
    }
}
