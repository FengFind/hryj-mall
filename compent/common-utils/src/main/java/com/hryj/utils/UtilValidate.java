package com.hryj.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Map;

/**
 * @author 王光银
 * @className: UtilValidate
 * @description:
 * @create 2018/6/30 0030 11:00
 **/
@Slf4j
public class UtilValidate {



    public static final String module = UtilValidate.class.getName();

    private UtilValidate() {}

    /** boolean specifying by default whether or not it is okay for a String to be empty */
    private static final boolean defaultEmptyOK = true;

    /** digit characters */
    public static final String digits = "0123456789";

    /** hex digit characters */
    private static final String hexDigits = digits + "abcdefABCDEF";

    /** lower-case letter characters */
    public static final String lowercaseLetters = "abcdefghijklmnopqrstuvwxyz";

    /** upper-case letter characters */
    public static final String uppercaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /** letter characters */
    public static final String letters = lowercaseLetters + uppercaseLetters;

    /** whitespace characters */
    private static final String whitespace = " \t\n\r";

    /** decimal point character differs by language and culture */
    public static final String decimalPointDelimiter = ".";

    /** non-digit characters which are allowed in phone numbers */
    public static final String phoneNumberDelimiters = "()- ";

    /** characters which are allowed in US phone numbers */
    public static final String validUSPhoneChars = digits + phoneNumberDelimiters;

    /** characters which are allowed in international phone numbers(a leading + is OK) */
    public static final String validWorldPhoneChars = digits + phoneNumberDelimiters + "+";

    /** non-digit characters which are allowed in Social Security Numbers */
    public static final String SSNDelimiters = "- ";

    /** characters which are allowed in Social Security Numbers */
    public static final String validSSNChars = digits + SSNDelimiters;

    /** U.S. Social Security Numbers have 9 digits. They are formatted as 123-45-6789. */
    public static final int digitsInSocialSecurityNumber = 9;

    /** U.S. phone numbers have 10 digits. They are formatted as 123 456 7890 or(123) 456-7890. */
    public static final int digitsInUSPhoneNumber = 10;
    public static final int digitsInUSPhoneAreaCode = 3;
    public static final int digitsInUSPhoneMainNumber = 7;

    /** non-digit characters which are allowed in ZIP Codes */
    public static final String ZipCodeDelimiters = "-";

    /** our preferred delimiter for reformatting ZIP Codes */
    public static final String ZipCodeDelimeter = "-";

    /** characters which are allowed in Social Security Numbers */
    public static final String validZipCodeChars = digits + ZipCodeDelimiters;

    /** U.S. ZIP codes have 5 or 9 digits. They are formatted as 12345 or 12345-6789. */
    public static final int digitsInZipCode1 = 5;

    /** U.S. ZIP codes have 5 or 9 digits. They are formatted as 12345 or 12345-6789. */
    public static final int digitsInZipCode2 = 9;

    /** non-digit characters which are allowed in credit card numbers */
    public static final String creditCardDelimiters = " -";

