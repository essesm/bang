package com.messes.panorama.bang;

/**
 * Created by panorama on 9/6/14.
 */
public class PickupLine {
    private long key;
    private String line;
    private static long id = 0;

    public PickupLine(String line) {
        this.key = id++;
        this.line = line;
    }

    public long getKey() {
        return key;
    }

    public String getLine() {
        return line;
    }

    public String toString() {
        return "Line [id=" + key + ", line" + line + "]";
    }
}