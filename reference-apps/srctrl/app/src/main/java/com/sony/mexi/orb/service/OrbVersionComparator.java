package com.sony.mexi.orb.service;

import java.util.Comparator;

/* loaded from: classes.dex */
public class OrbVersionComparator implements Comparator<String> {
    @Override // java.util.Comparator
    public int compare(String s1, String s2) {
        if (s1 == null && s2 == null) {
            return 0;
        }
        if (s1 == null) {
            return 1;
        }
        if (s2 == null) {
            return -1;
        }
        if (s1.equals(s2)) {
            return 0;
        }
        String[] ns1 = s1.split("\\.");
        String[] ns2 = s2.split("\\.");
        try {
            if (ns1.length != 2 || ns2.length != 2) {
                return -1;
            }
            int diff = Integer.parseInt(ns2[0]) - Integer.parseInt(ns1[0]);
            if (diff == 0) {
                return Integer.parseInt(ns2[1]) - Integer.parseInt(ns1[1]);
            }
            return diff;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
