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

import { Component, Input } from '@angular/core';
import { FieldConfig, SelectedFilter, SourceConfig } from '../../../../models/dataview-dashboard.model';
import { WidgetConfigurationService } from '../../../../services/widget-configuration.service';

@Component({
  selector: 'sp-filter-selection-panel',
  templateUrl: './filter-selection-panel.component.html',
  styleUrls: ['./filter-selection-panel.component.scss']
})
export class FilterSelectionPanelComponent {

  @Input() sourceConfig: SourceConfig;
  @Input() widgetId: string;

  constructor(private widgetConfigService: WidgetConfigurationService) {
  }

  addFilter() {
    const newFilter: SelectedFilter = {
      index: this.sourceConfig.queryConfig.selectedFilters.length,
      operator: '=',
      value: ''
    };
    this.sourceConfig.queryConfig.selectedFilters.push(newFilter);
    this.widgetConfigService.notify({ widgetId: this.widgetId, refreshData: true, refreshView: true });
    this.updateWidget();
  }

  remove(sourceConfig: any, index: number) {
    sourceConfig.queryConfig.selectedFilters.splice(index, 1);
    this.updateWidget();
  }

  updateWidget() {
    let update = true;
    this.sourceConfig.queryConfig.selectedFilters.forEach(filter => {
      if (!filter.field || !filter.value || !filter.operator) {
        update = false;
      }
    });

    if (update) {
      this.widgetConfigService.notify({ widgetId: this.widgetId, refreshData: true, refreshView: true });
    }
  }

  compare(available: FieldConfig, selected: FieldConfig) {
    return (available.runtimeName === selected.runtimeName);
  }
}
