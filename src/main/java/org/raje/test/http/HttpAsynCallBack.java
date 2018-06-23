package org.raje.test.http;

import org.raje.test.common.AsynCallBack;
import org.raje.test.common.Result;
import org.springframework.stereotype.Component;


@Component
public class HttpAsynCallBack implements AsynCallBack<String> {

	@Override
	public Result callBack(String res) {
		return Result.SUCC;
	}

}
