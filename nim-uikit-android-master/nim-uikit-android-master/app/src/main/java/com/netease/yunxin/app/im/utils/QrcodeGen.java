package com.netease.yunxin.app.im.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.netease.yunxin.app.im.bean.LoginIMResultBean;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

public class QrcodeGen {

    static QrcodeGen qrcode;

    public static QrcodeGen geInstance() {
        if (qrcode == null) {
            qrcode = new QrcodeGen();
        }
        return qrcode;
    }

    public String getPersonalQrcode(Context context) {

        Bitmap bitmap = null;

        String loginData = SPUtils.getInstance().get(SPUtils.loginData, "");

        if (!TextUtils.isEmpty(loginData)) {
            int wh = ScreenUtils.getDIP2PX(context, 25);
            LoginIMResultBean loginIMResultBean = new Gson().fromJson(loginData, LoginIMResultBean.class);
            String userName = loginIMResultBean.getUsername();

            File file=new File(context.getExternalCacheDir(),userName + ".Qrcode");
            if(file!=null && file.exists())
            {
                return file.getPath();
            }

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            Hashtable<EncodeHintType, String> hints = new Hashtable<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            try {
                BitMatrix bitMatrix = qrCodeWriter.encode(userName, BarcodeFormat.QR_CODE, wh, wh, hints);

                // 创建一个Bitmap对象，用于保存二维码图像
                bitmap = Bitmap.createBitmap(wh, wh, Bitmap.Config.RGB_565);
                // 矩阵转换图像
                for (int x = 0; x < wh; x++) {
                    for (int y = 0; y < wh; y++) {
                        bitmap.setPixel(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                    }
                }
                FileOutputStream outputStream = context.openFileOutput(userName + ".Qrcode", Context.MODE_PRIVATE);
                bitmap.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                outputStream.flush();
                outputStream.close();

                return file.getPath();

            } catch (WriterException e) {
                throw new RuntimeException(e);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return null;
    }

}
