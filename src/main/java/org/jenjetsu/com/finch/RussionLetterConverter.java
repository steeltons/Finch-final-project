package org.jenjetsu.com.finch;

import java.util.HashMap;
import java.util.Map;

public class RussionLetterConverter {

    public static final int[] STANDART_DISPLAY = new int[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
    private final static Map<Character, int[]> RUSSION_LETTERS_MAP;
    static {
        RUSSION_LETTERS_MAP = new HashMap<>();
        RUSSION_LETTERS_MAP.put('Б', new int[]{1,1,1,1,0,1,0,0,0,0,1,1,1,1,0,1,0,0,1,0,1,1,1,1,0});
        RUSSION_LETTERS_MAP.put('Г', new int[]{1,1,1,1,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0});
        RUSSION_LETTERS_MAP.put('Д', new int[]{0,1,1,1,0,0,1,0,1,0,0,1,0,1,0,0,1,1,1,0,1,0,0,0,1});
        RUSSION_LETTERS_MAP.put('Ж', new int[]{1,0,1,0,1,0,1,1,1,0,0,0,1,0,0,0,1,1,1,0,1,0,1,0,1});

    }

    public static int[] convertToDisplay(Character ch) {
        return RUSSION_LETTERS_MAP.get(ch);
    }
}
