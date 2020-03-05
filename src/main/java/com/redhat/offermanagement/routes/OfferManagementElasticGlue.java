package com.redhat.offermanagement.routes;

import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class OfferManagementElasticGlue extends RouteBuilder {

	private static final Logger LOG = LoggerFactory.getLogger(OfferManagementElasticGlue.class);

	private String kafkaBootstrap = "my-cluster-kafka-bootstrap:9092";
	private String offerTopic = "offer-output";
	private String consumerMaxPollRecords = "500";
	private String consumerCount = "1";
	private String consumerSeekTo = "beginning";
	private String consumerGroup = "case-glue";

	@Override
	public void configure() throws Exception {
		try {



			from("kafka:" + offerTopic + "?brokers=" + kafkaBootstrap + "&maxPollRecords="
					+ consumerMaxPollRecords + "&consumersCount=" + consumerCount + "&seekTo=" + consumerSeekTo
					+ "&groupId=" + consumerGroup
			)

					.bean(Transformer.class,"txnTransform")
					.log("${body}")
					.setHeader("Authorization",simple("Basic cGFtQWRtaW46cmVkaGF0cGFtMSE="))
					.to("rest:post:/services/rest/server/containers/OfferMgmtCase_1.0.0-SNAPSHOT/cases/DisputeMgmtCase.OfferManagementCase/instances?host=" + System.getenv("bcurl") + "&produces=application/json");;


		}catch (Exception e) {
			e.printStackTrace();
		}


	}


}
