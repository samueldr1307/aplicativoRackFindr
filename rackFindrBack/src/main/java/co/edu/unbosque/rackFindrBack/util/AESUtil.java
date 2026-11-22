package co.edu.unbosque.rackFindrBack.util;

import static org.apache.commons.codec.binary.Base64.decodeBase64;
import static org.apache.commons.codec.binary.Base64.encodeBase64;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * Clase utilitaria para el cifrado y descifrado simétrico de datos sensibles
 * utilizando el algoritmo {@code AES/GCM/NoPadding} con codificación Base64,
 * además de funciones de hashing criptográfico (MD5, SHA-1, SHA-256, SHA-384, SHA-512).
 *
 * @author Equipo RackFindr - Universidad El Bosque
 * @version 1.0
 */
public class AESUtil {

	/** Algoritmo base de cifrado simétrico. */
	private final static String ALGORITMO = "AES";

	/** Modo de operación y relleno del cifrado AES. */
	private final static String TIPOCIFRADO = "AES/GCM/NoPadding";

	/**
	 * Cifra un texto plano utilizando AES-GCM con una llave y vector de inicialización (IV) específicos.
	 *
	 * @param llave llave secreta de 16 bytes
	 * @param iv    vector de inicialización
	 * @param texto texto en claro que se desea cifrar
	 * @return texto cifrado codificado en Base64, o {@code null} si la entrada es {@code null}
	 */
	public static String encrypt(String llave, String iv, String texto) {
		if (texto == null) {
			return null;
		}
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance(TIPOCIFRADO);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			e.printStackTrace();
		}

		SecretKeySpec secretKeySpec = new SecretKeySpec(llave.getBytes(), ALGORITMO);
		GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128, iv.getBytes());
		try {
			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, gcmParameterSpec);
		} catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}

		byte[] encrypted = null;
		try {
			encrypted = cipher.doFinal(texto.getBytes());
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}

		return new String(encodeBase64(encrypted));
	}

	public static String decrypt(String llave, String iv, String encrypted) {
		if (encrypted == null || encrypted.isEmpty()) {
			return "";
		}
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance(TIPOCIFRADO);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			e.printStackTrace();
		}

		SecretKeySpec secretKeySpec = new SecretKeySpec(llave.getBytes(), ALGORITMO);
		GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128, iv.getBytes());
		try {
			cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, gcmParameterSpec);
		} catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}

		byte[] enc = decodeBase64(encrypted);
		byte[] decrypted = null;
		try {
			decrypted = cipher.doFinal(enc);
			return new String(decrypted);
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			return encrypted;
		}
	}

	public static String decrypt(String encrypted) {
		String iv = "programacioncomp";
		String key = "llavede16carater";
		return decrypt(key, iv, encrypted);
	}

	public static String encrypt(String plainText) {
		String iv = "programacioncomp";
		String key = "llavede16carater";
		return encrypt(key, iv, plainText);
	}

	public static String hashingToMD5(String content) {
		return DigestUtils.md5Hex(content);
	}

	public static String hashingToSHA1(String content) {
		return DigestUtils.sha1Hex(content);
	}

	public static String hashingToSHA256(String content) {
		return DigestUtils.sha256Hex(content);
	}

	public static String hashingToSHA384(String content) {
		return DigestUtils.sha384Hex(content);
	}

	public static String hashingToSHA512(String content) {
		return DigestUtils.sha512Hex(content);
	}

}
