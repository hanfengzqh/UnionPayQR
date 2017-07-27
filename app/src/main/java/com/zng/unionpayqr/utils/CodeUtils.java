package com.zng.unionpayqr.utils;

import java.io.UnsupportedEncodingException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.widget.ImageView;

import com.google.zxing.WriterException;
import com.zng.unionpayqr.R;
import com.zng.unionpayqr.zxing.encode.EncodingHandler;

public class CodeUtils {

	/**
	 * 生成二维码
	 * 
	 * @param key
	 */
	public static Bitmap create2Code(String key,ImageView iv2Code) {
		Bitmap qrCode = null;
		try {
			qrCode = EncodingHandler.create2Code(key, 600);
		    iv2Code.setImageBitmap(qrCode);
		} catch (WriterException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return qrCode;
	}

	/**
	 * 初始化头像图片
	 */
	public static Bitmap getHeadBitmap(int size,Activity mActivity) {
		try {
			// 这里采用从asset中加载图片abc.jpg
			Bitmap portrait = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.unionpay_loggo);
			// 对原有图片压缩显示大小
			Matrix mMatrix = new Matrix();
			float width = portrait.getWidth();
			float height = portrait.getHeight();
			mMatrix.setScale(size / width, size / height);
			return Bitmap.createBitmap(portrait, 0, 0, (int) width, (int) height, mMatrix, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 在二维码上绘制头像
	 */
	public static void createQRCodeBitmapWithPortrait(Bitmap qr, Bitmap portrait) {
		// 头像图片的大小
		int portrait_W = portrait.getWidth();
		int portrait_H = portrait.getHeight();

		// 设置头像要显示的位置，即居中显示
		int left = (qr.getWidth() - portrait_W) / 2;
		int top = (qr.getHeight() - portrait_H) / 2;
		int right = left + portrait_W;
		int bottom = top + portrait_H;
		Rect rect1 = new Rect(left, top, right, bottom);

		// 取得qr二维码图片上的画笔，即要在二维码图片上绘制我们的头像
		Canvas canvas = new Canvas(qr);

		// 设置我们要绘制的范围大小，也就是头像的大小范围
		Rect rect2 = new Rect(0, 0, portrait_W, portrait_H);
		// 开始绘制
		canvas.drawBitmap(portrait, rect2, rect1, null);
	}
}
