package com.aiurt.boot.modules.message.websocket;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

@Api(tags = "123")
@RestController
@RequestMapping("/webSocketApi")
public class TestController {

    @Autowired
    private WebSocket webSocket;

	@ApiOperation("eeee")
    @PostMapping("/sendAll")
    public Result<String> sendAll(@RequestBody JSONObject jsonObject) {
    	Result<String> result = new Result<String>();
    	String title = jsonObject.getString("title");
    	String message = jsonObject.getString("message");
    	JSONObject obj = new JSONObject();
		obj.put("title",title);
		obj.put("msg",message);
    	webSocket.sendAllMessage(obj.toJSONString());
        result.setResult("群发！");
        return result;
    }

	@ApiOperation("888")
    @PostMapping("/sendUser")
    public Result<String> sendUser(@RequestBody JSONObject jsonObject) {
    	Result<String> result = new Result<String>();
    	String userId = jsonObject.getString("userId");
    	String message = jsonObject.getString("message");
    	JSONObject obj = new JSONObject();
    	obj.put("cmd", "user");
    	obj.put("userId", userId);
		obj.put("msgId", "M0001");
		obj.put("msgTxt", message);
        webSocket.sendOneMessage(userId, obj.toJSONString());
        result.setResult("单发");
        return result;
    }

}
