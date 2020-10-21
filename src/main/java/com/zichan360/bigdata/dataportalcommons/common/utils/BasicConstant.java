package com.zichan360.bigdata.dataportalcommons.common.utils;

/**
 * 基础的常量类
 *
 * @author sunweihong
 */
public class BasicConstant {
    public static final char CHAR_TAB = '\t';
    public static final char CHAR_ENTER = '\n';
    public static final char CHAR_SOH = '\u0001';

    public static final char CHAR_DOT = '.';
    public static final char CHAR_COMMA = ',';
    public static final char CHAR_PLUS = '+';
    public static final char CHAR_MINUS = '-';
    public static final char CHAR_EQUALS = '=';
    public static final char CHAR_UNDERLINE = '_';

    public static final char CHAR_COLON = ':';
    public static final char CHAR_QUESTION = '?';
    public static final char CHAR_SEMICOLON = ';';
    public static final char CHAR_LESS_THAN = '<';
    public static final char CHAR_MORE_THAN = '>';
    public static final char CHAR_VERTICAL_BAR = '|';
    public static final char CHAR_EXCLAMATION_POINT = '!';
    public static final char CHAR_SINGLE_QUOTATION = '\'';
    public static final char CHAR_DOUBLE_QUOTATION = '\"';

    public static final char CHAR_AT = '@';
    public static final char CHAR_CARET = '^';
    public static final char CHAR_POUND = '#';
    public static final char CHAR_DOLLAR = '$';
    public static final char CHAR_PERCENT = '%';
    public static final char CHAR_ASTERISK = '*';
    public static final char CHAR_AMPERSAND = '&';
    public static final char CHAR_SEPARATION = '·';
    public static final char CHAR_SWUNG_DASH = '~';

    public static final char CHAR_SLASH = '/';
    public static final char CHAR_BACKSLASH = '\\';

    public static final char CHAR_ROUND_BRACKETS_LEFT = '(';
    public static final char CHAR_ROUND_BRACKETS_RIGHT = ')';
    public static final char CHAR_SQUARE_BRACKETS_LEFT = '[';
    public static final char CHAR_SQUARE_BRACKETS_RIGHT = ']';
    public static final char CHAR_CURLY_BRACKETS_LEFT = '{';
    public static final char CHAR_CURLY_BRACKETS_RIGHT = '}';


    public static final String STR_EMPTY = "";
    public static final String STR_TAB = "\t";
    public static final String STR_ENTER = "\n";
    public static final String STR_SOH = "\u0001";

    public static final String STR_DOT = ".";
    public static final String TRANSF_STR_DOT = "\\.";
    public static final String STR_COMMA = ",";
    public static final String STR_PLUS = "+";
    public static final String STR_MINUS = "-";
    public static final String STR_EQUALS = "=";
    public static final String STR_UNDERLINE = "_";

    public static final String STR_COLON = ":";
    public static final String STR_QUESTION = "?";
    public static final String STR_SEMICOLON = ";";
    public static final String STR_LESS_THAN = "<";
    public static final String STR_MORE_THAN = ">";
    public static final String STR_VERTICAL_BAR = "|";
    public static final String STR_EXCLAMATION_POINT = "!";
    public static final String STR_SINGLE_QUOTATION = "\'";
    public static final String STR_DOUBLE_QUOTATION = "\"";

    public static final String STR_AT = "@";
    public static final String STR_CARET = "^";
    public static final String STR_POUND = "#";
    public static final String STR_DOLLAR = "$";
    public static final String STR_PERCENT = "%";
    public static final String STR_ASTERISK = "*";
    public static final String STR_AMPERSAND = "&";
    public static final String STR_SEPARATION = "·";
    public static final String STR_SWUNG_DASH = "~";

    public static final String STR_SLASH = "/";
    public static final String STR_BACKSLASH = "\\";

    public static final String STR_ROUND_BRACKETS_LEFT = "(";
    public static final String STR_ROUND_BRACKETS_RIGHT = ")";
    public static final String STR_SQUARE_BRACKETS_LEFT = "[";
    public static final String STR_SQUARE_BRACKETS_RIGHT = "]";
    public static final String STR_CURLY_BRACKETS_LEFT = "{";
    public static final String STR_CURLY_BRACKETS_RIGHT = "}";

    public static final String REGEX_STR_VERTICAL_BAR = "\\|";
    public static final String CLAIM_USER_NAME = "userName";
    public static final String CLAIM_USER_ID = "userId";
    public static final String CLAIM_REAL_NAME = "realName";
    public static final String AUTHORIZATION_HEADER = "Authorization";

    public static final String DATE_FRM_YYYY_MM_DD_HH_MI_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FRM_YYYYMMDDHHMISS = "yyyyMMddHHmmss";
    public static final String DATE_FRM_YYYY_MM_DD = "yyyy-MM-dd";
    public static final String EXCEL03_SUFFIX = ".xls";
    public static final String EXCEL07_SUFFIX = ".xlsx";
    public static final String RETURN_CODE_KEY = "code";
    public static final String RETURN_MESSAGE_KEY = "message";
    public static final Integer PAGESIZE_MAX = 500;
    public static final Integer PAGESIZE_DEFAULT = 10;
    /**
     * 列元素
     */
    public static final String C_ELEMENT = "c";
    /**
     * 列中属性r
     */
    public static final String R_ATTR = "r";
    /**
     * 列中的v元素
     */
    public static final String V_ELEMENT = "v";
    /**
     * 列中的t元素
     */
    public static final String T_ELEMENT = "t";
    /**
     * 列中属性值
     */
    public static final String S_ATTR_VALUE = "s";
    /**
     * 列中属性值
     */
    public static final String T_ATTR_VALUE = "t";
    /**
     * sheet r:Id前缀
     */
    public static final String RID_PREFIX = "rId";
    /**
     * 行元素
     */
    public static final String ROW_ELEMENT = "row";
    /**
     * 填充字符串
     */
    public static final String CELL_FILL_STR = "@";
    /**
     * 列的最大位数
     */
    public static final int MAX_CELL_BIT = 3;
    /**
     * 科学计数法正则表达式
     */
    public static final String SCIENTIFIC_NOTATION_REGX = "[+-]?[\\d]+([\\.][\\d]*)?([Ee][+-]?[0-9]{0,2})?";

    public static final int TIMEOUT_TIME = 5000;

    public static final String STR_TRUE = "true";

    public static final String STR_FALSE = "false";
    /**
     * redis上消息组名称
     */
    private static final String REDIS_STREAM_LOG_GROUP_NAME="log_group";
    /**
     * redis上消息队列信道名称
     */
    private static final String REDIS_MESSAGE_QUEUE_NAME="log_name";


    private BasicConstant() {
    }
}
