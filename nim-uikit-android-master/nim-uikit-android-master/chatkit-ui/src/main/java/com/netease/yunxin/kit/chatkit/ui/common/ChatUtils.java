// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

package com.netease.yunxin.kit.chatkit.ui.common;

import static com.netease.yunxin.kit.chatkit.ui.ChatKitUIConstant.CHAT_FORWARD_USER_LIMIT;
import static com.netease.yunxin.kit.chatkit.ui.ChatKitUIConstant.LIB_TAG;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;
import androidx.activity.result.ActivityResultLauncher;
import com.netease.nimlib.sdk.msg.attachment.FileAttachment;
import com.netease.nimlib.sdk.msg.attachment.VideoAttachment;
import com.netease.nimlib.sdk.msg.constant.AttachStatusEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.team.constant.TeamTypeEnum;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.yunxin.kit.alog.ALog;
import com.netease.yunxin.kit.chatkit.model.IMMessageInfo;
import com.netease.yunxin.kit.chatkit.repo.ChatRepo;
import com.netease.yunxin.kit.chatkit.ui.ChatKitClient;
import com.netease.yunxin.kit.chatkit.ui.ChatKitUIConstant;
import com.netease.yunxin.kit.chatkit.ui.R;
import com.netease.yunxin.kit.chatkit.ui.page.WatchImageActivity;
import com.netease.yunxin.kit.chatkit.ui.page.WatchVideoActivity;
import com.netease.yunxin.kit.common.utils.CommonFileProvider;
import com.netease.yunxin.kit.common.utils.FileUtils;
import com.netease.yunxin.kit.corekit.im.IMKitClient;
import com.netease.yunxin.kit.corekit.im.utils.IMKitConstant;
import com.netease.yunxin.kit.corekit.im.utils.RouterConstant;
import com.netease.yunxin.kit.corekit.route.XKitRouter;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ChatUtils {

  public static final String TAG = "ChatUtils";

  //使用手机应用打开文件
  public static void openFileWithApp(Context context, IMMessage message) {
    FileAttachment fileAttachment = (FileAttachment) message.getAttachment();
    File openFile = new File(fileAttachment.getPath());
    if (openFile.exists()) {
      Uri fileUri = CommonFileProvider.Companion.getUriForFile(context, openFile);
      String fileExtension = fileAttachment.getExtension();
      if (TextUtils.isEmpty(fileExtension)) {
        fileExtension = FileUtils.getFileExtension(openFile.getPath());
      }
      String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);
      Intent fileIntent = new Intent(Intent.ACTION_VIEW);
      fileIntent.setDataAndType(fileUri, mimeType);
      fileIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
      fileIntent.addFlags(
          Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
      fileIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      context.startActivity(fileIntent);
    }
  }

  //是否为讨论组
  public static boolean isTeamGroup(Team teamInfo) {
    String teamExtension = teamInfo.getExtension();
    if ((teamExtension != null && teamExtension.contains(IMKitConstant.TEAM_GROUP_TAG))
        || teamInfo.getType() == TeamTypeEnum.Normal) {
      return true;
    }

    return false;
  }

  public static String formatFileSize(long fileS) {
    DecimalFormat df = new DecimalFormat("#.00");
    String fileSizeString;
    String wrongSize = "0B";
    if (fileS == 0) {
      return wrongSize;
    }
    if (fileS < 1000) {
      fileSizeString = df.format((double) fileS) + "B";
    } else if (fileS < 1000000) {
      fileSizeString = df.format((double) fileS / 1000) + "KB";
    } else if (fileS < 1000000000) {
      fileSizeString = df.format((double) fileS / 1000000) + "MB";
    } else {
      fileSizeString = df.format((double) fileS / 1000000000) + "GB";
    }
    return fileSizeString;
  }

  public static boolean fileSizeLimit(long fileS) {
    long limit = getFileLimitSize();
    if (limit < 0) {
      return false;
    }
    long limitSize = limit * 1000000L;
    return fileS > limitSize;
  }

  public static Long getFileLimitSize() {
    long limit = ChatKitUIConstant.FILE_LIMIT;
    if (ChatKitClient.getChatUIConfig() != null
        && ChatKitClient.getChatUIConfig().messageProperties != null) {
      limit = ChatKitClient.getChatUIConfig().messageProperties.sendFileLimit;
    }
    return limit;
  }

  private static final Map<String, Integer> fileRes = getFileTypeMap();

  public static int getFileIcon(String type) {
    if (!TextUtils.isEmpty(type)) {
      String lowerType = type.toLowerCase(Locale.getDefault());
      Integer result = fileRes.get(lowerType);
      return result != null ? result : R.drawable.ic_unknown_file;
    }
    return R.drawable.ic_unknown_file;
  }

  public static String getUrlFileName(Context context, Uri uri) {
    Cursor resolver = context.getContentResolver().query(uri, null, null, null, null, null);
    resolver.moveToFirst();
    int nameIndex = resolver.getColumnIndex(OpenableColumns.DISPLAY_NAME);
    String displayName = "";
    if (nameIndex >= 0) {
      displayName = resolver.getString(nameIndex);
      ALog.d("ChatUtils", "pick file result uri(" + displayName + ") -->> " + uri);
    }
    resolver.close();

    return displayName;
  }

  public static String getUrlFileSize(Context context, Uri uri) {
    Cursor resolver = context.getContentResolver().query(uri, null, null, null, null, null);
    resolver.moveToFirst();
    int sizeIndex = resolver.getColumnIndex(OpenableColumns.SIZE);
    String displaySize = "0";
    if (sizeIndex >= 0) {
      displaySize = resolver.getString(sizeIndex);
      ALog.d("ChatUtils", "pick file result uri(" + displaySize + ") -->> " + uri);
    }
    resolver.close();

    return displaySize;
  }

  private static Map<String, Integer> getFileTypeMap() {
    Map<String, Integer> fileRes = new HashMap<>();
    fileRes.put("doc", R.drawable.ic_word_file);
    fileRes.put("docx", R.drawable.ic_word_file);
    fileRes.put("xls", R.drawable.ic_excel_file);
    fileRes.put("xlsx", R.drawable.ic_excel_file);
    fileRes.put("ppt", R.drawable.ic_ppt_file);
    fileRes.put("pptx", R.drawable.ic_ppt_file);
    //    fileRes.put("keynote", R.drawable.ic_ppt_file);
    fileRes.put("jpg", R.drawable.ic_image_file);
    fileRes.put("png", R.drawable.ic_image_file);
    fileRes.put("jpeg", R.drawable.ic_image_file);
    //    fileRes.put("psd", R.drawable.ic_image_file);
    fileRes.put("tiff", R.drawable.ic_image_file);
    fileRes.put("gif", R.drawable.ic_image_file);
    fileRes.put("zip", R.drawable.ic_rar_file);
    fileRes.put("7z", R.drawable.ic_rar_file);
    fileRes.put("tar", R.drawable.ic_rar_file);
    fileRes.put("rar", R.drawable.ic_rar_file);
    fileRes.put("pdf", R.drawable.ic_pdf_file);
    fileRes.put("rtf", R.drawable.ic_pdf_file);
    fileRes.put("txt", R.drawable.ic_text_file);
    fileRes.put("csv", R.drawable.ic_excel_file);
    fileRes.put("html", R.drawable.ic_html_file);
    fileRes.put("mp4", R.drawable.ic_video_file);
    fileRes.put("avi", R.drawable.ic_video_file);
    fileRes.put("wmv", R.drawable.ic_video_file);
    fileRes.put("mpeg", R.drawable.ic_video_file);
    fileRes.put("m4v", R.drawable.ic_video_file);
    fileRes.put("mov", R.drawable.ic_video_file);
    fileRes.put("asf", R.drawable.ic_video_file);
    fileRes.put("flv", R.drawable.ic_video_file);
    fileRes.put("f4v", R.drawable.ic_video_file);
    fileRes.put("rmvb", R.drawable.ic_video_file);
    fileRes.put("rm", R.drawable.ic_video_file);
    fileRes.put("3gp", R.drawable.ic_video_file);
    //    fileRes.put("vob", R.drawable.ic_video_file);
    fileRes.put("mp3", R.drawable.ic_mp3_file);
    fileRes.put("aac", R.drawable.ic_mp3_file);
    fileRes.put("wav", R.drawable.ic_mp3_file);
    fileRes.put("wma", R.drawable.ic_mp3_file);
    //    fileRes.put("cda", R.drawable.ic_mp3_file);
    fileRes.put("flac", R.drawable.ic_mp3_file);
    fileRes.put("unknown", R.drawable.ic_unknown_file);
    fileRes.put("", R.drawable.ic_unknown_file);
    return fileRes;
  }

  public static void startTeamList(
      Context context, String pagePath, ActivityResultLauncher<Intent> launcher) {
    XKitRouter.withKey(pagePath)
        .withParam(RouterConstant.KEY_TEAM_LIST_SELECT, true)
        .withContext(context)
        .navigate(launcher);
  }

  public static void startP2PSelector(
      Context context, String pagePath, String filterId, ActivityResultLauncher<Intent> launcher) {
    ArrayList<String> filterList = new ArrayList<>();
    filterList.add(filterId);
    XKitRouter.withKey(pagePath)
        .withParam(RouterConstant.KEY_CONTACT_SELECTOR_MAX_COUNT, CHAT_FORWARD_USER_LIMIT)
        .withContext(context)
        .withParam(RouterConstant.SELECTOR_CONTACT_FILTER_KEY, filterList)
        .navigate(launcher);
  }

  public static void startVideoCall(Context context, String sessionId) {
    XKitRouter.withKey(RouterConstant.PATH_CALL_SINGLE_PAGE)
        .withContext(context)
        .withParam(RouterConstant.KEY_CALLER_ACC_ID, IMKitClient.account())
        .withParam(RouterConstant.KEY_CALLED_ACC_ID, sessionId)
        .withParam(RouterConstant.KEY_CALL_TYPE, RouterConstant.KEY_CALL_TYPE_VIDEO)
        .navigate();
  }

  public static void startAudioCall(Context context, String sessionId) {
    XKitRouter.withKey(RouterConstant.PATH_CALL_SINGLE_PAGE)
        .withContext(context)
        .withParam(RouterConstant.KEY_CALLER_ACC_ID, IMKitClient.account())
        .withParam(RouterConstant.KEY_CALLED_ACC_ID, sessionId)
        .withParam(RouterConstant.KEY_CALL_TYPE, RouterConstant.KEY_CALL_TYPE_AUDIO)
        .navigate();
  }

  public static void watchImage(
      Context context, IMMessageInfo messageInfo, ArrayList<IMMessageInfo> imageMessages) {
    int index = 0;
    if (messageInfo.getMessage().getAttachStatus() != AttachStatusEnum.transferred
        && messageInfo.getMessage().getAttachStatus() != AttachStatusEnum.transferring) {
      ChatRepo.downloadAttachment(messageInfo.getMessage(), false, null);
    }
    ArrayList<IMMessage> messages = new ArrayList<>();
    for (int i = 0; i < imageMessages.size(); ++i) {
      if (messageInfo.equals(imageMessages.get(i))) {
        index = i;
      }
      messages.add(imageMessages.get(i).getMessage());
    }
    WatchImageActivity.launch(context, messages, index);
  }

  public static boolean watchVideo(Context context, IMMessageInfo messageInfo) {
    if (messageInfo == null) {
      return false;
    }
    IMMessage message = messageInfo.getMessage();
    if (message.getAttachStatus() == AttachStatusEnum.transferred
        && !TextUtils.isEmpty(((VideoAttachment) message.getAttachment()).getPath())) {
      WatchVideoActivity.launch(context, message);
      return true;
    } else if (message.getAttachStatus() != AttachStatusEnum.transferring
        && message.getAttachment() instanceof FileAttachment) {
      ALog.d(LIB_TAG, TAG, "downloadMessageAttachment:" + message.getUuid());
      ChatRepo.downloadAttachment(message, false, null);
    }
    return false;
  }

  public static boolean openFile(Context context, IMMessageInfo messageInfo) {
    if (messageInfo == null) {
      return false;
    }
    IMMessage message = messageInfo.getMessage();
    if (message.getAttachStatus() == AttachStatusEnum.transferred
        && !TextUtils.isEmpty(((FileAttachment) message.getAttachment()).getPath())) {
      ChatUtils.openFileWithApp(context, message);
      return true;
    } else if (message.getAttachStatus() != AttachStatusEnum.transferring
        && message.getAttachment() instanceof FileAttachment) {
      ALog.d(LIB_TAG, TAG, "downloadMessageAttachment:" + message.getUuid());
      ChatRepo.downloadAttachment(message, false, null);
    }
    return false;
  }
}
