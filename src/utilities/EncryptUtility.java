package utilities;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;

public class EncryptUtility {

	//Genera un par de claves
    public static KeyPair buildKeyPair() {
        final int keySize = 2048;
        KeyPairGenerator keyPairGenerator = null;
        
		try {
			keyPairGenerator = KeyPairGenerator.getInstance("RSA");
	        keyPairGenerator.initialize(keySize);      
		} catch (NoSuchAlgorithmException e) {
			new LoggerUtility().getLogger().severe("Error al crear las claves: "+e);
		}
        return keyPairGenerator.genKeyPair();
    }

	//Encripta un mensaje
    @SuppressWarnings("null")
	public static byte[] encrypt(String tipo, PrivateKey privateKey, String message) {
    	Cipher cipher = null;
    	try {
        	if(tipo != null || !tipo.equals("") || tipo.length() != 0)
                cipher = Cipher.getInstance(tipo);  
        	else
                cipher = Cipher.getInstance("RSA");  
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);  

            return cipher.doFinal(message.getBytes());  
    	} catch (Exception e) {
			new LoggerUtility().getLogger().severe("Error al al encriptar: "+e);
			return null;
    	}
    }
    
	//Desencripta un mensaje
    @SuppressWarnings("null")
	public static byte[] decrypt(String tipo, PublicKey publicKey, byte [] encrypted) {
    	Cipher cipher;
    	try {
        	if(tipo != null || !tipo.equals("") || tipo.length() != 0)
                cipher = Cipher.getInstance(tipo);  
        	else
                cipher = Cipher.getInstance("RSA");   
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            
            return cipher.doFinal(encrypted);
    	} catch (Exception e) {
			new LoggerUtility().getLogger().severe("Error al al encriptar: "+e);
			return null;
    	}
    }
}
