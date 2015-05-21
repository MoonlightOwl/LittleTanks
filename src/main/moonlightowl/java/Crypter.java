package main.moonlightowl.java;

public class Crypter {
	public static byte[] encrypt(String text, String keyWord) {
        byte[] arr = text.getBytes();
        byte[] keyarr = keyWord.getBytes();
        byte[] result = new byte[arr.length];
        for(int i = 0; i< arr.length; i++) {
            result[i] = (byte) (arr[i] ^ keyarr[i % keyarr.length]);
        }
        return result;
    }
    public static byte[] decrypt(byte[] text, String keyWord) {
        byte[] result  = new byte[text.length];
        byte[] keyarr = keyWord.getBytes();
        for(int i = 0; i < text.length;i++) {
            result[i] = (byte) (text[i] ^ keyarr[i% keyarr.length]);
        }
        return result;
    }
}