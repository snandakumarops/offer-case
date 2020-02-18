package com.redhat.offermanagement.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.elasticsearch.ElasticsearchComponent;
import org.apache.camel.component.http4.HttpComponent;
import org.apache.camel.component.kafka.KafkaComponent;
import org.apache.camel.util.jsse.KeyStoreParameters;
import org.apache.camel.util.jsse.SSLContextParameters;
import org.apache.camel.util.jsse.TrustManagersParameters;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthenticationStrategy;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.*;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.nio.conn.SchemeIOSessionStrategy;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;

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
