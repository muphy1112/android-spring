package me.muphy.spring.util;

import me.muphy.spring.annotation.Remind;
import me.muphy.spring.common.Constants;

import java.math.BigDecimal;
import java.math.BigInteger;

public class ConvertUtils {

    public static int byteToInt(byte b) {
        if (b < 0) {
            return b + 256;
        }
        return b;
    }

    public static int bytesToInt(byte[] bytes) {
        int num = 0;
        if (bytes == null) {
            return 0;
        }
        for (int i = 0; i < bytes.length; i++) {
            num = byteToInt(bytes[i]) + (num << 8);
        }
        return num;
    }

    @Remind("length：一字节长度位2")
    public static String toHexString(long value, int length) {
        String hexString = Long.toHexString(value);
        if (hexString.length() % 2 == 1) {
            hexString = '0' + hexString;
        }
        if (length % 2 == 1) {
            length += 1;
        }
        return StringUtils.fillString(hexString, "0", length, true);
    }

    @Remind("length：一字节长度位2")
    public static String toHexString(long value, int length, boolean revers) {
        String hexString = toHexString(value, length);
        if (revers && StringUtils.isNotEmpty(hexString)) {
            if (hexString.length() % 2 == 1) {
                hexString = '0' + hexString;
            }
            char[] chars = new char[hexString.length()];
            char[] charArray = hexString.toCharArray();
            for (int i = charArray.length - 1, j = 0; i >= 0; i -= 2) {
                chars[j++] = charArray[i - 1];
                chars[j++] = charArray[i];
            }
            return String.valueOf(chars);
        }
        return hexString;
    }

    @Remind("字节转字符串")
    public static String bytesToString(byte[] bytes, String formal) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }
        formal = StringUtils.nvl(formal, Constants.StringFormal.ASCII.toString()).toUpperCase();
        Constants.StringFormal stringFormal = Constants.StringFormal.valueOf(formal);
        String string = bytesToString(bytes, stringFormal);
        return string;
    }

    @Remind("字节转字符串")
    public static String bytesToString(byte[] bytes, Constants.StringFormal formal) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }
        switch (formal) {
            case HEX:
                return new BigInteger(bytes).toString(16);
            case IEEE_754_SINGLE:
                BigDecimal single = IEEE754Utils.bytesToSingle(bytes);
                return String.valueOf(single);
            case ASCII:
                return new String(bytes);
            default:
        }
        return new String(bytes);
    }

    // new BigInteger(hex, 16).toByteArray()
    public static byte[] hexToByteArray(String hex) {
        if (StringUtils.isEmpty(hex)) {
            return new byte[0];
        }
        hex = hex.replaceAll("\\s+", "");
        if (hex.length() % 2 == 1) {
            hex = '0' + hex;
        }
        int l = hex.length();
        byte[] data = new byte[l / 2];
        for (int i = 0; i < l; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4) + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }

    //以免忘记
    @Remind("以免忘记")
    public static byte[] asciiToByteArray(String ascii) {
        if (StringUtils.isEmpty(ascii)) {
            return new byte[0];
        }
        return ascii.getBytes();
    }

    public static String hexToBinaryString(String hex) {
        StringBuilder sb = new StringBuilder();
        String hexFormal = StringUtils.toHexFormal(hex);
        for (char c : hexFormal.toCharArray()) {
            sb.append(hexToBinaryString(c));
        }
        return sb.toString();
    }

    public static String hexToBinaryString(char c) {
        String s = Constants.HEX_BIN_TABLE.get(c);
        if (s == null) {
            return "";
        }
        return s;
        //String[] hexBinaryTable = {"0000", "0001", "0010", "0011", "0100", "0101", "0110", "0111", "1000", "1001", "1010", "1011", "1100", "1101", "1110", "1111"};
        // byte b = Byte.parseByte(String.valueOf(c), 16);
        // if (b > 15 || b < 0) {
        //     return "";
        // }
        // return hexBinaryTable[b];
    }

    //小数部分转换为二进制
    public static String unsignedDecimalToBinary(float fv) {
        int fi = Float.valueOf(fv).intValue();
        float f = fv - fi;
        StringBuilder stringBuilder = new StringBuilder();
        int i = 0;
        while (f * 2 != 0) {
            f = f * 2;
            if (f >= 1) {
                stringBuilder.append(1);
                f = f - (int) f;
            } else {
                stringBuilder.append(0);
            }
            if (++i >= 23) {
                break;
            }
        }
        return stringBuilder.toString();
    }

}
