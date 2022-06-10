package com.aiurt.boot.modules.webHome.utils;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * @description: SessionListener
 * @author: Mr.zhao
 * @date: 2021/10/28 16:24
 */
@WebListener
public class SessionListener implements HttpSessionListener {

	private static int USER_COUNT = 0;


	@Override
	public synchronized void sessionCreated(HttpSessionEvent se) {
		USER_COUNT+=1;
		System.out.println("监听器+1,现在总数量:"+USER_COUNT);
	}

	@Override
	public synchronized void sessionDestroyed(HttpSessionEvent se) {
		USER_COUNT-=1;
		System.out.println("监听器-1,现在总数量:"+USER_COUNT);
	}

	public static Integer getCount(){
		return USER_COUNT;
	}

}
