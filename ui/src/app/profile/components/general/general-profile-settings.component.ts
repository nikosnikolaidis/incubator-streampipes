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

import {Component, OnInit} from "@angular/core";
import {ProfileService} from "../../profile.service";
import {User} from "../../../core-model/gen/streampipes-model-client";
import {BasicProfileSettings} from "../basic-profile-settings";
import {AppConstants} from "../../../services/app.constants";
import {AuthStatusService} from "../../../services/auth-status.service";

@Component({
  selector: 'general-profile-settings',
  templateUrl: './general-profile-settings.component.html',
  styleUrls: ['./general-profile-settings.component.scss']
})
export class GeneralProfileSettingsComponent extends BasicProfileSettings implements OnInit {

  darkMode: boolean = false;
  originalDarkMode: boolean = false;
  darkModeChanged: boolean = false;

  constructor(private authStatusService: AuthStatusService,
              profileService: ProfileService,
              appConstants: AppConstants) {
    super(profileService, appConstants);
  }

  ngOnInit(): void {
    this.darkMode = this.authStatusService.darkMode;
    this.originalDarkMode = this.authStatusService.darkMode;
    this.receiveUserData();
  }

  ngOnDestroy(): void {
    if (!this.darkModeChanged) {
      this.authStatusService.darkMode = this.originalDarkMode;
    }
  }

  changeModePreview(value: boolean) {
    this.authStatusService.darkMode = value;
  }

  onUserDataReceived() {
  }

  updateAppearanceMode() {
    this.profileService.updateAppearanceMode(this.darkMode).subscribe(response => {
      this.darkModeChanged = true;
    });
  }

}
