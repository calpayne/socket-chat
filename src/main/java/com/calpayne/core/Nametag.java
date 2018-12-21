package com.calpayne.core;

/**
 *
 * @author Cal Payne
 */
public class Nametag {

    private final String handle;
    private Rank rank;

    public Nametag(String handle) {
        this(handle, Rank.NORMAL);
    }

    public Nametag(String handle, Rank rank) {
        this.handle = handle;
        this.rank = rank;
    }

    public String getHandle() {
        return handle;
    }

    @Override
    public String toString() {
        return rank.getPrefixCode() + handle + rank.getSuffixCode();
    }

}
