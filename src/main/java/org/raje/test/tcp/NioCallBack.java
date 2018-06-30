package org.raje.test.tcp;

import org.raje.test.common.Result;
import org.springframework.stereotype.Component;

@Component
public class NioCallBack {

	public Result callBack(String res) {
		return Result.SUCC;
	}

}
