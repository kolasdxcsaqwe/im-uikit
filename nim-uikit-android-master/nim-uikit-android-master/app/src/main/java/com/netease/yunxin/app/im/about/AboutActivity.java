// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

package com.netease.yunxin.app.im.about;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.netease.yunxin.app.im.R;
import com.netease.yunxin.app.im.databinding.ActivityAboutBinding;
import com.netease.yunxin.app.im.utils.AppUtils;
import com.netease.yunxin.app.im.utils.SPUtils;
import com.netease.yunxin.kit.common.ui.activities.BrowseActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class AboutActivity extends AppCompatActivity {

  private ActivityAboutBinding viewBinding;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    viewBinding = ActivityAboutBinding.inflate(getLayoutInflater());
    setContentView(viewBinding.getRoot());
    viewBinding.aboutTitleBar.setOnBackIconClickListener(v -> onBackPressed());
    viewBinding.aboutTitleBar.setTitle(getString(R.string.mine_about));
    String detail=SPUtils.getInstance().get(SPUtils.ConfigData,"");
    try {
      JSONObject jsonObject=new JSONObject(detail);
      String content=jsonObject.optString("about","");
      viewBinding.webView.loadData(content,"text/html; charset=UTF-8",null);
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }

  }
}
