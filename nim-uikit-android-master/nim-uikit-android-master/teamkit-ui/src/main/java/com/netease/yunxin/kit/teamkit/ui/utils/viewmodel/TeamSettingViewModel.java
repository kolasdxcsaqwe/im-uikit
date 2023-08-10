// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

package com.netease.yunxin.kit.teamkit.ui.utils.viewmodel;

import android.text.TextUtils;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.StickTopSessionInfo;
import com.netease.nimlib.sdk.team.constant.TeamInviteModeEnum;
import com.netease.nimlib.sdk.team.constant.TeamTypeEnum;
import com.netease.nimlib.sdk.team.constant.TeamUpdateModeEnum;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.team.model.TeamMember;
import com.netease.yunxin.kit.alog.ALog;
import com.netease.yunxin.kit.chatkit.model.TeamWithCurrentMember;
import com.netease.yunxin.kit.chatkit.model.UserInfoWithTeam;
import com.netease.yunxin.kit.chatkit.repo.ConversationRepo;
import com.netease.yunxin.kit.chatkit.repo.TeamRepo;
import com.netease.yunxin.kit.common.ui.viewmodel.BaseViewModel;
import com.netease.yunxin.kit.corekit.event.EventCenter;
import com.netease.yunxin.kit.corekit.im.IMKitClient;
import com.netease.yunxin.kit.corekit.im.custom.TeamEvent;
import com.netease.yunxin.kit.corekit.im.custom.TeamEventAction;
import com.netease.yunxin.kit.corekit.im.provider.FetchCallback;
import com.netease.yunxin.kit.corekit.model.ErrorMsg;
import com.netease.yunxin.kit.corekit.model.ResultInfo;
import java.util.List;
import java.util.Objects;

public class TeamSettingViewModel extends BaseViewModel {
  private static final String TAG = "TeamSettingViewModel";
  private static final String LIB_TAG = "TeamKit-UI";
  private final MutableLiveData<ResultInfo<TeamWithCurrentMember>> teamWithMemberData =
      new MutableLiveData<>();
  private final MutableLiveData<ResultInfo<List<UserInfoWithTeam>>> userInfoData =
      new MutableLiveData<>();
  private final MutableLiveData<ResultInfo<String>> nameData = new MutableLiveData<>();
  private final MutableLiveData<ResultInfo<String>> introduceData = new MutableLiveData<>();
  private final MutableLiveData<ResultInfo<String>> nicknameData = new MutableLiveData<>();
  private final MutableLiveData<ResultInfo<String>> iconData = new MutableLiveData<>();
  private final MutableLiveData<ResultInfo<Void>> quitTeamData = new MutableLiveData<>();
  private final MutableLiveData<ResultInfo<Void>> dismissTeamData = new MutableLiveData<>();
  private final MutableLiveData<ResultInfo<Integer>> updateInvitePrivilegeData =
      new MutableLiveData<>();
  private final MutableLiveData<ResultInfo<Integer>> updateInfoPrivilegeData =
      new MutableLiveData<>();
  private final MutableLiveData<ResultInfo<Boolean>> muteTeamData = new MutableLiveData<>();
  private final MutableLiveData<ResultInfo<Boolean>> beInvitedNeedAgreedData =
      new MutableLiveData<>();
  private final MutableLiveData<ResultInfo<Boolean>> stickData = new MutableLiveData<>();
  private final MutableLiveData<ResultInfo<List<String>>> addMembersData = new MutableLiveData<>();
  private final MutableLiveData<ResultInfo<Boolean>> muteTeamAllMemberData =
      new MutableLiveData<>();

  public void requestTeamData(String teamId) {
    ALog.d(LIB_TAG, TAG, "requestTeamData:" + teamId);
    TeamRepo.queryTeamWithMember(
        teamId,
        Objects.requireNonNull(IMKitClient.account()),
        new FetchCallback<TeamWithCurrentMember>() {
          @Override
          public void onSuccess(@Nullable TeamWithCurrentMember param) {
            ALog.d(LIB_TAG, TAG, "requestTeamData,onSuccess:" + (param == null));
            teamWithMemberData.postValue(new ResultInfo<>(param));
          }

          @Override
          public void onFailed(int code) {
            ALog.d(LIB_TAG, TAG, "requestTeamData,onFailed:" + code);
            teamWithMemberData.postValue(new ResultInfo<>(null, false, new ErrorMsg(code)));
          }

          @Override
          public void onException(@Nullable Throwable exception) {
            ALog.d(LIB_TAG, TAG, "requestTeamData,onException");
            teamWithMemberData.postValue(
                new ResultInfo<>(null, false, new ErrorMsg(-1, "", exception)));
          }
        });
  }

