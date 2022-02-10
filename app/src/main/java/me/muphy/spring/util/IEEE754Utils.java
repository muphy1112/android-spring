package me.muphy.spring.util;

import me.muphy.spring.annotation.Remind;

import java.math.BigDecimal;
import java.math.BigInteger;

/***
 * IEE754 浮点数转换工具
 * 单精度校验：47218100 = 41345.0、 47849A0D = 67892.1016
 * 双精度校验：4721810047849A0D = 45442749783852262460186723560194048
 *
 * @author: 若非
 * @date: 2021/9/10 8:58
 */
@Remind("校验：47218100 = 41345.0、 47849A0D = 67892.1016")
public class IEEE754Utils {
    //浮点数匹配
    //private static Pattern pattern = Pattern.compile("(\\+|\\-)?(\\d)+(\\.\\d+)?");

    /**
     * 字节数组转IEEE 754
     *
     * @param bytes 长度4或者8
     * @author: 若非
     * @date: 2021/9/10 16:57
     */
    public static BigDecimal bytesToSingle(byte[] bytes) {
        if (bytes.length > 8) {
            throw new ArrayIndexOutOfBoundsException("转化失败，字节超出整型大小！");
        }
        String hex = new BigInteger(bytes).toString(16);
        return hexToSingle(hex, bytes.length * 8);
    }

    /**
     * 字节数组转IEEE 754
     *
     * @param hex
     * @param bitLen 32或者64
     * @author: 若非
     * @date: 2021/9/10 16:57
     */
    private static BigDecimal hexToSingle(String hex, int bitLen) {
        if (StringUtils.isEmpty(hex)) {
            return BigDecimal.valueOf(0);
        }
        if (bitLen == 32) {
            int i = Integer.parseInt(hex, 16);
            float v = Float.intBitsToFloat(i);
            return new BigDecimal(v);
        }
        if (bitLen == 64) {
            long l = Long.parseLong(hex, 16);
            double d = Double.longBitsToDouble(l);
            return new BigDecimal(d);
        }
        return BigDecimal.valueOf(0);
    }

    /**
     * IEEE 754字符串转十六进制字符串
     *
     * @param f
     * @author: 若非
     * @date: 2021/9/10 16:57
     */
    public static String singleToHex(float f) {
        int i = Float.floatToIntBits(f);
        String hex = Integer.toHexString(i);
        return hex;
    }

    /**
     * IEEE 754字符串转十六进制字符串
     *
     * @param d
     * @author: 若非
     * @date: 2021/9/10 16:57
     */
    public static String singleToHex(double d) {
        long l = Double.doubleToRawLongBits(d);
        String hex = Long.toHexString(l);
        return hex;
    }


    //下面测方法是方便c++等没有类似功能的时候做参考
    /**
     * 十六进制字符串转IEEE 754
     *
     * @author: 若非
     * @date: 2021/9/10 16:57
     */
    /*public static BigDecimal hexToSingle(String hex, int bitLen) {
        if (StringUtils.isEmpty(hex) || bitLen != 32 && bitLen != 64) {
            return BigDecimal.valueOf(0);
        }
        String binaryString = StringUtils.fillString(new BigInteger(hex, 16).toString(2), "0", bitLen, true);
        //String binaryString = String.format("%32s", new BigInteger(hex, 16).toString(2)).replace(' ', '0');
        int exponentBitLength = hex.length() == 8 ? 8 : 11;
        int mantissaBitLength = (hex.length() * 4) - exponentBitLength - 1;
        char s = binaryString.charAt(0);//符号位
        String e = binaryString.substring(1, exponentBitLength + 1);
        String m = binaryString.substring(exponentBitLength + 1);
        Double bias = Math.pow(2, exponentBitLength - 1) - 1;
        int exponent = Integer.valueOf(e, 2) - bias.intValue();
        long mantissa = Long.valueOf(m, 2);
        //BigDecimal bigDecimal = (new BigDecimal(1).add(new BigDecimal(mantissa).multiply(new BigDecimal(Math.pow(2, -mantissaBits))))).multiply(new BigDecimal(Math.pow(2, exponent)));
        Double res = (1 + (mantissa * Math.pow(2, -mantissaBitLength))) * Math.pow(2, exponent);
        BigDecimal bigDecimal = new BigDecimal(res);
        if (s == '1') {
            return new BigDecimal(0).subtract(bigDecimal);
        }
        return bigDecimal;
    }*/

    /**
     * IEEE 754字符串转十六进制字符串
     *
     * @author: 若非
     * @date: 2021/9/10 16:57
     */
    /*public static String singleToHex(float fv) {
        String hex = singleToHex(fv);
        return hex;
    }*/

    /**
     * IEEE 754转十六进制字符串
     *
     * @author: 若非
     * @date: 2021/9/10 16:57
     */
    /*public static String singleToHex(float fv) {
        String negative = "0";
        if (fv < 0) {
            negative = "1";
            fv = fv * -1;
        }
        int thirty;
        int integer = Float.valueOf(fv).intValue();
        float decimal = fv - integer;

        String rear; //0-22位存放的值
        String medim = "";//23位到29位
        String integerBinaryString = Integer.toBinaryString(integer);
        String decimalBinaryString = ConvertUtils.unsignedDecimalToBinary(decimal);
        if (integerBinaryString.length() > 1) {
            //左移 30为存1
            thirty = 1;
            int exponent = integerBinaryString.length() - 2;
            rear = integerBinaryString.substring(1) + decimalBinaryString;
            if (rear.length() > 23) {
                rear = rear.substring(0, 23);
            } else {
                for (int i = rear.length(); i <= 22; i++) {
                    rear += "0";
                }
            }
            medim = Integer.toBinaryString(exponent);
            medim = StringUtils.fillString(medim, "0", 7, true);
        } else if ("1".equals(integerBinaryString)) {
            //不需要移动位数 30位0  剩下的补1 指数部分 23-29位
            //127-指数 23-29位正好七位最大值127
            thirty = 0;
            rear = integerBinaryString.substring(1) + decimalBinaryString;
            if (rear.length() > 23) {
                rear = rear.substring(0, 23);
            } else {
                rear = StringUtils.fillString(medim, "0", 22, true);
            }
            medim = "1111111";
        } else {//0 0 1111101 100 1100 1100 1100 1100 1100
            //右移 30位存0
            thirty = 0;
            int index = decimalBinaryString.indexOf("1");//找到第一个为1的位置索引 右移动的位数
            int exponent = 127 - (index + 1);
            rear = decimalBinaryString.substring(index + 1);
            if (rear.length() > 23) {
                rear = rear.substring(0, 23);
            } else {
                rear = StringUtils.fillString(medim, "0", 22, true);
            }
            medim = Integer.toBinaryString(exponent);
            medim = StringUtils.fillString(medim, "0", 7, true);
        }
        String res = "" + negative + thirty + medim + rear;
        StringBuilder sb = new StringBuilder();
        if (res.length() == 32) {
            for (int i = 0; i < 8; i++) {
                String binary = res.substring(4 * i, 4 * i + 4);
                sb.append(Constants.BIN_HEX_TABLE.get(binary));
            }
        }
        return sb.toString();
    }*/
}
