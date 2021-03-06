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
package org.apache.streampipes.commons.constants;

public enum Envs {

  SP_HOST("SP_HOST"),
  SP_PORT("SP_PORT"),
  SP_CONSUL_LOCATION("CONSUL_LOCATION"),
  SP_KAFKA_RETENTION_MS("SP_KAFKA_RETENTION_MS");

  private final String envVariableName;

  Envs(String envVariableName) {
    this.envVariableName = envVariableName;
  }

  public boolean exists() {
    return CustomEnvs.exists(this.envVariableName);
  }

  public String getValue() {
    return CustomEnvs.getEnv(this.envVariableName);
  }

  public Integer getValueAsInt() {
    return CustomEnvs.getEnvAsInt(this.envVariableName);
  }

  public Boolean getValueAsBoolean() {
    return CustomEnvs.getEnvAsBoolean(this.envVariableName);
  }

}
