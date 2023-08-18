// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

package com.netease.yunxin.app.im;

import static com.netease.yunxin.kit.corekit.im.utils.RouterConstant.PATH_FUN_ADD_FRIEND_PAGE;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.content.ContextCompat;

import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.yunxin.kit.chatkit.ui.ChatKitClient;
import com.netease.yunxin.kit.chatkit.ui.ChatUIConfig;
import com.netease.yunxin.kit.chatkit.ui.IChatInputMenu;
import com.netease.yunxin.kit.chatkit.ui.builder.IChatViewCustom;
import com.netease.yunxin.kit.chatkit.ui.fun.view.FunChatView;
import com.netease.yunxin.kit.chatkit.ui.interfaces.IChatView;
import com.netease.yunxin.kit.chatkit.ui.normal.view.ChatView;
import com.netease.yunxin.kit.chatkit.ui.view.input.ActionConstants;
import com.netease.yunxin.kit.common.ui.action.ActionItem;
import com.netease.yunxin.kit.common.ui.utils.ToastX;
import com.netease.yunxin.kit.common.ui.widgets.ContentListPopView;
import com.netease.yunxin.kit.contactkit.ui.ContactKitClient;
import com.netease.yunxin.kit.contactkit.ui.ContactUIConfig;
import com.netease.yunxin.kit.contactkit.ui.contact.BaseContactFragment;
import com.netease.yunxin.kit.contactkit.ui.model.ContactEntranceBean;
import com.netease.yunxin.kit.contactkit.ui.model.IViewTypeConstant;
import com.netease.yunxin.kit.contactkit.ui.view.ContactListViewAttrs;
import com.netease.yunxin.kit.conversationkit.ui.ConversationKitClient;
import com.netease.yunxin.kit.conversationkit.ui.ConversationUIConfig;
import com.netease.yunxin.kit.conversationkit.ui.fun.FunPopItemFactory;
import com.netease.yunxin.kit.corekit.im.utils.RouterConstant;
import com.netease.yunxin.kit.corekit.route.XKitRouter;

import java.util.ArrayList;
import java.util.List;

public class CustomConfig {


