package me.muphy.spring.common;

import me.muphy.spring.annotation.Remind;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public interface Constants {
    String LOG_LEVEL = "app.log.level";
    String SERVER_PORT = "app.server.port";
    @Remind("模板匹配")
    String TEMPLATE_FORMAL = "\\$\\{([^\\}:]+):?([^\\}]*)\\}";
    @Remind("默认配置文件")
    String DEFAULT_PROPERTY_FILE = "application.properties";
    @Remind(value = "环境加载", notice = "如：当前配置环境为dev")
    String ENVIRONMENT = "app.environment";
    @Remind("group(1)表示参数")
    Pattern PARAM_PATTERN = Pattern.compile("\\[([^\\]]*)\\]");
    @Remind("group(1)表示属性，group(2)表示默认值")
    Pattern PROPERTY_PATTERN = Pattern.compile(Constants.TEMPLATE_FORMAL);
    @Remind("group(1)表示字节数，group(2)表示表达式")
    Pattern EXPRESS_PATTERN = Pattern.compile("\\$(\\d*)\\{([^\\}]+)\\}");
    @Remind("判断是不是ASCII")
    Pattern ASCII_PATTERN = Pattern.compile("^[0-9a-zA-Z\\-\\+\\.\\*\\s\\!\\@\\(\\)\\#\\=\\?\\,\\$~\\|\\&\\^\\_]*$");

    @Remind("字符串格式，通常指的是命令或数据的格式")
    enum StringFormal {
        @Remind(value = "十六进制", notice = "如：0123456789ABCDEF")
        HEX,
        @Remind(value = "ASCII码", notice = "如：0-9a-zA-Z...")
        ASCII,
        @Remind(value = "十进制", notice = "如：0123456789")
        DEC,
        @Remind(value = "二进制", notice = "如：101010")
        BIN,
        @Remind(value = "八进制", notice = "如：01234567")
        OTC,
        @Remind(value = "IEEE 754单精度浮点数", notice = "如：47 21 81 00 -> 41345")
        IEEE_754_SINGLE
    }

    @Remind("字节序")
    enum Endian {
        @Remind("小端")
        LITTLE,
        @Remind("大端")
        BIG
    }

    Map<String, Character> BIN_HEX_TABLE = new HashMap<String, Character>() {{
        put("0000", '0');
        put("0001", '1');
        put("0010", '2');
        put("0011", '3');
        put("0100", '4');
        put("0101", '5');
        put("0110", '6');
        put("0111", '7');
        put("1000", '8');
        put("1001", '9');
        put("1010", 'A');
        put("1011", 'B');
        put("1100", 'C');
        put("1101", 'D');
        put("1110", 'E');
        put("1111", 'F');
    }};

    Map<Character, String> HEX_BIN_TABLE = new HashMap<Character, String>() {{
        put('0', "0000");
        put('1', "0001");
        put('2', "0010");
        put('3', "0011");
        put('4', "0100");
        put('5', "0101");
        put('6', "0110");
        put('7', "0111");
        put('8', "1000");
        put('9', "1001");
        put('A', "1010");
        put('B', "1011");
        put('C', "1100");
        put('D', "1101");
        put('E', "1110");
        put('F', "1111");
    }};
}
