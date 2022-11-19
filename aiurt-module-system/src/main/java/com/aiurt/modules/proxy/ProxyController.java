package com.aiurt.modules.proxy;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(ProxyController.DELEGATE_PREFIX)
public class ProxyController {


    public final static String DELEGATE_PREFIX = "/proxy";

    private String routeUrl = "http://39.97.225.186";

    @Autowired
    private RoutingDelegate routingDelegate;

    @RequestMapping(value = "/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE}, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity catchAll(HttpServletRequest request, HttpServletResponse response) {
        String token = routingDelegate.getToken();
        Cookie cookie = new Cookie("Admin-Token", token);
        response.addCookie(cookie);
        return routingDelegate.redirect(request, response, routeUrl, DELEGATE_PREFIX);
    }
}
