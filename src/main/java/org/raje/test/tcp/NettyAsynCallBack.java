package org.raje.test.tcp;

import org.raje.test.common.AsynCallBack;
import org.raje.test.common.Result;
import org.springframework.stereotype.Component;

@Component
public class NettyAsynCallBack implements AsynCallBack<String> {

	@Override
	public Result callBack(String content) {
		if (content.startsWith("HTTP/1.1 200 OK")) {
			return Result.SUCC;
		}else {
			return Result.httpStatusNoOk;
		}
	}

}
