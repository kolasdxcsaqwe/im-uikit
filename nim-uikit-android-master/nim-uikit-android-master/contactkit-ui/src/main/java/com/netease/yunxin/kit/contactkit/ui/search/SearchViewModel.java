// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

package com.netease.yunxin.kit.contactkit.ui.search;

import android.text.TextUtils;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.friend.model.Friend;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.MsgSearchOption;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.netease.yunxin.kit.alog.ALog;
import com.netease.yunxin.kit.chatkit.model.FriendSearchInfo;
import com.netease.yunxin.kit.chatkit.model.TeamSearchInfo;
import com.netease.yunxin.kit.chatkit.repo.ChatRepo;
import com.netease.yunxin.kit.chatkit.repo.SearchRepo;
import com.netease.yunxin.kit.common.ui.viewholder.BaseBean;
import com.netease.yunxin.kit.common.ui.viewmodel.BaseViewModel;
import com.netease.yunxin.kit.common.ui.viewmodel.FetchResult;
import com.netease.yunxin.kit.common.ui.viewmodel.LoadStatus;
import com.netease.yunxin.kit.contactkit.ui.ContactConstant;
import com.netease.yunxin.kit.contactkit.ui.R;
import com.netease.yunxin.kit.contactkit.ui.model.P2PChatHistoryBean;
import com.netease.yunxin.kit.contactkit.ui.model.SearchFriendBean;
import com.netease.yunxin.kit.contactkit.ui.model.SearchMoreBean;
import com.netease.yunxin.kit.contactkit.ui.model.SearchTeamBean;
import com.netease.yunxin.kit.contactkit.ui.model.SearchTitleBean;
import com.netease.yunxin.kit.corekit.im.provider.FetchCallback;
import java.util.ArrayList;
import java.util.List;

/** to provider search data and operation */
public class SearchViewModel extends BaseViewModel {

  private static final String TAG = "SearchViewModel";
  private static final String LIB_TAG = "SearchKit-UI";
  private final MutableLiveData<FetchResult<List<BaseBean>>> queryLiveData =
      new MutableLiveData<>();

  private final List<BaseBean> resultList = new ArrayList<>();

  public MutableLiveData<FetchResult<List<BaseBean>>> getQueryLiveData() {
    return queryLiveData;
  }

  protected String routerFriend;
  protected String routerTeam;

  public void setRouter(String friend, String team) {
    routerFriend = friend;
    routerTeam = team;
  }

  public void query(boolean isDetailList,String text,int...types) {

    resultList.clear();
    if (!TextUtils.isEmpty(text) && types!=null) {

        for (int i = 0; i < types.length; i++) {
            switch (types[i])
            {
                case ContactConstant.SearchViewType.USER:
                    searchUser(text,isDetailList);
                    break;
                case ContactConstant.SearchViewType.ChatHistory:
                    searchChat(text,isDetailList);
                    break;
            }
        }

//      SearchRepo.searchGroup(
//          text,
//          new FetchCallback<List<TeamSearchInfo>>() {
//            @Override
//            public void onSuccess(@Nullable List<TeamSearchInfo> param) {
//              if (param != null && param.size() > 0) {
//                List<BaseBean> groupResult = new ArrayList<>();
//                groupResult.add(new SearchTitleBean(R.string.global_search_group_title));
//                for (int index = 0; index < param.size(); index++) {
//                  groupResult.add(new SearchTeamBean(param.get(index), routerTeam));
//                }
//                ALog.d(LIB_TAG, TAG, "searchTeamm,onSuccess,team:" + param.size());
//                resultList.addAll(groupResult);
//              }
//              FetchResult<List<BaseBean>> fetchResult = new FetchResult<>(LoadStatus.Success);
//              fetchResult.setData(resultList);
//              queryLiveData.postValue(fetchResult);
//            }
//
//            @Override
//            public void onFailed(int code) {
//              ALog.d(LIB_TAG, TAG, "searchFriend,onFailed:" + code);
//            }
//
//            @Override
//            public void onException(@Nullable Throwable exception) {
//              ALog.d(LIB_TAG, TAG, "searchFriend:onException");
//            }
//          });
//
//      SearchRepo.searchTeam(
//          text,
//          new FetchCallback<List<TeamSearchInfo>>() {
//            @Override
//            public void onSuccess(@Nullable List<TeamSearchInfo> param) {
//              if (param != null && param.size() > 0) {
//                List<BaseBean> friendResult = new ArrayList<>();
//                friendResult.add(new SearchTitleBean(R.string.global_search_team_title));
//                for (int index = 0; index < param.size(); index++) {
//                  friendResult.add(new SearchTeamBean(param.get(index), routerTeam));
//                }
//                resultList.addAll(friendResult);
//                ALog.d(LIB_TAG, TAG, "searchTeam,onSuccess:" + friendResult.size());
//              }
//              FetchResult<List<BaseBean>> fetchResult = new FetchResult<>(LoadStatus.Success);
//              fetchResult.setData(resultList);
//              queryLiveData.postValue(fetchResult);
//            }
//
//            @Override
//            public void onFailed(int code) {
//              ALog.d(LIB_TAG, TAG, "searchFriend,onFailed:" + code);
//            }
//
//            @Override
//            public void onException(@Nullable Throwable exception) {
//              ALog.d(LIB_TAG, TAG, "searchFriend,onException");
//            }
//          });





    } else {
      FetchResult<List<BaseBean>> fetchResult = new FetchResult<>(LoadStatus.Success);
      fetchResult.setData(resultList);
      queryLiveData.postValue(fetchResult);
    }
  }


