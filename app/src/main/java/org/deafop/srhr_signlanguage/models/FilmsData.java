package org.deafop.srhr_signlanguage.models;

import org.deafop.srhr_signlanguage.R;

public class FilmsData {
    public static Integer[] drawableArray;
    public static Integer[] id_ = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    public static String[] nameArray = {"City Girl Episode 1: Village Life", "City Girl Episode 2: Journey to The City", "City Girl Episode 3: The City Experience", "City Girl Episode 4: The Going Gets Tough", "City Girl Episode 5: After Party", "City Girl Episode 6: Light at The End of The Tunnel", "City Girl Episode 7: Finding Love", "City Girl Episode 8: Happily Ever After", "Silent Cry", "Poem"};

    static {
        Integer valueOf = Integer.valueOf(R.drawable.ep8);
        drawableArray = new Integer[]{Integer.valueOf(R.drawable.ep1), Integer.valueOf(R.drawable.ep2), Integer.valueOf(R.drawable.ep3), Integer.valueOf(R.drawable.ep4), Integer.valueOf(R.drawable.ep5), Integer.valueOf(R.drawable.ep6), valueOf, valueOf, Integer.valueOf(R.drawable.silent), Integer.valueOf(R.drawable.poem)};
    }
}