    // 个性化配置会话消息页面
    public static void configChatKit(Context context) {
        // test

        final List<ActionItem> actionItemList = assembleInputMoreActions();
        ChatUIConfig chatUIConfig = new ChatUIConfig();

        //    chatUIConfig.messageItemClickListener =
        //        new IMessageItemClickListener() {
        //          @Override
        //          public boolean onSelfIconLongClick(View view, int position, ChatMessageBean messageInfo) {
        //            ToastX.showShortToast("会话页面onSelfIconLongClick点击事件");
        //            return false;
        //          }
        //
        //          @Override
        //          public boolean onUserIconLongClick(View view, int position, ChatMessageBean messageInfo) {
        //            ToastX.showShortToast("会话页面onUserIconLongClick点击事件");
        //            return false;
        //          }
        //
        //            @Override
        //            public boolean onMessageClick(View view, int position, ChatMessageBean messageInfo) {
        //                ToastX.showShortToast("会话消息点击事件"+messageInfo.getMessageData().getMessage().getContent());
        //                return false;
        //            }
        //
        //            @Override
        //            public boolean onReplyMessageClick(View view, int position, IMMessageInfo messageInfo) {
        //                ToastX.showShortToast("onReplyMessageClick"+messageInfo.getMessage().getContent());
        //                return false;
        //            }
        //
        //            @Override
        //            public boolean onReEditRevokeMessage(
        //                    View view,
        //                    int position,
        //                    ChatMessageBean messageInfo
        //            ) {
        //                ToastX.showShortToast("onReEditRevokeMessage"+messageInfo.getMessageData().getMessage().getContent());
        //                return false;
        //            }
        //        };
        // 设置是否展示标题栏、右侧按钮图片和右侧按钮点击事件
        //    chatUIConfig.messageProperties = new MessageProperties();

        //    chatUIConfig.messageProperties.showTitleBar = false;
        //    chatUIConfig.messageProperties.titleBarRightRes = R.drawable.ic_user_setting;
        //      chatUIConfig.messageProperties.avatarCornerRadius = 30f;
        //      chatUIConfig.messageProperties.messageTextColor = Color.GREEN;
        //    chatUIConfig.messageProperties.titleBarRightClick =
        //        new View.OnClickListener() {
        //          @Override
        //          public void onClick(View v) {
        //            ToastX.showShortToast("会话页面标题栏右侧点击事件");
        //          }
        //        };

        //    chatUIConfig.messageProperties.selfMessageBg =
        //        new ColorDrawable(context.getResources().getColor(R.color.color_blue_3a9efb));
        //    chatUIConfig.messageProperties.receiveMessageBg =
        //        new ColorDrawable(context.getResources().getColor(R.color.color_666666));
        //
        //    chatUIConfig.messageProperties.chatViewBackground =
        //        new ColorDrawable(context.getResources().getColor(R.color.red));
        // 个性化配置会话消息页面视图
//        chatUIConfig.chatViewCustom =
//            new IChatViewCustom() {
//              @Override
//              public void customizeChatLayout(IChatView layout) {
//                if (layout instanceof FunChatView) {
//                  FunChatView chatLayout = (FunChatView) layout;
//                  FrameLayout frameLayout = chatLayout.getChatBodyBottomLayout();
//                  TextView textView = new TextView(context, null);
//                  textView.setText("hahhahhhh");
//                  FrameLayout.LayoutParams layoutParams =
//                      new FrameLayout.LayoutParams(
//                          ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                  layoutParams.gravity = Gravity.BOTTOM;
//                  frameLayout.addView(textView, layoutParams);
//                } else if (layout instanceof ChatView) {
//                  ChatView chatLayout = (ChatView) layout;
//                  FrameLayout frameLayout = chatLayout.getChatBodyBottomLayout();
//                  TextView textView = new TextView(context, null);
//                  textView.setText("hahhahhhh");
//                  FrameLayout.LayoutParams layoutParams =
//                      new FrameLayout.LayoutParams(
//                          ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                  layoutParams.gravity = Gravity.BOTTOM;
//                  frameLayout.addView(textView, layoutParams);
//                }
//              }
//            };
        // 个信号配置输入框下面输入按钮
        chatUIConfig.chatInputMenu =
                new IChatInputMenu() {
                    @Override
                    public List<ActionItem> customizeInputBar(List<ActionItem> items) {
                        return actionItemList;
                    }

                    @Override
                    public List<ActionItem> customizeInputMore(List<ActionItem> items) {

                        return actionItemList;
                    }

                    @Override
                    public boolean onCustomInputClick(Context context, View view, String action) {

                        return true;
                    }
                };

        // 个性化配置会话消息中消息长按弹窗菜单
        //    chatUIConfig.chatPopMenu =
        //        new IChatPopMenu() {
        //          @NonNull
        //          @Override
        //          public List<ChatPopMenuAction> customizePopMenu(
        //              List<ChatPopMenuAction> menuList, ChatMessageBean messageBean) {
        //              if(menuList != null){
        //                  for (int index = menuList.size() - 1;index >=0;index--){
        //                      if
        // (TextUtils.equals(menuList.get(index).getAction(),ActionConstants.POP_ACTION_PIN)){
        //                          menuList.remove(index);
        //                          break;
        //                      }
        //                  }
        //              }
        //            return menuList;
        //          }
        //
        //          @Override
        //          public boolean showDefaultPopMenu() {
        //            return true;
        //          }
        //        };
        ChatKitClient.setChatUIConfig(chatUIConfig);
        //    ChatKitClient.addCustomAttach(ChatMessageType.CUSTOM_STICKER, StickerAttachment.class);
        //    ChatKitClient.addCustomViewHolder(ChatMessageType.CUSTOM_STICKER, ChatStickerViewHolder.class);
    }