    private static final String isNotEmptyMsg = "This field cannot be empty, please enter a value.";
    private static final String isStateCodeMsg = "The State Code must be a valid two character U.S. state abbreviation(like CA for California).";
    private static final String isContiguousStateCodeMsg = "The State Code must be a valid two character U.S. state abbreviation for one of the 48 contiguous United States (like CA for California).";
    private static final String isZipCodeMsg = "The ZIP Code must be a 5 or 9 digit U.S. ZIP Code(like 94043).";
    private static final String isUSPhoneMsg = "The US Phone must be a 10 digit U.S. phone number(like 415-555-1212).";
    private static final String isUSPhoneAreaCodeMsg = "The Phone Number Area Code must be 3 digits.";
    private static final String isUSPhoneMainNumberMsg = "The Phone Number must be 7 digits.";
    private static final String isContiguousZipCodeMsg = "Zip Code is not a valid Zip Code for one of the 48 contiguous United States .";
    private static final String isInternationalPhoneNumberMsg = "The World Phone must be a valid international phone number.";
    private static final String isSSNMsg = "The SSN must be a 9 digit U.S. social security number(like 123-45-6789).";
    private static final String isEmailMsg = "The Email must be a valid email address(like john@email.com). Please re-enter it now.";
    private static final String isAnyCardMsg = "The credit card number is not a valid card number.";
    private static final String isCreditCardPrefixMsg = " is not a valid ";
    private static final String isCreditCardSuffixMsg = " credit card number.";
    private static final String isDayMsg = "The Day must be a day number between 1 and 31. ";
    private static final String isMonthMsg = "The Month must be a month number between 1 and 12. ";
    private static final String isYearMsg = "The Year must be a 2 or 4 digit year number. ";
    private static final String isDatePrefixMsg = "The Day, Month, and Year for ";
    private static final String isDateSuffixMsg = " do not form a valid date.  Please reenter them now.";
    private static final String isHourMsg = "The Hour must be a number between 0 and 23.";
    private static final String isMinuteMsg = "The Minute must be a number between 0 and 59.";
    private static final String isSecondMsg = "The Second must be a number between 0 and 59.";
    private static final String isTimeMsg = "The Time must be a valid time formed like: HH:MM or HH:MM:SS.";
    private static final String isDateMsg = "The Date must be a valid date formed like: MM/YY, MM/YYYY, MM/DD/YY, or MM/DD/YYYY.";
    private static final String isDateAfterToday = "The Date must be a valid date after today, and formed like: MM/YY, MM/YYYY, MM/DD/YY, or MM/DD/YYYY.";
    private static final String isIntegerMsg = "The Number must be a valid unsigned whole decimal number.";
    private static final String isSignedIntegerMsg = "The Number must be a valid signed whole decimal number.";
    private static final String isLongMsg = "The Number must be a valid unsigned whole decimal number.";
    private static final String isSignedLongMsg = "The Number must be a valid signed whole decimal number.";
    private static final String isFloatMsg = "The Number must be a valid unsigned decimal number.";
    private static final String isSignedFloatMsg = "The Number must be a valid signed decimal number.";
    private static final String isSignedDoubleMsg = "The Number must be a valid signed decimal number.";