  public void requestTeamMembers(String teamId) {
    ALog.d(LIB_TAG, TAG, "requestTeamMembers:" + teamId);
    TeamRepo.getMemberList(
        teamId,
        new FetchCallback<List<UserInfoWithTeam>>() {
          @Override
          public void onSuccess(@Nullable List<UserInfoWithTeam> param) {
            ALog.d(
                LIB_TAG,
                TAG,
                "requestTeamMembers,onSuccess:" + (param == null ? "null" : param.size()));
            userInfoData.postValue(new ResultInfo<>(param));
          }

          @Override
          public void onFailed(int code) {
            ALog.d(LIB_TAG, TAG, "requestTeamMembers,onFailed:" + code);
            userInfoData.postValue(new ResultInfo<>(null, false, new ErrorMsg(code)));
          }

          @Override
          public void onException(@Nullable Throwable exception) {
            ALog.d(LIB_TAG, TAG, "requestTeamMembers,onException");
            userInfoData.postValue(new ResultInfo<>(null, false, new ErrorMsg(-1, "", exception)));
          }
        });
  }

  public void updateName(String teamId, String name) {
    ALog.d(LIB_TAG, TAG, "updateName:" + teamId);
    TeamRepo.updateTeamName(
        teamId,
        name,
        new FetchCallback<Void>() {
          @Override
          public void onSuccess(@Nullable Void param) {
            ALog.d(LIB_TAG, TAG, "updateName,onSuccess");
            nameData.postValue(new ResultInfo<>(name));
          }

          @Override
          public void onFailed(int code) {
            ALog.d(LIB_TAG, TAG, "updateName,onFailed:" + code);
            nameData.postValue(new ResultInfo<>(null, false, new ErrorMsg(code)));
          }

          @Override
          public void onException(@Nullable Throwable exception) {
            ALog.d(LIB_TAG, TAG, "updateName,onException");
            nameData.postValue(new ResultInfo<>(null, false, new ErrorMsg(-1, "", exception)));
          }
        });
  }

  public void updateIntroduce(String teamId, String introduce) {
    ALog.d(LIB_TAG, TAG, "updateIntroduce:" + teamId);
    TeamRepo.updateTeamIntroduce(
        teamId,
        introduce,
        new FetchCallback<Void>() {
          @Override
          public void onSuccess(@Nullable Void param) {
            ALog.d(LIB_TAG, TAG, "updateIntroduce,onSuccess");
            introduceData.postValue(new ResultInfo<>(introduce));
          }

          @Override
          public void onFailed(int code) {
            ALog.d(LIB_TAG, TAG, "updateIntroduce,onFailed:" + code);
            introduceData.postValue(new ResultInfo<>(null, false, new ErrorMsg(code)));
          }

          @Override
          public void onException(@Nullable Throwable exception) {
            ALog.d(LIB_TAG, TAG, "updateIntroduce,onException");
            introduceData.postValue(new ResultInfo<>(null, false, new ErrorMsg(-1, "", exception)));
          }
        });
  }

  public void updateNickname(String teamId, String nickname) {
    ALog.d(LIB_TAG, TAG, "updateNickname:" + teamId);
    TeamRepo.updateMemberNick(
        teamId,
        Objects.requireNonNull(IMKitClient.account()),
        nickname,
        new FetchCallback<Void>() {
          @Override
          public void onSuccess(@Nullable Void param) {
            ALog.d(LIB_TAG, TAG, "updateNickname,onSuccess");
            nicknameData.postValue(new ResultInfo<>(nickname));
          }

          @Override
          public void onFailed(int code) {
            ALog.d(LIB_TAG, TAG, "updateNickname,onFailed:" + code);
            nicknameData.postValue(new ResultInfo<>(null, false, new ErrorMsg(code)));
          }

          @Override
          public void onException(@Nullable Throwable exception) {
            ALog.d(LIB_TAG, TAG, "updateNickname,onException");
            nicknameData.postValue(new ResultInfo<>(null, false, new ErrorMsg(-1, "", exception)));
          }
        });
  }

