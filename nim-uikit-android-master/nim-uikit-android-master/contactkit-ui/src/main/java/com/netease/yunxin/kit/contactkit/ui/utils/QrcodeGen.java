package com.netease.yunxin.kit.contactkit.ui.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.netease.yunxin.kit.contactkit.ui.model.LoginIMResultBean;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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

        String loginData = SPUtils.getInstance(context).get(SPUtils.loginData, "");

        if (!TextUtils.isEmpty(loginData)) {
            int wh = ScreenUtils.getDIP2PX(context, 100);
            LoginIMResultBean loginIMResultBean = new Gson().fromJson(loginData, LoginIMResultBean.class);
            String userName = loginIMResultBean.getAccid();

            File file=new File(context.getExternalCacheDir(),userName + ".Qrcode");
            if(file!=null && file.exists())
            {
                return file.getPath();
            }

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
            hints.put(EncodeHintType.MARGIN,0);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            try {
                BitMatrix bitMatrix = qrCodeWriter.encode(userName, BarcodeFormat.QR_CODE, wh, wh, hints);

                // 创建一个Bitmap对象，用于保存二维码图像
                bitmap = Bitmap.createBitmap(wh, wh, Bitmap.Config.ARGB_8888);
                // 矩阵转换图像
                for (int x = 0; x < wh; x++) {
                    for (int y = 0; y < wh; y++) {
                        bitmap.setPixel(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0x0);

                    }
                }
                FileOutputStream outputStream = new FileOutputStream(file);
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
