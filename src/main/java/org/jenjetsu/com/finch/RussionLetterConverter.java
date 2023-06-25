package org.jenjetsu.com.finch;

import java.util.HashMap;
import java.util.Map;

public class RussionLetterConverter {

    public static final int[] STANDART_DISPLAY = new int[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
    private final static Map<Character, int[]> RUSSION_LETTERS_MAP;
    static {
        RUSSION_LETTERS_MAP = new HashMap<>();
        RUSSION_LETTERS_MAP.put('А', new int[]{0,0,1,0,0,0,1,0,1,0,0,1,1,1,0,0,1,0,1,0,1,0,0,0,1});
        RUSSION_LETTERS_MAP.put('Б', new int[]{1,1,1,1,0,1,0,0,0,0,1,1,1,1,0,1,0,0,1,0,1,1,1,1,0});
        RUSSION_LETTERS_MAP.put('В', new int[]{1,1,1,0,0,1,0,0,1,0,1,1,1,0,0,1,0,0,1,0,1,1,1,0,0});
        RUSSION_LETTERS_MAP.put('Г', new int[]{1,1,1,1,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0});
        RUSSION_LETTERS_MAP.put('Д', new int[]{0,1,1,1,0,0,1,0,1,0,0,1,0,1,0,0,1,1,1,0,1,0,0,0,1});
        RUSSION_LETTERS_MAP.put('Е', new int[]{1,1,1,1,0,1,0,0,0,0,1,1,1,1,0,1,0,0,0,0,1,1,1,1,0});
        RUSSION_LETTERS_MAP.put('Ж', new int[]{1,0,1,0,1,0,1,1,1,0,0,0,1,0,0,0,1,1,1,0,1,0,1,0,1});
        RUSSION_LETTERS_MAP.put('З', new int[]{1,0,1,0,1,0,1,1,1,0,0,0,1,0,0,0,1,1,1,0,1,0,1,0,1});
        RUSSION_LETTERS_MAP.put('И', new int[]{1,0,0,0,1,1,0,0,1,1,1,0,1,0,1,1,1,0,0,1,1,0,0,0,1});
        RUSSION_LETTERS_MAP.put('К', new int[]{1,0,0,1,0,1,0,1,0,0,1,1,0,0,0,1,0,1,0,0,1,0,0,1,0});
        RUSSION_LETTERS_MAP.put('Л', new int[]{0,1,1,1,1,0,1,0,0,1,0,1,0,0,1,0,1,0,0,1,1,1,0,0,1});
        RUSSION_LETTERS_MAP.put('М', new int[]{1,0,0,0,1,1,1,0,1,1,1,0,1,0,1,1,0,0,0,1,1,0,0,0,1});
        RUSSION_LETTERS_MAP.put('Н', new int[]{1,0,0,0,1,1,0,0,0,1,1,1,1,1,1,1,0,0,0,1,1,0,0,0,1});
        RUSSION_LETTERS_MAP.put('О', new int[]{1,1,1,1,1,1,0,0,0,1,1,0,0,0,1,1,0,0,0,1,1,1,1,1,1});
        RUSSION_LETTERS_MAP.put('П', new int[]{1,1,1,1,1,1,0,0,0,1,1,0,0,0,1,1,0,0,0,1,1,0,0,0,1});
        RUSSION_LETTERS_MAP.put('Р', new int[]{1,1,1,1,0,1,0,0,1,0,1,0,0,1,0,1,1,1,1,1,1,0,0,0,0});
        RUSSION_LETTERS_MAP.put('С', new int[]{1,1,1,1,1,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,1,1,1,1});
        RUSSION_LETTERS_MAP.put('Т', new int[]{1,1,1,1,1,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0});
        RUSSION_LETTERS_MAP.put('У', new int[]{1,0,0,1,0,0,1,0,1,0,0,0,1,1,0,0,0,0,1,0,0,1,1,0,0});
        RUSSION_LETTERS_MAP.put('Ф', new int[]{1,1,1,1,1,1,0,1,0,1,1,0,1,0,1,1,1,1,1,1,0,0,1,0,0});
        RUSSION_LETTERS_MAP.put('Х', new int[]{1,0,0,0,1,0,1,0,1,0,0,0,1,0,0,0,1,0,1,0,1,0,0,0,1});
        RUSSION_LETTERS_MAP.put('Ц', new int[]{1,0,0,1,0,1,0,0,1,0,1,0,0,1,0,1,1,1,1,1,0,0,0,0,1});
        RUSSION_LETTERS_MAP.put('Ч', new int[]{1,0,0,0,1,1,0,0,0,1,1,1,1,1,1,0,0,0,0,1,0,0,0,0,1});
        RUSSION_LETTERS_MAP.put('Ш', new int[]{1,0,1,0,1,1,0,1,0,1,1,0,1,0,1,1,0,1,0,1,1,1,1,1,1});
        RUSSION_LETTERS_MAP.put('Щ', new int[]{1,0,1,0,1,1,0,1,0,1,1,0,1,0,1,1,1,1,1,1,0,0,0,0,1});
        RUSSION_LETTERS_MAP.put('Ъ', new int[]{1,1,0,0,0,0,1,0,0,0,0,1,1,1,1,0,1,0,0,1,0,1,1,1,1});
        RUSSION_LETTERS_MAP.put('Ы', new int[]{1,0,0,0,1,1,0,0,0,1,1,1,1,0,1,1,0,1,0,1,1,1,1,0,1});
        RUSSION_LETTERS_MAP.put('Ь', new int[]{1,0,0,0,0,1,0,0,0,0,1,1,1,0,0,1,0,1,0,0,1,1,1,0,0});
        RUSSION_LETTERS_MAP.put('Э', new int[]{0,1,1,1,1,0,0,0,0,1,0,1,1,1,1,0,0,0,0,1,0,1,1,1,1});
        RUSSION_LETTERS_MAP.put('Ю', new int[]{1,0,1,1,1,1,0,1,0,1,1,1,1,0,1,1,0,1,0,1,1,0,1,1,1});
        RUSSION_LETTERS_MAP.put('Я', new int[]{0,0,1,1,1,0,0,1,0,1,0,0,1,1,1,0,1,0,0,1,1,0,0,0,1});
    }

    public static int[] convertToDisplay(Character ch) {
        return RUSSION_LETTERS_MAP.get(ch);
    }
}