  public void updateIcon(String teamId, String iconUrl) {
    ALog.d(LIB_TAG, TAG, "updateIcon:" + teamId);
    TeamRepo.updateTeamIcon(
        teamId,
        iconUrl,
        new FetchCallback<Void>() {
          @Override
          public void onSuccess(@Nullable Void param) {
            ALog.d(LIB_TAG, TAG, "updateIcon,onSuccess");
            iconData.postValue(new ResultInfo<>(iconUrl));
          }

          @Override
          public void onFailed(int code) {
            ALog.d(LIB_TAG, TAG, "updateIcon,onFailed:" + code);
            iconData.postValue(new ResultInfo<>(null, false, new ErrorMsg(code)));
          }

          @Override
          public void onException(@Nullable Throwable exception) {
            ALog.d(LIB_TAG, TAG, "updateIcon,onException");
            iconData.postValue(new ResultInfo<>(null, false, new ErrorMsg(-1, "", exception)));
          }
        });
  }

  public void quitTeam(Team team) {
    ALog.d(LIB_TAG, TAG, "quitTeam:" + team.getId());
    if (team.getType() == TeamTypeEnum.Advanced
        && TextUtils.equals(team.getCreator(), IMKitClient.account())) {
      TeamRepo.getMemberList(
          team.getId(),
          new FetchCallback<List<UserInfoWithTeam>>() {
            @Override
            public void onSuccess(@Nullable List<UserInfoWithTeam> param) {
              String account = IMKitClient.account();
              if (param == null || param.size() <= 1) {
                dismissTeam(team.getId());
                return;
              }
              for (UserInfoWithTeam user : param) {
                if (!TextUtils.equals(account, user.getTeamInfo().getAccount())) {
                  account = user.getTeamInfo().getAccount();
                  break;
                }
              }
              TeamRepo.transferTeam(
                  team.getId(),
                  account,
                  true,
                  new FetchCallback<List<TeamMember>>() {
                    @Override
                    public void onSuccess(@Nullable List<TeamMember> param) {
                      quitTeam(team.getId());
                    }

                    @Override
                    public void onFailed(int code) {
                      ALog.d(LIB_TAG, TAG, "quitTeam transferTeam,onFailed:" + code);
                      quitTeamData.postValue(new ResultInfo<>(null, false, new ErrorMsg(code)));
                    }

                    @Override
                    public void onException(@Nullable Throwable exception) {
                      ALog.d(LIB_TAG, TAG, "quitTeam transferTeam,onException");
                      quitTeamData.postValue(
                          new ResultInfo<>(null, false, new ErrorMsg(-1, "", exception)));
                    }
                  });
            }

            @Override
            public void onFailed(int code) {
              ALog.d(LIB_TAG, TAG, "quitTeam  getMemberList,onFailed:" + code);
              quitTeamData.postValue(new ResultInfo<>(null, false, new ErrorMsg(code)));
            }

            @Override
            public void onException(@Nullable Throwable exception) {
              ALog.d(LIB_TAG, TAG, "quitTeam getMemberList,onException");
              quitTeamData.postValue(
                  new ResultInfo<>(null, false, new ErrorMsg(-1, "", exception)));
            }
          });
    } else {
      quitTeam(team.getId());
    }
  }

  public void quitTeam(String teamId) {
    EventCenter.notifyEvent(new TeamEvent(teamId, TeamEventAction.ACTION_DISMISS));
    TeamRepo.quitTeam(
        teamId,
        new FetchCallback<Void>() {
          @Override
          public void onSuccess(@Nullable Void param) {
            ALog.d(LIB_TAG, TAG, "quitTeam,onSuccess");
            clearNotify(teamId);
            removeStickTop(teamId);
            quitTeamData.postValue(new ResultInfo<>(param));
          }

          @Override
          public void onFailed(int code) {
            ALog.d(LIB_TAG, TAG, "quitTeam,onFailed:" + code);
            quitTeamData.postValue(new ResultInfo<>(null, false, new ErrorMsg(code)));
          }

          @Override
          public void onException(@Nullable Throwable exception) {
            ALog.d(LIB_TAG, TAG, "quitTeam,onException");
            quitTeamData.postValue(new ResultInfo<>(null, false, new ErrorMsg(-1, "", exception)));
          }
        });
  }

