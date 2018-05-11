package com.anshi.kuaishou.utils;

import com.anshi.kuaishou.domain.MailAnalysisResult;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 快递单解析器.
 */
public class MailAnalyzer {

    /**
     * 快递单解析.
     * @param ocrResult OCR识别结果（字符串列表 List<String>）
     * @return 解析结果
     * <pre>
     *     {
     *         code: 解析结果（200:成功;其余:全部或部分解析失败）;
     *         mailNo: 快递单号（code=200时有效）;
     *         recipientName: 收件人姓名（code=200时有效）;
     *         recipientPhone: 收件人电话（code=200时有效）;
     *     }
     * </pre>
     */
    public static MailAnalysisResult doMailAnalysis(List<String> ocrResult) {
        MailAnalysisResult result = new MailAnalysisResult();
        result.setCode(MailAnalysisResult.SUCCESS);
        boolean isNameOk = false;
        boolean isPhoneOk = false;
        boolean isMailNoOk = false;
        String tmpStr = null;
        String[] tmpStrArr = null;
        for (int i = 0; i < ocrResult.size(); i++) {
            tmpStr = ocrResult.get(i).trim();
            tmpStrArr = tmpStr.split("\\s+");
            if (1 == tmpStrArr.length) {
                if (isMobile(tmpStr)) {
                    result.setRecipientName(getNameStr(ocrResult.get(i - 1).trim()));
                    result.setRecipientPhone(getPhoneStr(tmpStr));
                    isNameOk = true;
                    isPhoneOk = true;
                    break;
                } else {
                    continue;
                }
            } else if (1 < tmpStrArr.length) {
                if (isMobile(tmpStrArr[1])) {
                    result.setRecipientName(getNameStr(tmpStrArr[0]));
                    result.setRecipientPhone(getPhoneStr(tmpStrArr[1]));
                    isNameOk = true;
                    isPhoneOk = true;
                    break;
                } else {
                    continue;
                }
            } else {
                continue;
            }
        }
        for (int i = ocrResult.size() -1; i >= 0; i--) {
            tmpStr = ocrResult.get(i).trim();
            if (tmpStr.matches("([a-zA-Z]*)\\d{12,}([a-zA-Z]*)")) {
                result.setMailNo(tmpStr);
                isMailNoOk = true;
                break;
            }
        }
        if (!isMailNoOk && !isNameOk && !isPhoneOk) {
            result.setCode(MailAnalysisResult.ERROR);
        }
        return result;
    }

    /** 手机号码正则（前三位判断）. */
    private static String mobileRegex3 = "^(130|131|132|133|134|135|136|137|138|139|145|147|149|150|151|152|153|155|156|157|158|159|166|170|171|172|173|175|176|177|178|180|181|182|183|184|185|186|187|188|189|198|199)\\d{8}$";
    /** 手机号码正则（前两位判断）. */
    private static String mobileRegex2 = "^(13|14|15|16|17|18|19)\\d{9}$";

    /**
     * 是否是手机号码.
     * @param mob 手机号码字符串
     * @return true：是；false：不是
     */
    private static boolean isMobile(String mob) {
        if (isBlank(mob)) {
            return false;
        }
        String tmpMob = mob;
        if (11 < mob.length()) {
            tmpMob = tmpMob.substring(0, 11);
        }
        return Pattern.matches(mobileRegex2, tmpMob);
    }

    /**
     * 手机号码字符串处理.
     * @param phStr 手机号码字符串
     * @return 处理后的手机号码
     */
    private static String getPhoneStr(String phStr) {
        String tmpPh = phStr;
        if (11 < phStr.length()) {
            tmpPh = tmpPh.substring(0, 11);
        }
        return tmpPh;
    }

    /**
     * 姓名字符串的处理.
     * @param nmStr 姓名字符串
     * @return 处理后的姓名
     */
    private static String getNameStr(String nmStr) {
        String tmpNm = nmStr;
        if (nmStr.startsWith("收")) {
            tmpNm = tmpNm.substring(1);
        }
        return tmpNm.trim();
    }

    /**
     * 是否是空或空白字符串.
     * @param cs 字符串
     * @return true：是；false：不是
     */
    private static boolean isBlank(String cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (Character.isWhitespace(cs.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }
}
