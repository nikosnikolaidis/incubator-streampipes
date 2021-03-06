/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.streampipes.wrapper.flink;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.streampipes.dataformat.SpDataFormatDefinition;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.grounding.EventGrounding;
import org.apache.streampipes.model.grounding.KafkaTransportProtocol;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.apache.streampipes.wrapper.flink.converter.EventToMapConverter;
import org.apache.streampipes.wrapper.flink.serializer.ByteArraySerializer;
import org.apache.streampipes.wrapper.flink.sink.JmsFlinkProducer;
import org.apache.streampipes.wrapper.flink.sink.MqttFlinkProducer;
import org.apache.streampipes.wrapper.params.binding.EventProcessorBindingParams;
import org.apache.streampipes.wrapper.params.runtime.EventProcessorRuntimeParams;

import java.util.Map;
import java.util.Properties;
import java.util.UUID;

public abstract class FlinkDataProcessorRuntime<B extends EventProcessorBindingParams> extends
        FlinkRuntime<EventProcessorRuntimeParams<B>, B,
        DataProcessorInvocation, EventProcessorRuntimeContext> {

  private static final long serialVersionUID = 1L;

  /**
   * @deprecated Use {@link #FlinkDataProcessorRuntime(EventProcessorBindingParams, boolean)} instead
   */
  @Deprecated
  public FlinkDataProcessorRuntime(B params) {
    super(params);
  }

  public FlinkDataProcessorRuntime(B params, boolean debug) {
    super(params, debug);
  }

  /**
   * @deprecated Use {@link #FlinkDataProcessorRuntime(EventProcessorBindingParams, boolean)} instead
   */
  public FlinkDataProcessorRuntime(B params, FlinkDeploymentConfig config) {
    super(params, config);
  }

  @SuppressWarnings("deprecation")
  public void appendExecutionConfig(DataStream<Event>... convertedStream) {
    DataStream<Map<String, Object>> applicationLogic = getApplicationLogic(convertedStream).flatMap
            (new EventToMapConverter());

    EventGrounding outputGrounding = getOutputStream().getEventGrounding();
    SpDataFormatDefinition outputDataFormatDefinition =
            getDataFormatDefinition(outputGrounding.getTransportFormats().get(0));

    ByteArraySerializer serializer =
            new ByteArraySerializer(outputDataFormatDefinition);
    if (isKafkaProtocol(getOutputStream())) {
      applicationLogic
              .addSink(new FlinkKafkaProducer<>(getTopic(getOutputStream()),
                      serializer,
                      getProducerProperties((KafkaTransportProtocol) outputGrounding.getTransportProtocol())));
    } else if (isJmsProtocol(getOutputStream())) {
      applicationLogic
              .addSink(new JmsFlinkProducer(getJmsProtocol(getOutputStream()), serializer));
    } else if (isMqttProtocol(getOutputStream())) {
      applicationLogic
              .addSink(new MqttFlinkProducer(getMqttProtocol(getOutputStream()), serializer));
    }

  }

  private SpDataStream getOutputStream() {
    return getGraph().getOutputStream();
  }

  protected abstract DataStream<Event> getApplicationLogic(DataStream<Event>... messageStream);

  protected Properties getProperties(KafkaTransportProtocol protocol) {
    Properties props = new Properties();

    String kafkaHost = protocol.getBrokerHostname();
    Integer kafkaPort = protocol.getKafkaPort();

    props.put("client.id", UUID.randomUUID().toString());
    props.put("bootstrap.servers", kafkaHost + ":" + kafkaPort);
    return props;
  }

  protected EventProcessorRuntimeParams<B> makeRuntimeParams() {
    // TODO add support for config extractor & client
    return new EventProcessorRuntimeParams<>(bindingParams, false, null, null);
  }
}
