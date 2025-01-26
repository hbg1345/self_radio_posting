package com.example.selfRadioPosting.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
public class IPManger {
    private IPManger() {
        // 인스턴스화 방지
    }
    public static String sliceIp(String ip){
        String[] parts = ip.split("\\.");
        if (parts.length < 2)
            return ip;
        return parts[0] + "." + parts[1];
    }

    public static String getClientIp(HttpServletRequest request) throws UnknownHostException {
        String ip = request.getHeader("X-Forwarded-For");
        log.info("X-FORWARDED-FOR : " + ip);

        if (ip == null) {
            ip = request.getHeader("Proxy-Client-IP");
            log.info("Proxy-Client-IP : " + ip);
        }
        if (ip == null) {
            ip = request.getHeader("WL-Proxy-Client-IP");
            log.info("WL-Proxy-Client-IP : " + ip);
        }
        if (ip == null) {
            ip = request.getHeader("HTTP_CLIENT_IP");
            log.info("HTTP_CLIENT_IP : " + ip);
        }
        if (ip == null) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            log.info("HTTP_X_FORWARDED_FOR : " + ip);
        }
        if (ip == null) {
            ip = request.getRemoteAddr();
            log.info("getRemoteAddr : " + ip);
        }
        if ("0:0:0:0:0:0:0:1".equals(ip) || "127.0.0.1".equals(ip)) {
            InetAddress address = InetAddress.getLocalHost();
            ip = address.getHostAddress();
        }
        log.info("Result : IP Address : " + ip);
        return ip;
    }
}
