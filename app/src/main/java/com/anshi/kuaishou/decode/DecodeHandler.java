/*
 * Copyright (C) 2010 ZXing authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.anshi.kuaishou.decode;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.anshi.kuaishou.MyApplication;
import com.anshi.kuaishou.R;
import com.anshi.kuaishou.activity.ScannerActivity;
import com.anshi.kuaishou.tess.TessEngine;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;

final class DecodeHandler extends Handler {

    private final ScannerActivity mActivity;
    private final MultiFormatReader mMultiFormatReader;
    private final Map<DecodeHintType, Object> mHints;
    private byte[] mRotatedData;
    private boolean isScanMobile;
    private boolean isScanOneCode;

    DecodeHandler(ScannerActivity activity, boolean scmo, boolean scoc) {
        this.mActivity = activity;
        this.isScanMobile = scmo;
        this.isScanOneCode = scoc;
        mMultiFormatReader = new MultiFormatReader();
        mHints = new Hashtable<>();
        mHints.put(DecodeHintType.CHARACTER_SET, "utf-8");
        mHints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        Collection<BarcodeFormat> barcodeFormats = new ArrayList<>();
        barcodeFormats.add(BarcodeFormat.CODE_39);
        barcodeFormats.add(BarcodeFormat.CODE_128); // 快递单常用格式39,128
        barcodeFormats.add(BarcodeFormat.CODABAR);
        barcodeFormats.add(BarcodeFormat.CODE_93);
        barcodeFormats.add(BarcodeFormat.EAN_8);
        barcodeFormats.add(BarcodeFormat.EAN_13);
        barcodeFormats.add(BarcodeFormat.UPC_A);
        barcodeFormats.add(BarcodeFormat.UPC_E);
//        barcodeFormats.add(BarcodeFormat.QR_CODE); //扫描格式自行添加
        mHints.put(DecodeHintType.POSSIBLE_FORMATS, barcodeFormats);
    }

    @Override
    public void handleMessage(Message message) {
        switch (message.what) {
            case R.id.decode:
                decode((byte[]) message.obj, message.arg1, message.arg2);
                break;
            case R.id.quit:
                Looper looper = Looper.myLooper();
                if (null != looper) {
                    looper.quit();
                }
                break;
        }
    }

    /**
     * Decode the data within the viewfinder rectangle, and time how long it took. For efficiency, reuse the same reader
     * objects from one decode to the next.
     *
     * @param data The YUV preview frame.
     * @param width The width of the preview frame.
     * @param height The height of the preview frame.
     */
    private void decode(byte[] data, int width, int height) {
        if (null == mRotatedData) {
            mRotatedData = new byte[width * height];
        } else {
            if (mRotatedData.length < width * height) {
                mRotatedData = new byte[width * height];
            }
        }
        Arrays.fill(mRotatedData, (byte) 0);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (x + y * width >= data.length) {
                    break;
                }
                mRotatedData[x * height + height - y - 1] = data[x + y * width];
            }
        }
        int tmp = width; // Here we are swapping, that's the difference to #11
        width = height;
        height = tmp;

        Result rawResult = null;
        try {
            Rect rect = mActivity.getCropRect();
            Rect rectOneCode = mActivity.getOnecodeRect();
            if (rect == null) {
                return;
            }

            PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(mRotatedData, width, height, rect.left, rect.top, rect.width(), rect.height(), false);
            PlanarYUVLuminanceSource sourceOnecode = new PlanarYUVLuminanceSource(mRotatedData, width, height, rectOneCode.left, rectOneCode.top, rectOneCode.width(), rectOneCode.height(), false);

            if( mActivity.isQRCode()){
                BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
                rawResult = mMultiFormatReader.decode(bitmap1, mHints);
            }else{
//                TessEngine tessEngine = TessEngine.Generate(MyApplication.sAppContext);
//                Bitmap bitmap = source.renderCroppedGreyscaleBitmap();
//                String result = tessEngine.detectText(bitmap);
//                if(!TextUtils.isEmpty(result)){
//                    rawResult = new Result(result, null, null, null);
//                    rawResult.setBitmap(bitmap);
//                }
                if (isScanOneCode) {
                    BinaryBitmap bitmap2 = new BinaryBitmap(new HybridBinarizer(sourceOnecode));
                    Result mailResult = mMultiFormatReader.decode(bitmap2, mHints);
                    if (null != mailResult && !TextUtils.isEmpty(mailResult.getText())) {
                        rawResult = new Result(mailResult.getText(), null, null, null);
                        rawResult.setMailNoStr(mailResult.getText());
                        isScanOneCode = false;
                    }
                }
                if (isScanMobile) {
                    TessEngine tessEngine = TessEngine.Generate(MyApplication.sAppContext);
                    Bitmap bitmap = source.renderCroppedGreyscaleBitmap();
                    String result = tessEngine.detectText(bitmap);
                    if(!TextUtils.isEmpty(result)){
                        if (11 < result.length()) {
                            result = result.substring(0, 11);
                        }
                        if (null == rawResult) {
                            rawResult = new Result(result, null, null, null);
                        }
                        rawResult.setBitmap(bitmap);
                        rawResult.setMobileNoStr(result);
                        isScanMobile = false;
                    }
                }
            }
        } catch (Exception ignored) {
            ignored.printStackTrace();
        } finally {
            mMultiFormatReader.reset();
        }

        if (rawResult != null) {
            if (!isScanOneCode && !isScanMobile) {
                isScanOneCode = true;
                isScanMobile = true;
            }
            Message message = Message.obtain(mActivity.getCaptureActivityHandler(), R.id.decode_succeeded, rawResult);
            message.sendToTarget();
        } else {
            Message message = Message.obtain(mActivity.getCaptureActivityHandler(), R.id.decode_failed);
            message.sendToTarget();
        }
    }
}