    // 个性化定制联系人页面
    public static void configContactKit(Context context) {
        ContactUIConfig contactUIConfig = new ContactUIConfig();
        //    contactUIConfig.showHeader = false;
//        contactUIConfig.showTitleBar = true;
        //    contactUIConfig.showTitleBarRightIcon = false;
        //    contactUIConfig.showTitleBarRight2Icon = false;

//        contactUIConfig.title = context.getString(com.netease.yunxin.kit.contactkit.ui.R.string.contact_title);
        //    contactUIConfig.titleColor = Color.GREEN;
        //    contactUIConfig.titleBarRight2Res = R.drawable.ic_about;
        //    contactUIConfig.titleBarRightRes = R.drawable.ic_about;
//        contactUIConfig.titleBarRightClick = v -> ToastX.showShortToast("titleBarRightClick");
//        contactUIConfig.titleBarRight2Click = v -> ToastX.showShortToast("titleBarRight2Click");
        contactUIConfig.titleBarRightClick = v -> {

            int memberLimit = 200;
            ContentListPopView contentListPopView = (new ContentListPopView.Builder(context)).addItem(getAddFriendItem(context))
                    .addItem(FunPopItemFactory.getDivideLineItem(context))
                    .addItem(getQrcodeItem(context)).enableShadow(false)
                    .backgroundRes(R.drawable.bg_popwindow_chats).build();
            contentListPopView.showAsDropDown(v, (int) context.getResources().getDimension(com.netease.yunxin.kit.conversationkit.ui.R.dimen.pop_margin_right), 0);
        };

//        contactUIConfig.contactAttrs = new ContactListViewAttrs();
//        contactUIConfig.contactAttrs.setShowIndexBar(true);
//          contactUIConfig.contactAttrs.setNameTextColor(Color.GREEN);
//          contactUIConfig.contactAttrs.setIndexTextColor(Color.RED);

//        contactUIConfig.headerData = getContactEntranceList(context);
//          contactUIConfig.itemClickListeners.put( IViewTypeConstant.CONTACT_ACTION_ENTER, (position, data) -> {
//              if (!TextUtils.isEmpty(data.router)) {
//                  XKitRouter.withKey(data.router)
//                          .withContext(context)
//                          .navigate();
//              }
//          });

        //    contactUIConfig.customLayout =
        //        new IContactViewLayout() {
        //          @Override
        //          public void customizeContactLayout(ContactLayout layout) {
        //                        layout
        //                            .getBodyLayout()
        //
        // .setBackgroundColor(context.getResources().getColor(R.color.color_a6adb6));
        //              TextView textView = new TextView(context);
        //                                textView.setText("this is contact");
        //                        layout.getBodyTopLayout().addView(textView);
        //          }
        //        };
        ContactKitClient.setContactUIConfig(contactUIConfig);
    }

