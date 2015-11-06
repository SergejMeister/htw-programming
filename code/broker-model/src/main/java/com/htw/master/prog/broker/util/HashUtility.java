package com.htw.master.prog.broker.util;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility Class to hash data.
 */
public final class HashUtility {

    private static final String SHA_512 = "SHA-512";
    private static final String SHA_1 = "SHA-1";

    private HashUtility() {
    }

    public static String hashAsString(String plain) {
        return hash(plain, SHA_1);
    }

    public static byte[] hashAsByte(String plain) {
        return hashToByte(plain, SHA_1);
    }

    public static String hash(String plain, String algorithm) {
        byte[] hash = hashToByte(plain, algorithm);
        return byteHashToHexString(hash);
    }

    private static byte[] hashToByte(String value, String algorithm) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            return md.digest(value.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException();
        }
    }

    private static String byteHashToHexString(byte[] hash) {
        return DatatypeConverter.printHexBinary(hash).toLowerCase();
    }
}