  public void removeStickTop(String teamId) {
    ALog.d(LIB_TAG, TAG, "removeStickTop,teamId:" + teamId);
    if (ConversationRepo.isStickTop(teamId, SessionTypeEnum.Team)) {
      ALog.d(LIB_TAG, TAG, "removeStickTop,isStickTop:" + teamId);
      ConversationRepo.removeStickTop(
          teamId,
          SessionTypeEnum.Team,
          "",
          new FetchCallback<Void>() {
            @Override
            public void onSuccess(@Nullable Void param) {
              ALog.d(LIB_TAG, TAG, "removeStickTop,onSuccess");
            }

            @Override
            public void onFailed(int code) {
              ALog.d(LIB_TAG, TAG, "removeStickTop,onFailed:" + code);
            }

            @Override
            public void onException(@Nullable Throwable exception) {
              ALog.d(LIB_TAG, TAG, "removeStickTop,onException");
            }
          });
    }
  }

  public void clearNotify(String teamId) {
    ALog.d(LIB_TAG, TAG, "clearNotify,teamId:" + teamId);
    ConversationRepo.setNotify(
        teamId,
        SessionTypeEnum.Team,
        true,
        new FetchCallback<Void>() {
          @Override
          public void onSuccess(@Nullable Void param) {
            ALog.d(LIB_TAG, TAG, "clearNotify,onSuccess");
          }

          @Override
          public void onFailed(int code) {
            ALog.d(LIB_TAG, TAG, "clearNotify,onFailed:" + code);
          }

          @Override
          public void onException(@Nullable Throwable exception) {
            ALog.d(LIB_TAG, TAG, "clearNotify,onException");
          }
        });
  }

  public void dismissTeam(String teamId) {
    ALog.d(LIB_TAG, TAG, "dismissTeam:" + teamId);
    EventCenter.notifyEvent(new TeamEvent(teamId, TeamEventAction.ACTION_DISMISS));
    TeamRepo.dismissTeam(
        teamId,
        new FetchCallback<Void>() {
          @Override
          public void onSuccess(@Nullable Void param) {
            ALog.d(LIB_TAG, TAG, "dismissTeam,onSuccess");
            dismissTeamData.postValue(new ResultInfo<>(param));
          }

          @Override
          public void onFailed(int code) {
            ALog.d(LIB_TAG, TAG, "dismissTeam,onFailed:" + code);
            dismissTeamData.postValue(new ResultInfo<>(null, false, new ErrorMsg(code)));
          }

          @Override
          public void onException(@Nullable Throwable exception) {
            ALog.d(LIB_TAG, TAG, "dismissTeam,onException");
            dismissTeamData.postValue(
                new ResultInfo<>(null, false, new ErrorMsg(-1, "", exception)));
          }
        });
  }

  public void muteTeam(String teamId, boolean mute) {
    ALog.d(LIB_TAG, TAG, "muteTeam:" + teamId);
    ConversationRepo.setNotify(
        teamId,
        SessionTypeEnum.Team,
        !mute,
        new FetchCallback<Void>() {
          @Override
          public void onSuccess(@Nullable Void param) {
            ALog.d(LIB_TAG, TAG, "muteTeam,onSuccess");
            muteTeamData.postValue(new ResultInfo<>(mute));
          }

          @Override
          public void onFailed(int code) {
            ALog.d(LIB_TAG, TAG, "muteTeam,onFailed:" + code);
            muteTeamData.postValue(new ResultInfo<>(null, false, new ErrorMsg(code)));
          }

          @Override
          public void onException(@Nullable Throwable exception) {
            ALog.d(LIB_TAG, TAG, "muteTeam,onException");
            muteTeamData.postValue(new ResultInfo<>(null, false, new ErrorMsg(-1, "", exception)));
          }
        });
  }