    // 个性化配置会话列表页面
    public static void configConversation(Context context) {
        ConversationUIConfig conversationUIConfig = new ConversationUIConfig();
        conversationUIConfig.showTitleBarLeftIcon = false;
        //    conversationUIConfig.showTitleBarRight2Icon = false;
        //    conversationUIConfig.showTitleBarRightIcon = false;
        conversationUIConfig.showTitleBar = true;
        //    conversationUIConfig.titleBarLeftRes = R.drawable.ic_more_point;
        //    conversationUIConfig.titleBarRightRes = R.drawable.ic_more_point;
        //    conversationUIConfig.titleBarRight2Res = R.drawable.ic_more_point;
        //    conversationUIConfig.titleBarLeftClick = v -> {
        //
        //        ToastX.showShortToast("titleBarLeftClick");
        //    };
        conversationUIConfig.titleBarRightClick = v -> {

            int memberLimit = 200;
            ContentListPopView contentListPopView = (new ContentListPopView.Builder(context)).addItem(getAddFriendItem(context))
                    .addItem(FunPopItemFactory.getDivideLineItem(context))
                    .addItem(getQrcodeItem(context)).enableShadow(false)
                    .backgroundRes(R.drawable.bg_popwindow_chats).build();
            contentListPopView.showAsDropDown(v, (int) context.getResources().getDimension(com.netease.yunxin.kit.conversationkit.ui.R.dimen.pop_margin_right), 0);
        };
        //      conversationUIConfig.titleBarRight2Click = v -> {
        //
        //          ToastX.showShortToast("titleBarRight2Click");
        //      };

        conversationUIConfig.titleBarTitle = context.getString(R.string.tab_session_tab_text);
        //      conversationUIConfig.titleBarTitleColor = Color.GREEN;
        //
        //
        //      conversationUIConfig.itemTitleColor = Color.GREEN;
        //      conversationUIConfig.itemTitleSize = 42;
        //      conversationUIConfig.itemBackground = new ColorDrawable(Color.BLACK);
        //      conversationUIConfig.itemContentColor = Color.GREEN;
        //      conversationUIConfig.itemContentSize = 32;
        //      conversationUIConfig.itemDateColor = Color.GREEN;
        //      conversationUIConfig.itemDateSize = 32;
        //      conversationUIConfig.itemStickTopBackground = new ColorDrawable(Color.RED);
        //      conversationUIConfig.avatarCornerRadius = 30f;

        //      conversationUIConfig.itemClickListener =
        //        new ItemClickListener() {
        //          @Override
        //          public boolean onClick(Context context, ConversationBean data, int position) {
        //
        //            ToastX.showShortToast("onClick");
        //            return false;
        //          }
        //
        //          @Override
        //          public boolean onAvatarClick(Context context, ConversationBean data, int position) {
        //            ToastX.showShortToast("onAvatarClick");
        //            return false;
        //          }
        //
        //            @Override
        //            public boolean onLongClick(Context context, ConversationBean data, int position) {
        //                ToastX.showShortToast("onLongClick");
        //                return false;
        //            }
        //
        //            @Override
        //            public boolean onAvatarLongClick(Context context, ConversationBean data, int
        // position) {
        //                ToastX.showShortToast("onAvatarLongClick");
        //                return false;
        //            }
        //        };

        //      conversationUIConfig.conversationCustom = new ConversationCustom(){
        //          @Override
        //          public String customContentText(Context context, ConversationInfo conversationInfo)
        // {
        //              String test = super.customContentText(context,conversationInfo);
        //              return test+"test";
        //          }
        //      };

        //      conversationUIConfig.customLayout = new IConversationViewLayout() {
        //          @Override
        //          public void customizeConversationLayout(ConversationBaseFragment fragment) {
        //
        //              if (fragment instanceof ConversationFragment){
        //                  ConversationFragment conversationFragment = (ConversationFragment) fragment;
        //                  TextView textView = new TextView(conversationFragment.getContext());
        //                  textView.setText("this is conversationFragment");
        //                  conversationFragment.getBodyTopLayout().addView(textView);
        //              }else if (fragment instanceof FunConversationFragment){
        //                  FunConversationFragment conversationFragment = (FunConversationFragment)
        // fragment;
        //                  TextView textView = new TextView(conversationFragment.getContext());
        //                  textView.setText("this is FunConversationFragment");
        //                  conversationFragment.getBodyTopLayout().addView(textView);
        //              }
        //
        //          }
        //      };

        ConversationKitClient.setConversationUIConfig(conversationUIConfig);
    }


    public static ContentListPopView.Item getAddFriendItem(Context context) {
        LinearLayout.LayoutParams params = getParams(context);
        return new ContentListPopView.Item.Builder()
                .configView(
                        getView(context, com.netease.yunxin.kit.conversationkit.ui.R.string.add_friend, com.netease.yunxin.kit.conversationkit.ui.R.drawable.fun_ic_conversation_add_friend))
                .configParams(params)
                .configClickListener(
                        v -> XKitRouter.withKey(PATH_FUN_ADD_FRIEND_PAGE).withContext(context).navigate())
                .build();
    }