    /** An array of ints representing the number of days in each month of the year.
     *  Note: February varies depending on the year */
    public static final int[] daysInMonth = {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    /** Delimiter for USStateCodes String */
    public static final String USStateCodeDelimiter = "|";

    /** Valid U.S. Postal Codes for states, territories, armed forces, etc.
     * See http://www.usps.gov/ncsc/lookups/abbr_state.txt. */
    public static final String USStateCodes = "AL|AK|AS|AZ|AR|CA|CO|CT|DE|DC|FM|FL|GA|GU|HI|ID|IL|IN|IA|KS|KY|LA|ME|MH|MD|MA|MI|MN|MS|MO|MT|NE|NV|NH|NJ|NM|NY|NC|ND|MP|OH|OK|OR|PW|PA|PR|RI|SC|SD|TN|TX|UT|VT|VI|VA|WA|WV|WI|WY|AE|AA|AE|AE|AP";

    /** Valid contiguous U.S. postal codes */
    public static final String ContiguousUSStateCodes = "AL|AZ|AR|CA|CO|CT|DE|DC|FL|GA|ID|IL|IN|IA|KS|KY|LA|ME|MD|MA|MI|MN|MS|MO|MT|NE|NV|NH|NJ|NM|NY|NC|ND|OH|OK|OR|PA|RI|SC|SD|TN|TX|UT|VT|VA|WA|WV|WI|WY";

    public static boolean areEqual(Object obj, Object obj2) {
        if (obj == null) {
            return obj2 == null;
        } else {
            return obj.equals(obj2);
        }
    }

    public static boolean idIsValid(Number id) {
        if (id == null) {
            return false;
        }
        if (id instanceof Long && id.longValue() <= 0L) {
            return false;
        }
        if (id instanceof Integer && id.intValue() <= 0L) {
            return false;
        }
        if (id instanceof Double && id.doubleValue() <= 0d) {
            return false;
        }
        if (id instanceof Float && id.floatValue() <= 0f) {
            return false;
        }
        if (id instanceof Short && id.shortValue() <= 0) {
            return false;
        }
        return true;
    }


    /** Check whether string s is empty. */
    public static boolean isEmpty(String s) {
        return (s == null) || s.length() == 0;
    }

    /** Check whether collection c is empty. */
    public static <E> boolean isEmpty(Collection<E> c) {
        return (c == null) || c.isEmpty();
    }

    /** Check whether collection c is empty. */
    public static <K, V> boolean isEmpty(Map<K, V> map) {
        return (map == null) || map.isEmpty();
    }

    /** Check whether charsequence c is empty. */
    public static <E> boolean isEmpty(CharSequence c) {
        return (c == null) || c.length() == 0;
    }

    /** Check whether string s is NOT empty. */
    public static boolean isNotEmpty(String s) {
        return (s != null) && s.length() > 0;
    }

    /** Check whether collection c is NOT empty. */
    public static <E> boolean isNotEmpty(Collection<E> c) {
        return (c != null) && !c.isEmpty();
    }

    /** Check whether map c is NOT empty. */
    public static <K, V> boolean isNotEmpty(Map<K, V> map) {
        return (map != null) && !map.isEmpty();
    }

    /** Check whether charsequence c is NOT empty. */
    public static <E> boolean isNotEmpty(CharSequence c) {
        return ((c != null) && (c.length() > 0));
    }

    public static boolean isString(Object obj) {
        return ((obj != null) && (obj instanceof java.lang.String));
    }

    /** Returns true if string s is empty or whitespace characters only. */
    public static boolean isWhitespace(String s) {
        // Is s empty?
        if (isEmpty(s)) {
            return true;
        }

        // Search through string's characters one by one
        // until we find a non-whitespace character.
        // When we do, return false; if we don't, return true.
        for (int i = 0; i < s.length(); i++) {
            // Check that current character isn't whitespace.
            char c = s.charAt(i);

            if (whitespace.indexOf(c) == -1) {
                return false;
            }
        }
        // All characters are whitespace.
        return true;
    }

    /** Removes all characters which appear in string bag from string s. */
    public static String stripCharsInBag(String s, String bag) {
        int i;
        StringBuilder stringBuilder = new StringBuilder("");
        // Search through string's characters one by one.
        // If character is not in bag, append to returnString.
        for (i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (bag.indexOf(c) == -1) {
                stringBuilder.append(c);
            }
        }
        return stringBuilder.toString();
    }

    /** Removes all characters which do NOT appear in string bag from string s. */
    public static String stripCharsNotInBag(String s, String bag) {
        int i;
        StringBuilder stringBuilder = new StringBuilder("");

        // Search through string's characters one by one.
        // If character is in bag, append to returnString.
        for (i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (bag.indexOf(c) != -1) {
                stringBuilder.append(c);
            }
        }
        return stringBuilder.toString();
    }

    /** Removes all whitespace characters from s.
     *  Member whitespace(see above) defines which characters are considered whitespace. */
    public static String stripWhitespace(String s) {
        return stripCharsInBag(s, whitespace);
    }

    /** Returns true if single character c(actually a string) is contained within string s. */
    public static boolean charInString(char c, String s) {
        return (s.indexOf(c) != -1);
    }

    /** Removes initial(leading) whitespace characters from s.
     *  Member whitespace(see above) defines which characters are considered whitespace. */
    public static String stripInitialWhitespace(String s) {
        int i = 0;
        while ((i < s.length()) && charInString(s.charAt(i), whitespace)) {
            i++;
        }
        return s.substring(i);
    }

    /** Returns true if character c is an English letter (A .. Z, a..z).
     *
     *  NOTE: Need i18n version to support European characters.
     *  This could be tricky due to different character
     *  sets and orderings for various languages and platforms. */
    public static boolean isLetter(char c) {
        return Character.isLetter(c);
    }

    /** Returns true if character c is a digit (0 .. 9). */
    public static boolean isDigit(char c) {
        return Character.isDigit(c);
    }

    /** Returns true if character c is a letter or digit. */
    public static boolean isLetterOrDigit(char c) {
        return Character.isLetterOrDigit(c);
    }

    /** Returns true if character c is a letter or digit. */
    public static boolean isHexDigit(char c) {
        return hexDigits.indexOf(c) >= 0;
    }

    /** Returns true if all characters in string s are numbers.
     *
     *  Accepts non-signed integers only. Does not accept floating
     *  point, exponential notation, etc.
     */
    public static boolean isInteger(String s) {
        if (isEmpty(s)) {
            return defaultEmptyOK;
        }

        // Search through string's characters one by one
        // until we find a non-numeric character.
        // When we do, return false; if we don't, return true.
        for (int i = 0; i < s.length(); i++) {
            // Check that current character is number.
            char c = s.charAt(i);

            if (!isDigit(c)) {
                return false;
            }
        }

        // All characters are numbers.
        return true;
    }

    /** Returns true if all characters are numbers;
     *  first character is allowed to be + or - as well.
     *
     *  Does not accept floating point, exponential notation, etc.
     */
    public static boolean isSignedInteger(String s) {
        if (isEmpty(s)) {
            return defaultEmptyOK;
        }
        try {
            Integer.parseInt(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /** Returns true if all characters are numbers;
     *  first character is allowed to be + or - as well.
     *
     *  Does not accept floating point, exponential notation, etc.
     */
    public static boolean isSignedLong(String s) {
        if (isEmpty(s)) {
            return defaultEmptyOK;
        }
        try {
            Long.parseLong(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /** Returns true if string s is an integer > 0. NOTE: using the Java Long object for greatest precision */
    public static boolean isPositiveInteger(String s) {
        if (isEmpty(s)) {
            return defaultEmptyOK;
        }

        try {
            long temp = Long.parseLong(s);

            if (temp > 0) return true;
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /** Returns true if string s is an integer >= 0. */
    public static boolean isNonnegativeInteger(String s) {
        if (isEmpty(s)) {
            return defaultEmptyOK;
        }
        try {
            int temp = Integer.parseInt(s);
            if (temp >= 0) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /** Returns true if string s is an integer < 0. */
    public static boolean isNegativeInteger(String s) {
        if (isEmpty(s)) {
            return defaultEmptyOK;
        }
        try {
            int temp = Integer.parseInt(s);
            if (temp < 0) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /** Returns true if string s is an integer <= 0. */
    public static boolean isNonpositiveInteger(String s) {
        if (isEmpty(s)) {
            return defaultEmptyOK;
        }

        try {
            int temp = Integer.parseInt(s);
            if (temp <= 0) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /** True if string s is an unsigned floating point(real) number.
     *
     *  Also returns true for unsigned integers. If you wish
     *  to distinguish between integers and floating point numbers,
     *  first call isInteger, then call isFloat.
     *
     *  Does not accept exponential notation.
     */
    public static boolean isFloat(String s) {
        if (isEmpty(s)) {
            return defaultEmptyOK;
        }

        boolean seenDecimalPoint = false;

        if (s.startsWith(decimalPointDelimiter)) {
            return false;
        }

        // Search through string's characters one by one
        // until we find a non-numeric character.
        // When we do, return false; if we don't, return true.
        for (int i = 0; i < s.length(); i++) {
            // Check that current character is number.
            char c = s.charAt(i);

            if (c == decimalPointDelimiter.charAt(0)) {
                if (!seenDecimalPoint) {
                    seenDecimalPoint = true;
                } else {
                    return false;
                }
            } else {
                if (!isDigit(c)) {
                    return false;
                }
            }
        }
        // All characters are numbers.
        return true;
    }

    /** General routine for testing whether a string is a float.
     */
    public static boolean isFloat(String s, boolean allowNegative, boolean allowPositive, int minDecimal, int maxDecimal) {
        if (isEmpty(s)) {
            return defaultEmptyOK;
        }

        try {
            float temp = Float.parseFloat(s);
            if (!allowNegative && temp < 0) {
                return false;
            }
            if (!allowPositive && temp > 0) {
                return false;
            }
            int decimalPoint = s.indexOf(".");
            if (decimalPoint == -1) {
                if (minDecimal > 0) {
                    return false;
                }
                return true;
            }
            // 1.2345; length=6; point=1; num=4
            int numDecimals = s.length() - decimalPoint - 1;
            if (minDecimal >= 0 && numDecimals < minDecimal) {
                return false;
            }
            if (maxDecimal >= 0 && numDecimals > maxDecimal) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /** General routine for testing whether a string is a double.
     */
    public static boolean isDouble(String s, boolean allowNegative, boolean allowPositive, int minDecimal, int maxDecimal) {
        if (isEmpty(s)) {
            return defaultEmptyOK;
        }

        try {
            double temp = Double.parseDouble(s);
            if (!allowNegative && temp < 0) {
                return false;
            }
            if (!allowPositive && temp > 0) {
                return false;
            }
            int decimalPoint = s.indexOf(".");
            if (decimalPoint == -1) {
                if (minDecimal > 0) {
                    return false;
                }
                return true;
            }
            // 1.2345; length=6; point=1; num=4
            int numDecimals = s.length() - decimalPoint - 1;
            if (minDecimal >= 0 && numDecimals < minDecimal) {
                return false;
            }
            if (maxDecimal >= 0 && numDecimals > maxDecimal) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /** True if string s is a signed or unsigned floating point
     *  (real) number. First character is allowed to be + or -.
     *
     *  Also returns true for unsigned integers. If you wish
     *  to distinguish between integers and floating point numbers,
     *  first call isSignedInteger, then call isSignedFloat.
     */
    public static boolean isSignedFloat(String s) {
        if (isEmpty(s)) return defaultEmptyOK;
        try {
            Float.parseFloat(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /** True if string s is a signed or unsigned floating point
     *  (real) number. First character is allowed to be + or -.
     *
     *  Also returns true for unsigned integers. If you wish
     *  to distinguish between integers and floating point numbers,
     *  first call isSignedInteger, then call isSignedFloat.
     */
    public static boolean isSignedDouble(String s) {
        if (isEmpty(s)) {
            return defaultEmptyOK;
        }
        try {
            Double.parseDouble(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /** Returns true if string s is letters only.
     *
     *  NOTE: This should handle i18n version to support European characters, etc.
     *  since it now uses Character.isLetter()
     */
    public static boolean isAlphabetic(String s) {
        if (isEmpty(s)) {
            return defaultEmptyOK;
        }

        // Search through string's characters one by one
        // until we find a non-alphabetic character.
        // When we do, return false; if we don't, return true.
        for (int i = 0; i < s.length(); i++) {
            // Check that current character is letter.
            char c = s.charAt(i);
            if (!isLetter(c)) {
                return false;
            }
        }

        // All characters are letters.
        return true;
    }


    /* ================== METHODS TO CHECK VARIOUS FIELDS. ==================== */



    /** isZIPCode returns true if string s is a valid U.S. ZIP code.  Must be 5 or 9 digits only. */
    public static boolean isZipCode(String s) {
        if (isEmpty(s)) {
            return defaultEmptyOK;
        }
        String normalizedZip = stripCharsInBag(s, ZipCodeDelimiters);
        return (isInteger(normalizedZip) && ((normalizedZip.length() == digitsInZipCode1) || (normalizedZip.length() == digitsInZipCode2)));
    }


    /** Return true if s is a valid U.S. Postal Code (abbreviation for state). */
    public static boolean isStateCode(String s) {
        if (isEmpty(s)) {
            return defaultEmptyOK;
        }
        return ((USStateCodes.indexOf(s) != -1) && (s.indexOf(USStateCodeDelimiter) == -1));
    }

    /** isUrl returns true if the string contains ://
     * @param s String to validate
     * @return true if s contains ://
     */
    public static boolean isUrl(String s) {
        if (isEmpty(s)) {
            return defaultEmptyOK;
        }
        if (s.indexOf("://") != -1) {
            return true;
        }

        return false;
    }

    /** isYear returns true if string s is a valid
     *  Year number.  Must be 2 or 4 digits only.
     *
     *  For Year 2000 compliance, you are advised
     *  to use 4-digit year numbers everywhere.
     */
    public static boolean isYear(String s) {
        if (isEmpty(s)) {
            return defaultEmptyOK;
        }

        if (!isNonnegativeInteger(s)) {
            return false;
        }
        return ((s.length() == 2) || (s.length() == 4));
    }

    /** isIntegerInRange returns true if string s is an integer
     *  within the range of integer arguments a and b, inclusive.
     */
    public static boolean isIntegerInRange(String s, int a, int b) {
        if (isEmpty(s)) {
            return defaultEmptyOK;
        }
        // Catch non-integer strings to avoid creating a NaN below,
        // which isn't available on JavaScript 1.0 for Windows.
        if (!isSignedInteger(s)) {
            return false;
        }
        // Now, explicitly change the type to integer via parseInt
        // so that the comparison code below will work both on
        // JavaScript 1.2(which typechecks in equality comparisons)
        // and JavaScript 1.1 and before(which doesn't).
        int num = Integer.parseInt(s);

        return ((num >= a) && (num <= b));
    }

    /** isMonth returns true if string s is a valid month number between 1 and 12. */
    public static boolean isMonth(String s) {
        if (isEmpty(s)) {
            return defaultEmptyOK;
        }
        return isIntegerInRange(s, 1, 12);
    }

    /** isDay returns true if string s is a valid day number between 1 and 31. */
    public static boolean isDay(String s) {
        if (isEmpty(s)) {
            return defaultEmptyOK;
        }
        return isIntegerInRange(s, 1, 31);
    }

    /** Given integer argument year, returns number of days in February of that year. */
    public static int daysInFebruary(int year) {
        // February has 29 days in any year evenly divisible by four,
        // EXCEPT for centurial years which are not also divisible by 400.
        return (((year % 4 == 0) && ((!(year % 100 == 0)) || (year % 400 == 0))) ? 29 : 28);
    }

    /** isHour returns true if string s is a valid number between 0 and 23. */
    public static boolean isHour(String s) {
        if (isEmpty(s)) {
            return defaultEmptyOK;
        }
        return isIntegerInRange(s, 0, 23);
    }

    /** isMinute returns true if string s is a valid number between 0 and 59. */
    public static boolean isMinute(String s) {
        if (isEmpty(s)) {
            return defaultEmptyOK;
        }
        return isIntegerInRange(s, 0, 59);
    }

    /** isSecond returns true if string s is a valid number between 0 and 59. */
    public static boolean isSecond(String s) {
        if (isEmpty(s)) {
            return defaultEmptyOK;
        }
        return isIntegerInRange(s, 0, 59);
    }

    /** isDate returns true if string arguments year, month, and day form a valid date. */
    public static boolean isDate(String year, String month, String day) {
        // catch invalid years(not 2- or 4-digit) and invalid months and days.
        if (!(isYear(year) && isMonth(month) && isDay(day))) {
            return false;
        }

        int intYear = Integer.parseInt(year);
        int intMonth = Integer.parseInt(month);
        int intDay = Integer.parseInt(day);

        // catch invalid days, except for February
        if (intDay > daysInMonth[intMonth - 1]) {
            return false;
        }
        if ((intMonth == 2) && (intDay > daysInFebruary(intYear))) {
            return false;
        }
        return true;
    }

    /** isDate returns true if string argument date forms a valid date. */
    public static boolean isDate(String date) {
        if (isEmpty(date)) {
            return defaultEmptyOK;
        }
        String month;
        String day;
        String year;
        int dateSlash1 = date.indexOf("/");
        int dateSlash2 = date.lastIndexOf("/");

        if (dateSlash1 <= 0 || dateSlash1 == dateSlash2) {
            return false;
        }
        month = date.substring(0, dateSlash1);
        day = date.substring(dateSlash1 + 1, dateSlash2);
        year = date.substring(dateSlash2 + 1);
        return isDate(year, month, day);
    }


    /** isTime returns true if string arguments hour, minute, and second form a valid time. */
    public static boolean isTime(String hour, String minute, String second) {
        // catch invalid years(not 2- or 4-digit) and invalid months and days.
        if (isHour(hour) && isMinute(minute) && isSecond(second)) {
            return true;
        }
        return false;
    }

    /** isTime returns true if string argument time forms a valid time. */
    public static boolean isTime(String time) {
        if (isEmpty(time)) {
            return defaultEmptyOK;
        }
        String hour;
        String minute;
        String second;

        int timeColon1 = time.indexOf(":");
        int timeColon2 = time.lastIndexOf(":");

        if (timeColon1 <= 0) {
            return false;
        }
        hour = time.substring(0, timeColon1);
        if (timeColon1 == timeColon2) {
            minute = time.substring(timeColon1 + 1);
            second = "0";
        } else {
            minute = time.substring(timeColon1 + 1, timeColon2);
            second = time.substring(timeColon2 + 1);
        }
        return isTime(hour, minute, second);
    }

    public static boolean isEmpty(Object value) {
        if (value == null) {
            return true;
        }
        if (value instanceof String) {
            return isEmpty((String) value);
        }
        if (value instanceof Collection) {
            return ((Collection) value).size() == 0;
        }
        if (value instanceof Map) {
            return ((Map) value).size() == 0;
        }
        if (value instanceof CharSequence) {
            return ((CharSequence) value).length() == 0;
        }
        // These types would flood the log
        // Number covers: BigDecimal, BigInteger, Byte, Double, Float, Integer, Long, Short
        if (value instanceof Boolean) {
            return false;
        }
        if (value instanceof Number) {
            return false;
        }
        if (value instanceof Character) {
            return false;
        }
        if (value instanceof java.util.Date) {
            return false;
        }
        if (log.isDebugEnabled()) {
            log.debug("In UtilValidate.isEmpty(Object value) returning false for " + value.getClass() + " Object.", module);
        }
        return false;
    }
}
