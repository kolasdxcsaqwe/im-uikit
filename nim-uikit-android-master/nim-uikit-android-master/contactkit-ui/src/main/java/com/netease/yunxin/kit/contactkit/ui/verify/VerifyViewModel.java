// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

package com.netease.yunxin.kit.contactkit.ui.verify;

import static com.netease.yunxin.kit.contactkit.ui.ContactConstant.LIB_TAG;

import android.text.TextUtils;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import com.netease.yunxin.kit.alog.ALog;
import com.netease.yunxin.kit.chatkit.repo.ContactObserverRepo;
import com.netease.yunxin.kit.chatkit.repo.ContactRepo;
import com.netease.yunxin.kit.chatkit.repo.TeamRepo;
import com.netease.yunxin.kit.common.ui.viewmodel.BaseViewModel;
import com.netease.yunxin.kit.common.ui.viewmodel.FetchResult;
import com.netease.yunxin.kit.common.ui.viewmodel.LoadStatus;
import com.netease.yunxin.kit.contactkit.ui.model.ContactVerifyInfoBean;
import com.netease.yunxin.kit.corekit.im.model.SystemMessageInfo;
import com.netease.yunxin.kit.corekit.im.model.SystemMessageInfoStatus;
import com.netease.yunxin.kit.corekit.im.model.SystemMessageInfoType;
import com.netease.yunxin.kit.corekit.im.provider.FetchCallback;
import com.netease.yunxin.kit.corekit.im.provider.SystemMessageInfoObserver;
import java.util.ArrayList;
import java.util.List;

public class VerifyViewModel extends BaseViewModel {

  private final String TAG = "VerifyViewModel";
  private final int PAGE_LIMIT = 100;
  //7 day expire time
  private final long expireLimit = 604800000;
  private int index = 0;
  private boolean hasMore = true;
  private final MutableLiveData<FetchResult<List<ContactVerifyInfoBean>>> resultLiveData =
      new MutableLiveData<>();
  private final FetchResult<List<ContactVerifyInfoBean>> fetchResult =
      new FetchResult<>(LoadStatus.Finish);
  private final List<ContactVerifyInfoBean> verifyBeanList = new ArrayList<>();
  private final List<ContactVerifyInfoBean> updateList = new ArrayList<>();
  private final SystemMessageInfoObserver infoObserver;

  public MutableLiveData<FetchResult<List<ContactVerifyInfoBean>>> getFetchResult() {
    return resultLiveData;
  }

  public VerifyViewModel() {
    infoObserver =
        info -> {
          if (info.getId() > 0) {
            List<SystemMessageInfo> msgInfo = new ArrayList<>();
            msgInfo.add(info);
            ContactRepo.fillNotificationWithUserAndTeam(
                msgInfo,
                new FetchCallback<List<SystemMessageInfo>>() {
                  @Override
                  public void onSuccess(@Nullable List<SystemMessageInfo> param) {
                    ALog.d(
                        LIB_TAG,
                        TAG,
                        "infoObserver,onSuccess:" + (param == null ? "null" : param.size()));
                    List<ContactVerifyInfoBean> add = new ArrayList<>();
                    if (param != null && !param.isEmpty()) {
                      resetMessageStatus(param);
                      add = mergeSystemMessageList(param);
                      // 如果有新的消息合并，需要更新原有消息
                      if (updateList.size() > 0) {
                        List<ContactVerifyInfoBean> update = new ArrayList<>(updateList);
                        updateList.clear();
                        fetchResult.setData(update);
                        fetchResult.setFetchType(FetchResult.FetchType.Update);
                        resultLiveData.setValue(fetchResult);
                      }
                    }
                    //update
                    if (add.size() > 0) {
                      fetchResult.setData(add);
                      fetchResult.setFetchType(FetchResult.FetchType.Add);
                      resultLiveData.setValue(fetchResult);
                    }
                  }

                  @Override
                  public void onFailed(int code) {
                    ALog.d(LIB_TAG, TAG, "infoObserver,onFailed:" + code);
                    fetchResult.setError(code, "");
                    resultLiveData.setValue(fetchResult);
                  }

                  @Override
                  public void onException(@Nullable Throwable exception) {
                    ALog.d(LIB_TAG, TAG, "infoObserver,onException");
                    fetchResult.setError(-1, "");
                    resultLiveData.setValue(fetchResult);
                  }
                });
          }
        };
    ContactObserverRepo.registerNotificationObserver(infoObserver);
  }

  public void fetchVerifyList(boolean nextPage) {
    ALog.d(LIB_TAG, TAG, "fetchVerifyList,nextPage:" + nextPage);
    fetchResult.setStatus(LoadStatus.Loading);
    resultLiveData.postValue(fetchResult);
    if (nextPage) {
      if (!hasMore) {
        return;
      }
      index += PAGE_LIMIT;
    } else {
      index = 0;
    }
    ContactRepo.getNotificationList(
        index,
        PAGE_LIMIT,
        new FetchCallback<List<SystemMessageInfo>>() {
          @Override
          public void onSuccess(@Nullable List<SystemMessageInfo> param) {
            ALog.d(
                LIB_TAG,
                TAG,
                "fetchVerifyList,onSuccess:" + (param == null ? "null" : param.size()));
            fetchResult.setStatus(LoadStatus.Success);
            hasMore = (param != null && param.size() == PAGE_LIMIT);
            if (param != null && param.size() > 0) {
              resetMessageStatus(param);
              List<ContactVerifyInfoBean> add = mergeSystemMessageList(param);
              fetchResult.setData(add);
            } else {
              fetchResult.setData(null);
            }
            resultLiveData.setValue(fetchResult);
          }

          @Override
          public void onFailed(int code) {
            ALog.d(LIB_TAG, TAG, "fetchVerifyList,onFailed:" + code);
            fetchResult.setError(code, "");
            resultLiveData.setValue(fetchResult);
          }

          @Override
          public void onException(@Nullable Throwable exception) {
            ALog.d(LIB_TAG, TAG, "fetchVerifyList,onException");
            fetchResult.setError(-1, "");
            resultLiveData.setValue(fetchResult);
          }
        });
  }

