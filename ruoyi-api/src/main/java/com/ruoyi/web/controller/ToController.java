package com.ruoyi.web.controller;

import com.ruoyi.common.utils.PureBase64Encoder;
import com.ruoyi.common.utils.ServletUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;

@RestController
@RequestMapping("/r")
public class ToController {

    @GetMapping("/{e}")
    public ResponseEntity<Void> to(@PathVariable String e, HttpServletRequest request) {

        // ***** 获取前端域名信息 *****
        //HttpServletRequest request = ServletUtils.getRequest();
        // 1. 获取请求的原始来源域名 (最常用，但依赖浏览器和代理设置)
        String originHeader = request.getHeader("Origin");
        String origin = request.getHeader("origin");
        System.out.println("Origin Header (前端域名，用于CORS): " + originHeader);
        System.out.println("origin Header (前端域名，用于CORS): " + origin);

        // 2. 获取请求的 Referer (如果用户是从某个页面点击链接跳转过来的)
        String refererHeader = request.getHeader("Referer");
        System.out.println("Referer Header (跳转来源页面): " + refererHeader);

        // 3. 获取代理/负载均衡转发的原始 Host (如果后端部署在代理后)
        String forwardedHost = request.getHeader("X-Forwarded-Host");
        System.out.println("X-Forwarded-Host (代理转发的原始Host): " + forwardedHost);

        // 4. 获取服务器接收到的 Host (通常是代理或负载均衡的地址)
        String serverHost = request.getHeader("Host");
        System.out.println("Server Host (服务器接收的Host): " + serverHost);


        String decode = PureBase64Encoder.decode(e);
        URI uri = URI.create(serverHost+"/#/i&" + decode);
        return ResponseEntity.status(HttpStatus.FOUND).location(uri).build();
    }

}
