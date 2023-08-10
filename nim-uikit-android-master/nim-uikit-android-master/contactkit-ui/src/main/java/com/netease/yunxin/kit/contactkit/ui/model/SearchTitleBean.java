// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

package com.netease.yunxin.kit.contactkit.ui.model;

import com.netease.yunxin.kit.common.ui.viewholder.BaseBean;
import com.netease.yunxin.kit.contactkit.ui.ContactConstant;

public class SearchTitleBean extends BaseBean {

  public String title;
  public int titleRes;

  public SearchTitleBean(String tt) {
    this.title = tt;
    this.viewType = ContactConstant.SearchViewType.TITLE;
  }

  public SearchTitleBean(int res) {
    this.titleRes = res;
    this.viewType = ContactConstant.SearchViewType.TITLE;
  }
}
