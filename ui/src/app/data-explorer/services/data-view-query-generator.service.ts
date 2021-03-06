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

import { Injectable } from '@angular/core';
import { DataExplorerDataConfig, SourceConfig } from '../models/dataview-dashboard.model';
import { DatalakeQueryParameterBuilder } from '../../core-services/datalake/DatalakeQueryParameterBuilder';
import { Observable } from 'rxjs';
import { DatalakeQueryParameters } from '../../core-services/datalake/DatalakeQueryParameters';
import { DatalakeRestService } from '../../platform-services/apis/datalake-rest.service';
import { SpQueryResult } from '../../core-model/gen/streampipes-model';


@Injectable()
export class DataViewQueryGeneratorService {

  constructor(protected dataLakeRestService: DatalakeRestService) {

  }

  generateObservables(startTime: number,
                      endTime: number,
                      dataConfig: DataExplorerDataConfig): Observable<SpQueryResult>[] {

    return dataConfig
      .sourceConfigs
      .map(sourceConfig => this.dataLakeRestService
        .getData(sourceConfig.measureName, this.generateQuery(startTime, endTime, sourceConfig)));
  }

  generateQuery(startTime: number,
                endTime: number,
                sourceConfig: SourceConfig): DatalakeQueryParameters {
    const queryBuilder = DatalakeQueryParameterBuilder.create(startTime, endTime);
    const queryConfig = sourceConfig.queryConfig;

    queryBuilder.withColumnFilter(
      queryConfig.fields.filter(f => f.selected),
      sourceConfig.queryType === 'aggregated' || sourceConfig.queryType === 'single'
    );

    if (sourceConfig.queryConfig.groupBy !== undefined) {
      const selectedGroupByFields = sourceConfig.queryConfig.groupBy.filter(field => field.selected);
      if (selectedGroupByFields.length > 0) {
        queryBuilder.withGrouping(sourceConfig.queryConfig.groupBy);
      }
    }

    if (queryConfig.selectedFilters.length > 0) {
      queryBuilder.withFilters(queryConfig.selectedFilters);
    }

    if (sourceConfig.queryType === 'single') {
      queryBuilder.withLimit(1);
    } else if (sourceConfig.queryType === 'raw') {
      // raw query with paging
      queryBuilder.withPaging(queryConfig.page - 1, queryConfig.limit);
    } else {
      // aggregated query
      if (queryConfig.autoAggregate) {
        queryBuilder.withAutoAggregation();
      } else {
        queryBuilder.withAggregation(queryConfig.aggregationTimeUnit, queryConfig.aggregationValue);
      }
    }

    return queryBuilder.build();
  }
}