  public void configStick(String sessionId, boolean stick) {
    ALog.d(LIB_TAG, TAG, "configStick:" + sessionId + "," + stick);
    if (TextUtils.isEmpty(sessionId)) {
      stickData.postValue(new ResultInfo<>(null, false, new ErrorMsg(-1)));
      return;
    }
    if (stick) {
      ConversationRepo.addStickTop(
          sessionId,
          SessionTypeEnum.Team,
          "",
          new FetchCallback<StickTopSessionInfo>() {
            @Override
            public void onSuccess(@Nullable StickTopSessionInfo param) {
              ALog.d(LIB_TAG, TAG, "configStick,onSuccess");
              stickData.postValue(new ResultInfo<>(true));
              ConversationRepo.notifyStickTop(sessionId, SessionTypeEnum.Team);
            }

            @Override
            public void onFailed(int code) {
              ALog.d(LIB_TAG, TAG, "configStick,onFailed:" + code);
              stickData.postValue(new ResultInfo<>(null, false, new ErrorMsg(code)));
            }

            @Override
            public void onException(@Nullable Throwable exception) {
              ALog.d(LIB_TAG, TAG, "configStick,onException");
              stickData.postValue(new ResultInfo<>(null, false, new ErrorMsg(-1, "", exception)));
            }
          });
    } else {
      ConversationRepo.removeStickTop(
          sessionId,
          SessionTypeEnum.Team,
          "",
          new FetchCallback<Void>() {
            @Override
            public void onSuccess(@Nullable Void param) {
              ALog.d(LIB_TAG, TAG, "configStick,onSuccess");
              stickData.postValue(new ResultInfo<>(false));
              ConversationRepo.notifyStickTop(sessionId, SessionTypeEnum.Team);
            }

            @Override
            public void onFailed(int code) {
              ALog.d(LIB_TAG, TAG, "configStick,onFailed:" + code);
              stickData.postValue(new ResultInfo<>(null, false, new ErrorMsg(code)));
            }

            @Override
            public void onException(@Nullable Throwable exception) {
              ALog.d(LIB_TAG, TAG, "configStick,onException");
              stickData.postValue(new ResultInfo<>(null, false, new ErrorMsg(-1, "", exception)));
            }
          });
    }
  }

  public void addMembers(String teamId, List<String> members) {
    ALog.d(
        LIB_TAG, TAG, "addMembers:" + teamId + "," + (members == null ? "null" : members.size()));
    TeamRepo.inviteUser(
        teamId,
        members,
        new FetchCallback<List<String>>() {
          @Override
          public void onSuccess(@Nullable List<String> param) {
            ALog.d(LIB_TAG, TAG, "addMembers,onSuccess");
            addMembersData.postValue(new ResultInfo<>(param));
          }

          @Override
          public void onFailed(int code) {
            ALog.d(LIB_TAG, TAG, "addMembers,onFailed:" + code);
            addMembersData.postValue(new ResultInfo<>(null, false, new ErrorMsg(code)));
          }

          @Override
          public void onException(@Nullable Throwable exception) {
            ALog.d(LIB_TAG, TAG, "addMembers,onException");
            addMembersData.postValue(
                new ResultInfo<>(null, false, new ErrorMsg(-1, "", exception)));
          }
        });
  }

  public void muteTeamAllMember(String teamId, boolean mute) {
    ALog.d(LIB_TAG, TAG, "muteTeamAllMember:" + teamId + "," + mute);
    TeamRepo.muteAllMembers(
        teamId,
        mute,
        new FetchCallback<Void>() {
          @Override
          public void onSuccess(@Nullable Void param) {
            ALog.d(LIB_TAG, TAG, "muteTeamAllMember,onSuccess");
            muteTeamAllMemberData.postValue(new ResultInfo<>(mute));
          }

          @Override
          public void onFailed(int code) {
            ALog.d(LIB_TAG, TAG, "muteTeamAllMember,onFailed:" + code);
            muteTeamAllMemberData.postValue(new ResultInfo<>(null, false, new ErrorMsg(code)));
          }

          @Override
          public void onException(@Nullable Throwable exception) {
            ALog.d(LIB_TAG, TAG, "muteTeamAllMember,onException");
            muteTeamAllMemberData.postValue(
                new ResultInfo<>(null, false, new ErrorMsg(-1, "", exception)));
          }
        });
  }

  public void updateBeInviteMode(String teamId, boolean needAgree) {
    ALog.d(LIB_TAG, TAG, "updateBeInviteMode:" + teamId + "," + needAgree);
    TeamRepo.updateBeInviteMode(
        teamId,
        needAgree,
        new FetchCallback<Void>() {
          @Override
          public void onSuccess(@Nullable Void param) {
            ALog.d(LIB_TAG, TAG, "updateBeInviteMode,onSuccess");
            beInvitedNeedAgreedData.postValue(new ResultInfo<>(needAgree));
          }

          @Override
          public void onFailed(int code) {
            ALog.d(LIB_TAG, TAG, "updateBeInviteMode,onFailed:" + code);
            beInvitedNeedAgreedData.postValue(new ResultInfo<>(null, false, new ErrorMsg(code)));
          }

          @Override
          public void onException(@Nullable Throwable exception) {
            ALog.d(LIB_TAG, TAG, "updateBeInviteMode,onException");
            beInvitedNeedAgreedData.postValue(
                new ResultInfo<>(null, false, new ErrorMsg(-1, "", exception)));
          }
        });
  }

