package com.calpayne.core;

/**
 *
 * @author Cal Payne
 */
public enum Rank {
    SERVER("<p class=\"server\"><b>", "</b></p>"),
    ADMIN("<p class=\"admin\"><b>", "</b></p>"),
    NORMAL("<p class=\"normal\">", "</p>");

    private final String prefixCode;
    private final String suffixCode;

    private Rank(String prefixCode, String suffixCode) {
        this.prefixCode = prefixCode;
        this.suffixCode = suffixCode;
    }

    public String getPrefixCode() {
        return prefixCode;
    }
    
    public String getSuffixCode() {
        return suffixCode;
    }
}
