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

package org.apache.streampipes.model.datalake;

import org.apache.streampipes.model.base.UnnamedStreamPipesEntity;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.model.shared.annotation.TsModel;

@TsModel
public class DataLakeMeasure extends UnnamedStreamPipesEntity {

    private String measureName;
    private EventSchema eventSchema;
    private String pipelineId;
    private String pipelineName;
    private boolean pipelineIsRunning;

    public DataLakeMeasure() {
        super();
    }

    public DataLakeMeasure(DataLakeMeasure other) {
        super(other);
        this.measureName = other.getMeasureName();
        this.eventSchema = new EventSchema(other.getEventSchema());

    }

    public DataLakeMeasure(String measureName, EventSchema eventSchema) {
        this.measureName = measureName;
        this.eventSchema = eventSchema;
    }

    public String getMeasureName() {
        return measureName;
    }

    public void setMeasureName(String measureName) {
        this.measureName = measureName;
    }

    public EventSchema getEventSchema() {
        return eventSchema;
    }

    public void setEventSchema(EventSchema eventSchema) {
        this.eventSchema = eventSchema;
    }

    public String getPipelineId() {
        return pipelineId;
    }

    public void setPipelineId(String pipelineId) {
        this.pipelineId = pipelineId;
    }

    public String getPipelineName() {
        return pipelineName;
    }

    public void setPipelineName(String pipelineName) {
        this.pipelineName = pipelineName;
    }

    public boolean isPipelineIsRunning() {
        return pipelineIsRunning;
    }

    public void setPipelineIsRunning(boolean pipelineIsRunning) {
        this.pipelineIsRunning = pipelineIsRunning;
    }
}
