package org.raje.test.http;

import org.raje.test.request.RequestProducer;
import org.springframework.stereotype.Component;


@Component
public class HttpRequestProducer implements RequestProducer<String> {

	@Override
	public String producerRequest() {
		return "avgCost:431, succ:38156, fail:0, succRate:100 , TPS:7631, -----[adj--avgCost:434,tps:7692,maxTps:7692,connect:3273]avgCost:431, succ:38156, fail:0, succRate:100 , TPS:7631, -----[adj--avgCost:434,tps:7692,maxTps:7692,connect:3273]";
	}

}
