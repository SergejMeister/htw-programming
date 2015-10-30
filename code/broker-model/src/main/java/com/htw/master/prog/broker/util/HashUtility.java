package com.htw.master.prog.broker.util;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility Class to hash data.
 */
public final class HashUtility {

    private static final String SHA_512 = "SHA-512";

    private HashUtility() {
    }

    public static String hashAsString(String plain) throws NoSuchAlgorithmException {
        return hash(plain, SHA_512);
    }

    public static byte[] hashAsByte(String plain) throws NoSuchAlgorithmException {
        return hashToByte(plain, SHA_512);
    }

    public static String hash(String plain, String algorithm) throws NoSuchAlgorithmException {
        byte[] hash = hashToByte(plain, algorithm);
        return byteHashToHexString(hash);
    }

    private static byte[] hashToByte(String value, String algorithm) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(algorithm);
        return md.digest(value.getBytes());
    }

    private static String byteHashToHexString(byte[] hash) {
        return DatatypeConverter.printHexBinary(hash).toLowerCase();
    }
}
