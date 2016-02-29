package com.cmread.cmu.css.utils.security;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

@SuppressWarnings("unused")
public class EncryptTool {

	private static final String DEFAULT_STRING_ENCRYPT = "AES";
	private static final String ENCRYPT_TYPE = "encryption";
	private static final String DECRYPT_TYPE = "decryption";
	private static final String UTFENCODING = "UTF-8";
	private static final String DEFAULT_KEY = "BMEIMPL@YYYYMMDD";
	private static final String OPT_TYPE = "-t";
	private static final String OPT_PASSWORD = "-p";
	private static final String OPT_HELP = "-help";
	private static final int BASELENGTH = 255;
	private static final int LOOKUPLENGTH = 64;
	private static final int TWENTYFOURBITGROUP = 24;
	private static final int EIGHTBIT = 8;
	private static final int SIXTEENBIT = 16;
	private static final int SIXBIT = 6;
	private static final int FOURBYTE = 4;
	private static final int SIGN = -128;
	private static final char PAD = '=';
	private static final boolean fDebug = false;
	private static final byte[] base64Alphabet = new byte['ÿ'];
	private static final char[] lookUpBase64Alphabet = new char[64];

	public String encrypt(String input) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
		return encryptString(input, "BMEIMPL@YYYYMMDD", "AES", "encryption");
	}

	public String encrypt(String input, String algorithm) throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
		return encryptString(input, "BMEIMPL@YYYYMMDD", algorithm, "encryption");
	}

	public String parseEncrypt(String input) throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
		return encryptString(input, "BMEIMPL@YYYYMMDD", "AES", "decryption");
	}

	public String parseEncrypt(String input, String algorithm) throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
		return encryptString(input, "BMEIMPL@YYYYMMDD", algorithm, "decryption");
	}

	private String encryptString(String input, String key, String algorithm, String type)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException, UnsupportedEncodingException {
		int cryptionMode = 0;

		if (type.equals("encryption")) {
			cryptionMode = 1;
		} else {
			cryptionMode = 2;
		}

		if ((null != input) && (0 != input.length()) && (null != key) && (0 != key.length())) {
			if ((null == algorithm) || (0 == algorithm.length())) {
				algorithm = "AES";
			}

			byte[] raw = key.getBytes("UTF-8");
			SecretKeySpec skeySpec = new SecretKeySpec(raw, algorithm);

			Cipher cipher = Cipher.getInstance(algorithm);
			cipher.init(cryptionMode, skeySpec);

			if (cryptionMode == 1) {
				byte[] outByte = cipher.doFinal(input.getBytes("UTF-8"));
				String strResult = encode(outByte);

				return strResult.trim();
			}

			byte[] outByte = cipher.doFinal(decode(input));
			String strResult = new String(outByte, "UTF-8");
			return strResult.trim();
		}

		return null;
	}

	private boolean isWhiteSpace(char octect) {
		return (octect == ' ') || (octect == '\r') || (octect == '\n') || (octect == '\t');
	}

	private boolean isPad(char octect) {
		return octect == '=';
	}

	private boolean isData(char octect) {
		return base64Alphabet[octect] != -1;
	}

	private boolean isBase64(char octect) {
		return (isWhiteSpace(octect)) || (isPad(octect)) || (isData(octect));
	}

	private String encode(byte[] binaryData) {
		if (binaryData == null) {
			return null;
		}

		int lengthDataBits = binaryData.length * 8;
		if (lengthDataBits == 0) {
			return "";
		}

		int fewerThan24bits = lengthDataBits % 24;
		int numberTriplets = lengthDataBits / 24;
		int numberQuartet = fewerThan24bits != 0 ? numberTriplets + 1 : numberTriplets;
		int numberLines = (numberQuartet - 1) / 19 + 1;
		char[] encodedData = null;

		encodedData = new char[numberQuartet * 4 + numberLines];

		byte k = 0;
		byte l = 0;
		byte b1 = 0;
		byte b2 = 0;
		byte b3 = 0;

		int encodedIndex = 0;
		int dataIndex = 0;
		int i = 0;

		for (int line = 0; line < numberLines - 1; line++) {
			for (int quartet = 0; quartet < 19; quartet++) {
				b1 = binaryData[(dataIndex++)];
				b2 = binaryData[(dataIndex++)];
				b3 = binaryData[(dataIndex++)];

				l = (byte) (b2 & 0xF);
				k = (byte) (b1 & 0x3);

				byte val1 = (b1 & 0xFFFFFF80) == 0 ? (byte) (b1 >> 2) : (byte) (b1 >> 2 ^ 0xC0);

				byte val2 = (b2 & 0xFFFFFF80) == 0 ? (byte) (b2 >> 4) : (byte) (b2 >> 4 ^ 0xF0);
				byte val3 = (b3 & 0xFFFFFF80) == 0 ? (byte) (b3 >> 6) : (byte) (b3 >> 6 ^ 0xFC);

				encodedData[(encodedIndex++)] = lookUpBase64Alphabet[val1];
				encodedData[(encodedIndex++)] = lookUpBase64Alphabet[(val2 | k << 4)];
				encodedData[(encodedIndex++)] = lookUpBase64Alphabet[(l << 2 | val3)];
				encodedData[(encodedIndex++)] = lookUpBase64Alphabet[(b3 & 0x3F)];

				i++;
			}
			encodedData[(encodedIndex++)] = '\n';
		}

		for (; i < numberTriplets; i++) {
			b1 = binaryData[(dataIndex++)];
			b2 = binaryData[(dataIndex++)];
			b3 = binaryData[(dataIndex++)];

			l = (byte) (b2 & 0xF);
			k = (byte) (b1 & 0x3);

			byte val1 = (b1 & 0xFFFFFF80) == 0 ? (byte) (b1 >> 2) : (byte) (b1 >> 2 ^ 0xC0);

			byte val2 = (b2 & 0xFFFFFF80) == 0 ? (byte) (b2 >> 4) : (byte) (b2 >> 4 ^ 0xF0);
			byte val3 = (b3 & 0xFFFFFF80) == 0 ? (byte) (b3 >> 6) : (byte) (b3 >> 6 ^ 0xFC);

			encodedData[(encodedIndex++)] = lookUpBase64Alphabet[val1];
			encodedData[(encodedIndex++)] = lookUpBase64Alphabet[(val2 | k << 4)];
			encodedData[(encodedIndex++)] = lookUpBase64Alphabet[(l << 2 | val3)];
			encodedData[(encodedIndex++)] = lookUpBase64Alphabet[(b3 & 0x3F)];
		}

		if (fewerThan24bits == 8) {
			b1 = binaryData[dataIndex];
			k = (byte) (b1 & 0x3);

			byte val1 = (b1 & 0xFFFFFF80) == 0 ? (byte) (b1 >> 2) : (byte) (b1 >> 2 ^ 0xC0);
			encodedData[(encodedIndex++)] = lookUpBase64Alphabet[val1];
			encodedData[(encodedIndex++)] = lookUpBase64Alphabet[(k << 4)];
			encodedData[(encodedIndex++)] = '=';
			encodedData[(encodedIndex++)] = '=';
		} else if (fewerThan24bits == 16) {
			b1 = binaryData[dataIndex];
			b2 = binaryData[(dataIndex + 1)];
			l = (byte) (b2 & 0xF);
			k = (byte) (b1 & 0x3);

			byte val1 = (b1 & 0xFFFFFF80) == 0 ? (byte) (b1 >> 2) : (byte) (b1 >> 2 ^ 0xC0);
			byte val2 = (b2 & 0xFFFFFF80) == 0 ? (byte) (b2 >> 4) : (byte) (b2 >> 4 ^ 0xF0);

			encodedData[(encodedIndex++)] = lookUpBase64Alphabet[val1];
			encodedData[(encodedIndex++)] = lookUpBase64Alphabet[(val2 | k << 4)];
			encodedData[(encodedIndex++)] = lookUpBase64Alphabet[(l << 2)];
			encodedData[(encodedIndex++)] = '=';
		}

		encodedData[encodedIndex] = '\n';

		return new String(encodedData);
	}

	private byte[] decode(String encoded) {
		if (encoded == null) {
			return null;
		}
		char[] base64Data = encoded.toCharArray();

		int len = removeWhiteSpace(base64Data);

		if (len % 4 != 0) {
			return null;
		}

		int numberQuadruple = len / 4;

		if (numberQuadruple == 0) {
			return new byte[0];
		}

		byte[] decodedData = null;
		byte b1 = 0;
		byte b2 = 0;
		byte b3 = 0;
		byte b4 = 0;
		byte marker0 = 0;
		byte marker1 = 0;
		char d1 = '\000';
		char d2 = '\000';
		char d3 = '\000';
		char d4 = '\000';

		int i = 0;
		int encodedIndex = 0;
		int dataIndex = 0;
		decodedData = new byte[numberQuadruple * 3];

		for (; i < numberQuadruple - 1; i++) {
			if ((!isData(d1 = base64Data[(dataIndex++)])) || (!isData(d2 = base64Data[(dataIndex++)]))
					|| (!isData(d3 = base64Data[(dataIndex++)])) || (!isData(d4 = base64Data[(dataIndex++)]))) {
				return null;
			}

			b1 = base64Alphabet[d1];
			b2 = base64Alphabet[d2];
			b3 = base64Alphabet[d3];
			b4 = base64Alphabet[d4];

			decodedData[(encodedIndex++)] = (byte) (b1 << 2 | b2 >> 4);
			decodedData[(encodedIndex++)] = (byte) ((b2 & 0xF) << 4 | b3 >> 2 & 0xF);
			decodedData[(encodedIndex++)] = (byte) (b3 << 6 | b4);
		}

		if ((!isData(d1 = base64Data[(dataIndex++)])) || (!isData(d2 = base64Data[(dataIndex++)]))) {
			return null;
		}

		b1 = base64Alphabet[d1];
		b2 = base64Alphabet[d2];

		d3 = base64Data[(dataIndex++)];
		d4 = base64Data[(dataIndex++)];
		if ((!isData(d3)) || (!isData(d4))) {
			if ((isPad(d3)) && (isPad(d4))) {
				if ((b2 & 0xF) != 0) {
					return null;
				}
				byte[] tmp = new byte[i * 3 + 1];
				System.arraycopy(decodedData, 0, tmp, 0, i * 3);
				tmp[encodedIndex] = (byte) (b1 << 2 | b2 >> 4);
				return tmp;
			}
			if ((!isPad(d3)) && (isPad(d4))) {
				b3 = base64Alphabet[d3];
				if ((b3 & 0x3) != 0) {
					return null;
				}
				byte[] tmp = new byte[i * 3 + 2];
				System.arraycopy(decodedData, 0, tmp, 0, i * 3);
				tmp[(encodedIndex++)] = (byte) (b1 << 2 | b2 >> 4);
				tmp[encodedIndex] = (byte) ((b2 & 0xF) << 4 | b3 >> 2 & 0xF);
				return tmp;
			}

			return null;
		}

		b3 = base64Alphabet[d3];
		b4 = base64Alphabet[d4];
		decodedData[(encodedIndex++)] = (byte) (b1 << 2 | b2 >> 4);
		decodedData[(encodedIndex++)] = (byte) ((b2 & 0xF) << 4 | b3 >> 2 & 0xF);
		decodedData[(encodedIndex++)] = (byte) (b3 << 6 | b4);

		return decodedData;
	}

	private int removeWhiteSpace(char[] data) {
		if (data == null) {
			return 0;
		}

		int newSize = 0;
		int len = data.length;
		for (int i = 0; i < len; i++) {
			if (isWhiteSpace(data[i]))
				continue;
			data[(newSize++)] = data[i];
		}

		return newSize;
	}

	@SuppressWarnings("rawtypes")
	public static void main(String[] args) {
		if ((null == args) || (args.length <= 0) || (args[0].equals("-help"))) {
			printHelp();
			return;
		}

		List options = Arrays.asList(args);

		String encryptAlgorithm = "AES";

		EncryptTool tool = new EncryptTool();
		try {
			System.out.println(tool.encrypt(args[0], encryptAlgorithm));
		} catch (Exception e) {
			System.out.println("Error occured durring encrypting: " + e);
		}
	}

	@SuppressWarnings("rawtypes")
	private static String getOption(String option, List options) {
		if (options.contains(option)) {
			int i = options.indexOf(option);
			if (i < options.size() - 1) {
				return options.get(i + 1).toString();
			}
		}

		return null;
	}

	private static void printHelp() {
		System.out.println("Usage: genpwd string");
	}

	private static void printInvalidFlag(String flag) {
		System.out.println("genpwd: invalid flag: " + flag);

		printHelp();
	}

	private static void printOptions() {
		System.out.println("where options include:");
		System.out.println("    <password>    a string to be encrypted");
	}

	static {
		for (int i = 0; i < 255; i++) {
			base64Alphabet[i] = -1;
		}
		for (int i = 90; i >= 65; i--) {
			base64Alphabet[i] = (byte) (i - 65);
		}
		for (int i = 122; i >= 97; i--) {
			base64Alphabet[i] = (byte) (i - 97 + 26);
		}

		for (int i = 57; i >= 48; i--) {
			base64Alphabet[i] = (byte) (i - 48 + 52);
		}

		base64Alphabet[43] = 62;
		base64Alphabet[47] = 63;

		for (int i = 0; i <= 25; i++) {
			lookUpBase64Alphabet[i] = (char) (65 + i);
		}
		int i = 26;
		for (int j = 0; i <= 51; j++) {
			lookUpBase64Alphabet[i] = (char) (97 + j);

			i++;
		}

		i = 52;
		for (int j = 0; i <= 61; j++) {
			lookUpBase64Alphabet[i] = (char) (48 + j);

			i++;
		}
		lookUpBase64Alphabet[62] = '+';
		lookUpBase64Alphabet[63] = '/';
	}
}