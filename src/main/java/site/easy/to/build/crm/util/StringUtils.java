package site.easy.to.build.crm.util;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class StringUtils {
    public static BigDecimal parseDouble(String input) throws NumberFormatException {
        input = input.replace(",", ".").replace("\n", "");
        if (!isNumeric(input)) {
            throw new NumberFormatException(input + " is not a number.");
        }
        return BigDecimal.valueOf(Double.parseDouble(input));
    }

    public static boolean isNumeric(String str) {
        try {
            str = str.replace(",", ".").replace("\n", "");
            double d = Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String replaceCharToCamelCase(String input, char replaced) {
        if(!input.contains(String.valueOf(replaced))){
            return input;
        }

        StringBuilder result = new StringBuilder();
        boolean capitalizeNextChar = false;
        for (int i = 0; i < input.length(); i++) {
            char currentChar = input.charAt(i);

            if (currentChar == replaced) {
                capitalizeNextChar = true;
            } else {
                if (capitalizeNextChar) {
                    result.append(Character.toUpperCase(currentChar));
                    capitalizeNextChar = false;
                } else {
                    result.append(Character.toLowerCase(currentChar));
                }
            }
        }

        return result.toString();
    }

    public static String capitalizeFirstLetter(String input) {
        return Character.toUpperCase(input.charAt(0)) + input.substring(1);
    }

    public static boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