    public static ContentListPopView.Item getQrcodeItem(Context context) {
        LinearLayout.LayoutParams params = getParams(context);
        return new ContentListPopView.Item.Builder()
                .configView(
                        getView(context, com.netease.yunxin.kit.conversationkit.ui.R.string.add_friend, R.mipmap.icon_qr))
                .configParams(params)
                .configClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, QrCaptureActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                })
                .build();
    }

    private static LinearLayout.LayoutParams getParams(Context context) {
        return new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                (int) context.getResources().getDimension(com.netease.yunxin.kit.conversationkit.ui.R.dimen.fun_add_pop_item_height));
    }

    private static View getView(Context context, int txtId, int drawableId) {
        TextView textView = new TextView(context);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setTextSize(16);
        textView.setMaxLines(1);
        textView.setText(txtId);

        int marginSize =
                (int) context.getResources().getDimension(com.netease.yunxin.kit.conversationkit.ui.R.dimen.fun_add_pop_item_margin_right_top);
        int padding = (int) context.getResources().getDimension(com.netease.yunxin.kit.conversationkit.ui.R.dimen.fun_add_pop_item_padding);
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (drawable != null) {
            drawable.setBounds(padding, 0, marginSize + padding, marginSize);
            textView.setCompoundDrawables(drawable, null, null, null);
        }
        textView.setPadding(
                (int) context.getResources().getDimension(com.netease.yunxin.kit.conversationkit.ui.R.dimen.dimen_8_dp),
                0,
                (int) context.getResources().getDimension(com.netease.yunxin.kit.conversationkit.ui.R.dimen.dimen_14_dp),
                0);
        textView.setMinWidth((int) context.getResources().getDimension(com.netease.yunxin.kit.conversationkit.ui.R.dimen.fun_add_pop_item_width));
        textView.setTextColor(
                context.getResources().getColor(com.netease.yunxin.kit.conversationkit.ui.R.color.fun_conversation_add_pop_text_color));
        textView.setCompoundDrawablePadding(marginSize);
        return textView;
    }


    public static List<ActionItem> assembleInputMoreActions() {
        ArrayList<ActionItem> actions = new ArrayList<>();

        actions.add(
                new ActionItem(
                        ActionConstants.ACTION_TYPE_ALBUM,
                        com.netease.yunxin.kit.chatkit.ui.R.drawable.fun_ic_chat_input_more_album,
                        com.netease.yunxin.kit.chatkit.ui.R.string.chat_input_more_album_title));
        actions.add(
                new ActionItem(
                        ActionConstants.ACTION_TYPE_CAMERA,
                        com.netease.yunxin.kit.chatkit.ui.R.drawable.ic_shoot,
                        com.netease.yunxin.kit.chatkit.ui.R.string.chat_message_more_shoot));
//        actions.add(
//                new ActionItem(
//                        ActionConstants.ACTION_TYPE_LOCATION,
//                        com.netease.yunxin.kit.chatkit.ui.R.drawable.ic_location,
//                        com.netease.yunxin.kit.chatkit.ui.R.string.chat_message_location));
        actions.add(
                new ActionItem(
                        ActionConstants.ACTION_TYPE_FILE, com.netease.yunxin.kit.chatkit.ui.R.drawable.ic_send_file, com.netease.yunxin.kit.chatkit.ui.R.string.chat_message_file));
        actions.add(
                new ActionItem(
                        ActionConstants.ACTION_TYPE_VIDEO_CALL,
                        com.netease.yunxin.kit.chatkit.ui.R.drawable.ic_video_call,
                        com.netease.yunxin.kit.chatkit.ui.R.string.chat_message_video_call));
        return actions;
    }


    protected static List<ContactEntranceBean> getContactEntranceList(Context context) {
        List<ContactEntranceBean> contactDataList = new ArrayList<>();
        //verify message
        ContactEntranceBean verifyBean =
                new ContactEntranceBean(
                        com.netease.yunxin.kit.contactkit.ui.R.mipmap.fun_ic_contact_verfiy_msg,
                        context.getString(com.netease.yunxin.kit.contactkit.ui.R.string.contact_list_verify_msg));
        verifyBean.router = RouterConstant.PATH_FUN_MY_NOTIFICATION_PAGE;
        verifyBean.showRightArrow = false;

        //black list
        ContactEntranceBean blackBean =
                new ContactEntranceBean(
                        com.netease.yunxin.kit.contactkit.ui.R.mipmap.fun_ic_contact_black_list,
                        context.getString(com.netease.yunxin.kit.contactkit.ui.R.string.contact_list_black_list));
        blackBean.router = RouterConstant.PATH_FUN_MY_BLACK_PAGE;
        blackBean.showRightArrow = false;
//        //my group
//        ContactEntranceBean groupBean =
//                new ContactEntranceBean(
//                        com.netease.yunxin.kit.contactkit.ui.R.mipmap.fun_ic_contact_my_group, context.getString(com.netease.yunxin.kit.contactkit.ui.R.string.contact_list_my_group));
//        groupBean.router = RouterConstant.PATH_FUN_MY_TEAM_PAGE;
//        groupBean.showRightArrow = false;

        contactDataList.add(verifyBean);
        contactDataList.add(blackBean);
//        contactDataList.add(groupBean);
        return contactDataList;
    }

}