  private void searchChat(String text,boolean isDetailList)
  {
      MsgSearchOption msgSearchOption=new MsgSearchOption();
      msgSearchOption.setSearchContent(text);
      msgSearchOption.setLimit(4);

      NIMClient.getService(MsgService.class).searchAllMessage(msgSearchOption).setCallback(new RequestCallbackWrapper<List<IMMessage>>() {
          @Override
          public void onResult(int code, List<IMMessage> result, Throwable exception) {
              if(result!=null)
              {
                  resultList.add(new SearchTitleBean(R.string.chat_history));
                  boolean isMoreThan3=result.size()>3;
                  int count=isMoreThan3?3:result.size();
                  if(isDetailList)
                  {
                      count=result.size();
                  }
                  for (int i = 0; i < count; i++) {
                      P2PChatHistoryBean p2PChatHistoryBean=new P2PChatHistoryBean();
                      p2PChatHistoryBean.setImMessage(result.get(i));
                      NimUserInfo nimUserInfo = NIMClient.getService(UserService.class).getUserInfo(result.get(i).getFromAccount());
                      Friend friend = NIMClient.getService(FriendService.class).getFriendByAccount(result.get(i).getFromAccount());

                      p2PChatHistoryBean.setFriend(friend);
                      p2PChatHistoryBean.setNimUserInfo(nimUserInfo);
                      p2PChatHistoryBean.setType(ContactConstant.SearchViewType.ChatHistory);
                      resultList.add(p2PChatHistoryBean);
                  }
                  if(isMoreThan3 && !isDetailList)
                  {
                      resultList.add(new SearchMoreBean(R.string.more_chat,ContactConstant.SearchViewType.ChatHistory,text));
                  }

                  FetchResult<List<BaseBean>> fetchResult = new FetchResult<>(LoadStatus.Success);
                  fetchResult.setData(resultList);
                  queryLiveData.postValue(fetchResult);
              }
          }
      });
  }


  private void searchUser(String text,boolean isDetailList)
  {
      SearchRepo.searchFriend(
              text,
              new FetchCallback<List<FriendSearchInfo>>() {
                  @Override
                  public void onSuccess(@Nullable List<FriendSearchInfo> param) {
                      if (param != null && param.size() > 0) {
                          List<BaseBean> friendResult = new ArrayList<>();
                          friendResult.add(new SearchTitleBean(R.string.global_search_friend_title));
                          ALog.d(LIB_TAG, TAG, "searchFriend,onSuccess,friend");
                          boolean isMoreThan3=param.size()>3;
                          int count=isMoreThan3?3:param.size();

                          if(isDetailList)
                          {
                              count=param.size();
                          }

                          for (int index = 0; index < count; index++) {
                              friendResult.add(new SearchFriendBean(param.get(index), routerFriend));
                              ALog.d(
                                      LIB_TAG,
                                      TAG,
                                      "searchFriend,onSuccess:" + param.get(index).getFriendInfo().getName());
                          }

                          if(isMoreThan3 && !isDetailList)
                          {
                              friendResult.add(new SearchMoreBean(R.string.more_contact,ContactConstant.SearchViewType.USER,text));
                          }
                          resultList.addAll(0, friendResult);


                      }


                      FetchResult<List<BaseBean>> fetchResult = new FetchResult<>(LoadStatus.Success);
                      fetchResult.setData(resultList);
                      queryLiveData.postValue(fetchResult);
                  }

                  @Override
                  public void onFailed(int code) {
                      ALog.d(LIB_TAG, TAG, "searchFriend,onFailed:" + code);
                  }

                  @Override
                  public void onException(@Nullable Throwable exception) {
                      ALog.d(LIB_TAG, TAG, "searchFriend,onException");
                  }
              });
  }
}
