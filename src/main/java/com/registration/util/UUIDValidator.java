package com.registration.util;

import java.util.regex.Pattern;

public class UUIDValidator {

    /**
     * Regular expression, in {@link String} form, to match a Version 4 Universally Unique Identifier
     * (UUID), in a case-insensitive fashion.
     */
    public static final String UUID_V4_STRING =
            "[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-4[a-fA-F0-9]{3}-[89abAB][a-fA-F0-9]{3}-[a-fA-F0-9]{12}";
    /**
     * Regular expression to match a Version 4 Universally Unique Identifier (UUID), in a
     * case-insensitive fashion.
     */
    public static final Pattern UUID_V4 = Pattern.compile(UUID_V4_STRING);



    public static boolean isValidUUID(String str) {
        if (str == null) {
            return false;
        }
        return UUID_V4.matcher(str).matches();
    }
}
