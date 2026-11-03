package com.example.countryflaggame;

/**
 * Central data source shared by all game modes.
 * Each country name must stay at the same array index as its flag image.
 */
public class FlagData {

    // The 21 country names
    public static final String[] COUNTRY_NAMES = {
            "Algeria",
            "Australia",
            "Belgium",
            "Brazil",
            "Canada",
            "Colombia",
            "Croatia",
            "Denmark",
            "Egypt",
            "Finland",
            "France",
            "Germany",
            "Greece",
            "Portugal",
            "Singapore",
            "Spain",
            "Sweden",
            "Switzerland",
            "Turkey",
            "Ukraine",
            "Zimbabwe"
    };

    // Drawable resource IDs for the 21 flags.
    // Index 0 matches COUNTRY_NAMES[0], index 1 matches COUNTRY_NAMES[1], etc.
    public static final int[] FLAG_IMAGES = {
            R.drawable.flag_of_algeria,
            R.drawable.flag_of_australia,
            R.drawable.flag_of_belgium,
            R.drawable.flag_of_brazil,
            R.drawable.flag_of_canada,
            R.drawable.flag_of_colombia,
            R.drawable.flag_of_croatia,
            R.drawable.flag_of_denmark,
            R.drawable.flag_of_egypt,
            R.drawable.flag_of_finland,
            R.drawable.flag_of_france,
            R.drawable.flag_of_germany,
            R.drawable.flag_of_greece,
            R.drawable.flag_of_portugal,
            R.drawable.flag_of_singapore,
            R.drawable.flag_of_spain,
            R.drawable.flag_of_sweden,
            R.drawable.flag_of_switzerland,
            R.drawable.flag_of_turkey,
            R.drawable.flag_of_ukraine,
            R.drawable.flag_of_zimbabwe
    };
}