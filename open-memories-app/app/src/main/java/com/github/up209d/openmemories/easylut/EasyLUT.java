package com.github.up209d.openmemories.easylut;

import com.github.up209d.openmemories.easylut.filter.Filter;
import com.github.up209d.openmemories.easylut.filter.FilterNon;
import com.github.up209d.openmemories.easylut.filter.LutFilterFromBitmap;
import com.github.up209d.openmemories.easylut.filter.LutFilterFromResource;

public class EasyLUT {

    public static LutFilterFromResource.Builder fromResourceId() {
        return new LutFilterFromResource.Builder();
    }

    public static LutFilterFromBitmap.Builder fromBitmap() {
        return new LutFilterFromBitmap.Builder();
    }

    public static Filter createNonFilter() {
        return new FilterNon();
    }

}