  public void updateInvitePrivilege(String teamId, int type) {
    ALog.d(LIB_TAG, TAG, "updateInvitePrivilege:" + teamId + "," + type);
    TeamRepo.updateInviteMode(
        teamId,
        TeamInviteModeEnum.typeOfValue(type),
        new FetchCallback<Void>() {
          @Override
          public void onSuccess(@Nullable Void param) {
            ALog.d(LIB_TAG, TAG, "updateInvitePrivilege,onSuccess");
            updateInvitePrivilegeData.postValue(new ResultInfo<>(type));
          }

          @Override
          public void onFailed(int code) {
            ALog.d(LIB_TAG, TAG, "updateInvitePrivilege,onFailed:" + code);
            updateInvitePrivilegeData.postValue(new ResultInfo<>(null, false, new ErrorMsg(code)));
          }

          @Override
          public void onException(@Nullable Throwable exception) {
            ALog.d(LIB_TAG, TAG, "updateInvitePrivilege,onException");
            updateInvitePrivilegeData.postValue(
                new ResultInfo<>(null, false, new ErrorMsg(-1, "", exception)));
          }
        });
  }

  public void updateInfoPrivilege(String teamId, int type) {
    ALog.d(LIB_TAG, TAG, "updateInfoPrivilege:" + teamId + "," + type);
    TeamRepo.updateTeamInfoPrivilege(
        teamId,
        TeamUpdateModeEnum.typeOfValue(type),
        new FetchCallback<Void>() {
          @Override
          public void onSuccess(@Nullable Void param) {
            ALog.d(LIB_TAG, TAG, "updateInfoPrivilege,onSuccess");
            updateInfoPrivilegeData.postValue(new ResultInfo<>(type));
          }

          @Override
          public void onFailed(int code) {
            ALog.d(LIB_TAG, TAG, "updateInfoPrivilege,onFailed:" + code);
            updateInfoPrivilegeData.postValue(new ResultInfo<>(null, false, new ErrorMsg(code)));
          }

          @Override
          public void onException(@Nullable Throwable exception) {
            ALog.d(LIB_TAG, TAG, "updateInfoPrivilege,onException");
            updateInfoPrivilegeData.postValue(
                new ResultInfo<>(null, false, new ErrorMsg(-1, "", exception)));
          }
        });
  }

  public MutableLiveData<ResultInfo<Boolean>> getMuteTeamAllMemberData() {
    return muteTeamAllMemberData;
  }

  public MutableLiveData<ResultInfo<Boolean>> getBeInvitedNeedAgreedData() {
    return beInvitedNeedAgreedData;
  }

  public MutableLiveData<ResultInfo<TeamWithCurrentMember>> getTeamWithMemberData() {
    return teamWithMemberData;
  }

  public MutableLiveData<ResultInfo<List<UserInfoWithTeam>>> getUserInfoData() {
    return userInfoData;
  }

  public MutableLiveData<ResultInfo<String>> getNameData() {
    return nameData;
  }

  public MutableLiveData<ResultInfo<String>> getIntroduceData() {
    return introduceData;
  }

  public MutableLiveData<ResultInfo<String>> getNicknameData() {
    return nicknameData;
  }

  public MutableLiveData<ResultInfo<String>> getIconData() {
    return iconData;
  }

  public MutableLiveData<ResultInfo<Void>> getQuitTeamData() {
    return quitTeamData;
  }

  public MutableLiveData<ResultInfo<Void>> getDismissTeamData() {
    return dismissTeamData;
  }

  public MutableLiveData<ResultInfo<Boolean>> getMuteTeamData() {
    return muteTeamData;
  }

  public MutableLiveData<ResultInfo<Boolean>> getStickData() {
    return stickData;
  }

  public MutableLiveData<ResultInfo<Integer>> getUpdateInvitePrivilegeData() {
    return updateInvitePrivilegeData;
  }

  public MutableLiveData<ResultInfo<Integer>> getUpdateInfoPrivilegeData() {
    return updateInfoPrivilegeData;
  }

  public MutableLiveData<ResultInfo<List<String>>> getAddMembersData() {
    return addMembersData;
  }
}
