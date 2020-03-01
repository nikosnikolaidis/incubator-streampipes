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

import {RdfsClass} from '../../../platform-services/tsonld/RdfsClass';
import {RdfProperty} from '../../../platform-services/tsonld/RdfsProperty';
import {RdfId} from '../../../platform-services/tsonld/RdfId';
import {Float} from '../../../platform-services/tsonld/Float';

@RdfsClass('http://schema.org/QuantitativeValue')
export class QuantitativeValue {
    private static QuantitativeValue = -3994041794693686406;

    @RdfId
    public id: string;

    @RdfProperty('http://schema.org/minValue')
    @Float
    public minValue: Number;

    @RdfProperty('http://schema.org/maxValue')
    @Float
    public maxValue: Number;

    @RdfProperty('http://schema.org/step')
    @Float
    public step: Number;

    constructor() {}
}