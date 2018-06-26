package org.raje.test.http;

import org.raje.test.common.Result;
import org.springframework.stereotype.Component;

@Component
public class HttpAsynCallBack {

	public Result callBack(String res) {
		return Result.SUCC;
	}

}