  public boolean hasMore() {
    return hasMore;
  }

  public void clearNotify() {
    ALog.d(LIB_TAG, TAG, "clearNotify");
    ContactRepo.clearNotification();
    fetchResult.setFetchType(FetchResult.FetchType.Remove);
    fetchResult.setData(new ArrayList<>(verifyBeanList));
    verifyBeanList.clear();
    resultLiveData.setValue(fetchResult);
  }

  public void agree(ContactVerifyInfoBean bean, FetchCallback<Void> callback) {
    ALog.d(LIB_TAG, TAG, "agree:" + (bean == null ? "null" : bean.data.getId()));
    SystemMessageInfo info = bean.data;
    SystemMessageInfoType type = info.getInfoType();
    SystemMessageInfoStatus status = info.getInfoStatus();
    String account = info.getFromAccount();
    if (status == SystemMessageInfoStatus.Init && !TextUtils.isEmpty(account)) {
      if (type == SystemMessageInfoType.AddFriend) {
        ContactRepo.acceptAddFriend(account, true, callback);
      } else if (type == SystemMessageInfoType.ApplyJoinTeam) {
        TeamRepo.agreeTeamApply(info.getTargetId(), account, callback);
      } else if (type == SystemMessageInfoType.TeamInvite) {
        TeamRepo.acceptTeamInvite(info.getTargetId(), account, callback);
      }
    }
  }

  public void disagree(ContactVerifyInfoBean bean, FetchCallback<Void> callback) {
    ALog.d(LIB_TAG, TAG, "disagree:" + (bean == null ? "null" : bean.data.getId()));
    if (bean == null || bean.data == null) {
      return;
    }
    SystemMessageInfo info = bean.data;
    SystemMessageInfoType type = info.getInfoType();
    SystemMessageInfoStatus status = info.getInfoStatus();
    String account = info.getFromAccount();
    if (status == SystemMessageInfoStatus.Init && !TextUtils.isEmpty(account)) {
      if (type == SystemMessageInfoType.AddFriend) {
        ContactRepo.acceptAddFriend(info.getFromAccount(), false, callback);

      } else if (type == SystemMessageInfoType.ApplyJoinTeam
          && !TextUtils.isEmpty(info.getTargetId())) {
        TeamRepo.rejectTeamApply(info.getTargetId(), account, "", callback);

      } else if (type == SystemMessageInfoType.TeamInvite
          && !TextUtils.isEmpty(info.getTargetId())) {
        TeamRepo.rejectTeamInvite(info.getTargetId(), account, "", callback);
      }
    }
  }

  public void setVerifyStatus(ContactVerifyInfoBean bean, SystemMessageInfoStatus status) {
    if (bean == null || status == null) {
      return;
    }
    ALog.d(LIB_TAG, TAG, "setVerifyStatus:" + (status == null ? "null" : status.name()));
    for (SystemMessageInfo messageInfo : bean.messageList) {
      ContactRepo.setNotificationStatus(messageInfo.getId(), status);
    }
    bean.updateStatus(status);
  }

  public void resetUnreadCount() {
    ALog.d(LIB_TAG, TAG, "resetUnreadCount");
    ContactRepo.clearNotificationUnreadCount();
    for (ContactVerifyInfoBean verifyInfoBean : verifyBeanList) {
      verifyInfoBean.clearUnreadCount();
    }
  }

  private void resetMessageStatus(List<SystemMessageInfo> infoList) {
    ALog.d(LIB_TAG, TAG, "resetMessageStatus:" + (infoList == null ? "null" : infoList.size()));
    if (infoList != null && infoList.size() > 0) {
      long lastTime = System.currentTimeMillis() - expireLimit;
      for (SystemMessageInfo info : infoList) {
        if (info.getInfoStatus() == SystemMessageInfoStatus.Init && info.getTime() < lastTime) {
          info.setInfoStatus(SystemMessageInfoStatus.Expired);
        }
      }
    }
  }

  private List<ContactVerifyInfoBean> mergeSystemMessageList(List<SystemMessageInfo> infoList) {
    List<ContactVerifyInfoBean> add = new ArrayList<>();
    updateList.clear();
    if (infoList != null) {
      for (int index = 0; index < infoList.size(); index++) {
        boolean hasInsert = false;
        for (ContactVerifyInfoBean bean : verifyBeanList) {
          if (bean.pushMessageIfSame(infoList.get(index))) {
            hasInsert = true;
            updateList.add(bean);
            break;
          }
        }
        if (!hasInsert) {
          ContactVerifyInfoBean infoBean = new ContactVerifyInfoBean(infoList.get(index));
          verifyBeanList.add(infoBean);
          add.add(infoBean);
        }
      }
    }
    return add;
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    ContactObserverRepo.unregisterNotificationObserver(infoObserver);
  }
}
