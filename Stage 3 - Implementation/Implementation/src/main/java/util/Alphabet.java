package util;

import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

/**
 * Class that represents the alphabet.
 * @author Jordan Jones
 */
public class Alphabet {

    //CONSTANTS
    private static final ArrayList<String> CHARACTERS = new ArrayList<>(Arrays
        .asList("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m",
            "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"));

    //Attributes
    private boolean isUpperCase;

    /**
     * Creates an alphabet object.
     * @param isUpperCase True if upper case
     */
    public Alphabet(boolean isUpperCase) {
        setIsUpperCase(isUpperCase);
    }

    /**
     * Sets if the alphabet is upper case.
     * @param isUpperCase True if upper case
     */
    private void setIsUpperCase(boolean isUpperCase) {
        this.isUpperCase = isUpperCase;
    }

    /**
     * Gets if the alphabet is uppercase.
     * @return True if the alphabet is uppercase
     */
    private boolean getIsUpperCase() {
        return isUpperCase;
    }

    /**
     * Gets the letter of the given index.
     * @param index Index for the letter
     * @return Letter
     */
    public String letter(int index) {
        StringBuilder string = new StringBuilder();
        while (index >= 0) {
            int value = index % CHARACTERS.size();
            string.insert(0, CHARACTERS.get(value));
            index = (index / CHARACTERS.size()) - 1;
        }
        if (getIsUpperCase()) {
            return string.toString().toUpperCase();
        } else {
            return string.toString().toLowerCase();
        }
    }

    /**
     * Gets the index of the given letter.
     * @param letter Letter for the index
     * @return Index
     */
    public int index(@NotNull String letter) {
        char[] characters = new StringBuilder(letter).reverse().toString()
            .toCharArray();
        int index = characterIndex(characters[0]);
        for (int i = 1; i < characters.length; i++) {
            index += (CHARACTERS.indexOf(String.valueOf(characters[i])) + 1)
                * Math.pow(CHARACTERS.size(), i);
        }
        return index;
    }

    /**
     * Gets the index of the character.
     * @param character Character to get index of
     * @return Index of the character
     */
    private int characterIndex(char character) {
        return CHARACTERS.indexOf(String.valueOf(character).toLowerCase());
    }

}
