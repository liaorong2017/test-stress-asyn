package org.raje.test.tcp;

import org.raje.test.common.Result;
import org.springframework.stereotype.Component;

@Component
public class NettyAsynCallBack {

	public Result callBack(String content) {
		if (content.startsWith("HTTP/1.1 200 OK")) {
			return Result.SUCC;
		} else {
			return Result.httpStatusNoOk;
		}
	}

}
