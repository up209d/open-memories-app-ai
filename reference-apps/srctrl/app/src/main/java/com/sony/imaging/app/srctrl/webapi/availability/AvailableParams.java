package com.sony.imaging.app.srctrl.webapi.availability;

import java.util.ArrayList;

/* loaded from: classes.dex */
public class AvailableParams {
    ArrayList<String> names = new ArrayList<>();
    ArrayList<String> types = new ArrayList<>();
    ArrayList<Boolean> rangeFlags = new ArrayList<>();
    ArrayList<String> currents = new ArrayList<>();
    ArrayList<String> availables = new ArrayList<>();

    public void addData(String name, String type, boolean flag, String current, ArrayList<String> available) {
        this.names.add(name);
        this.types.add(type);
        this.rangeFlags.add(Boolean.valueOf(flag));
        this.currents.add(current);
        this.availables.addAll(available);
    }

    public ArrayList<String> getNames() {
        return this.names;
    }

    public String[] getNamesArray() {
        return (String[]) this.names.toArray(new String[0]);
    }

    public ArrayList<String> getTypes() {
        return this.types;
    }

    public String[] getTypesArray() {
        return (String[]) this.types.toArray(new String[0]);
    }

    public ArrayList<Boolean> getRangeFlags() {
        return this.rangeFlags;
    }

    public boolean[] getRangeFlagsArray() {
        boolean[] flagsArray = new boolean[this.rangeFlags.size()];
        for (int i = 0; i < this.rangeFlags.size(); i++) {
            flagsArray[i] = this.rangeFlags.get(i).booleanValue();
        }
        return flagsArray;
    }

    public ArrayList<String> getCurrents() {
        return this.currents;
    }

    public String[] getCurrentsArray() {
        return (String[]) this.currents.toArray(new String[0]);
    }

    public ArrayList<String> getAvailables() {
        return this.availables;
    }

    public String[] getAvailablesArray() {
        return (String[]) this.availables.toArray(new String[0]);
    }
}
