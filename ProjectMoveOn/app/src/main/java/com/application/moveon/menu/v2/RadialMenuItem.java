/*
 * Copyright (C) 2012 
 * Arindam Nath (strider2023@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.application.moveon.menu.v2;

import com.application.moveon.menu.v2.RadialMenuRenderer.OnRadialMenuClick;

/** 
 * @author Arindam Nath (strider2023@gmail.com)
 */
public class RadialMenuItem {

	private String mMenuID;
	private String mMenuName;
	private OnRadialMenuClick mCallback;
	
	/**
	 * @param mMenuID
	 * @param mMenuName
	 */
	public RadialMenuItem(String mMenuID, String mMenuName) {
		this.mMenuID = mMenuID;
		this.mMenuName = mMenuName;
	}

	/**
	 * @return the mMenuID
	 */
	public String getMenuID() {
		return mMenuID;
	}
	
	/**
	 * @return the mMenuName
	 */
	public String getMenuName() {
		return mMenuName;
	}
	
	/**
	 * 
	 * @param OnRadialMenuClick
	 */
	public void setOnRadialMenuClickListener(OnRadialMenuClick onRadialMenuClick) {
		this.mCallback = onRadialMenuClick;
	}
	
	/**
	 * 
	 * @return
	 */
	public OnRadialMenuClick getOnRadailMenuClick() {
		return mCallback;
	}
}
