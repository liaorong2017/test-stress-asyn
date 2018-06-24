package org.raje.test.request;

import org.raje.test.common.Result;

public interface AsynCallBack<T> {

	public Result callBack(T res);

}
