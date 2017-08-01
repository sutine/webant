package org.webant.queen.commons.vo;

public class ErrorCode {
    public static final int  NONE = 0;                      // 没有错误
    public static final int  BAD_REQUEST = 400 ;            // 请求参数错误
    public static final int  IGN_INVALIDATE = 401 ;         // 数据签名验证失败
    public static final int  UNAUTHORIZED = 402;            // 请求用户验证失败
    public static final int  FORBIDDEN = 403;               // 请求被拒绝（验证信息无效或过期）
    public static final int  REQUEST_TIMEOUT = 408;         // 请求超时
    public static final int  INTERNAL_SERVER_ERROR = 500 ;  // 服务器内部错误
    public static final int  NOT_IMPLEMENTED = 501;         // 服务器未实现当前请求的方法
    public static final int  BAD_GATEWAY = 502;             // 上游服务器响应错误
    public static final int  SERVICE_UNAVAILABLE = 503;     // 服务器当前无法处理请求（维护或者过载）
    public static final int  GATEWAY_TIMEOUT = 504;         // 上游服务器连接超时
    public static final int  APPLICATION_ERROR = 800;       // 应用业务逻辑错误

    public static String desc(int code) {
        switch (code) {
            case NONE: return "没有错误";
            case BAD_REQUEST: return "请求参数错误";
            case IGN_INVALIDATE: return "数据签名验证失败";
            case UNAUTHORIZED: return "请求用户验证失败";
            case FORBIDDEN: return "请求被拒绝";
            case REQUEST_TIMEOUT: return "请求超时";
            case INTERNAL_SERVER_ERROR: return "服务器内部错误";
            case NOT_IMPLEMENTED: return "服务器未实现当前请求的方法";
            case BAD_GATEWAY: return "上游服务器响应错误";
            case SERVICE_UNAVAILABLE: return "服务器当前无法处理请求";
            case GATEWAY_TIMEOUT: return "上游服务器连接超时";
            case APPLICATION_ERROR: return "应用业务逻辑错误";

            default: return "未知错误";
        }
    }
}
