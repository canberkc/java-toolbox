package com.cb.experiments.security;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLContext;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;


public class SecurityUtils {

  public static void performClientAuth(String keystore, String keypass) throws Exception {
    SSLContext sslContext = SSLContexts.custom()
        .loadKeyMaterial(readStore(keystore, keypass), keypass.toCharArray()).build();

    HttpClient httpClient = HttpClients.custom().setSSLContext(sslContext).build();
    HttpResponse response = httpClient.execute(new HttpGet("https://client.badssl.com/"));

    HttpEntity entity = response.getEntity();
    System.out.println(response.getStatusLine());
    EntityUtils.consume(entity);

  }

  private static KeyStore readStore(String keystore, String keypass) throws Exception {
    try (InputStream keyStoreStream = new FileInputStream(keystore)) {
      KeyStore keyStore = KeyStore.getInstance("PKCS12");
      keyStore.load(keyStoreStream, keypass.toCharArray());
      return keyStore;
    }
  }

  public static KeyPair generateKeys(int keysize) throws NoSuchAlgorithmException {
    KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
    keygen.initialize(keysize);

    return keygen.generateKeyPair();
  }


  public static PublicKey getPublicKey(KeyPair keypair) {
    return keypair.getPublic();
  }

  public static PrivateKey getPrivateKey(KeyPair keypair) {
    return keypair.getPrivate();
  }

  public static PrivateKey readPrivateKeyFromFile(String filePath)
      throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
    Path path = Paths.get(filePath);
    byte[] keyBytes = Files.readAllBytes(path);
    PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
    return KeyFactory.getInstance("RSA").generatePrivate(spec);
  }

  public static PublicKey readPublicKeyFromFile(String filePath)
      throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
    Path path = Paths.get(filePath);
    byte[] keyBytes = Files.readAllBytes(path);
    X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
    return KeyFactory.getInstance("RSA").generatePublic(spec);
  }

  public static void writePublicKeyToFile(PublicKey publicKey, File fileToWrite) throws Exception {
    writeToFile(fileToWrite, publicKey.getEncoded());
  }

  public static void writePrivateKeyToFile(PrivateKey privateKey, File fileToWrite)
      throws Exception {
    writeToFile(fileToWrite, privateKey.getEncoded());
  }

  public static void writeToFile(File fileToWrite, byte[] content) throws Exception {
    try (FileOutputStream outStream = new FileOutputStream(fileToWrite)) {
      outStream.write(content);
      outStream.flush();
    } catch (Exception e) {
      throw e;
    }
  }

  public static void encryptFileRSA(byte[] content, File fileToWrite, PublicKey key)
      throws Exception {
    Cipher cipher = Cipher.getInstance("RSA");
    cipher.init(Cipher.ENCRYPT_MODE, key);
    writeToFile(fileToWrite, cipher.doFinal(content));
  }

  public static void decryptFileRSA(byte[] content, File fileToWrite, PrivateKey key)
      throws Exception {
    Cipher cipher = Cipher.getInstance("RSA");
    cipher.init(Cipher.DECRYPT_MODE, key);
    writeToFile(fileToWrite, cipher.doFinal(content));
  }

  public static SecretKey generateAESKey() throws NoSuchAlgorithmException {
    return KeyGenerator.getInstance("AES").generateKey();
  }

  public static void writeAESKeyToFileEncoded(SecretKey key, String filePath) throws Exception {
    String encodedKey = Base64.getEncoder().encodeToString(key.getEncoded());
    writeToFile(new File(filePath), encodedKey.getBytes());
  }

  public static SecretKey readAESKeyFromFile(String filePath) throws Exception {
    Path path = Paths.get(filePath);
    byte[] keyBytes = Files.readAllBytes(path);
    byte[] decodedKey = Base64.getDecoder().decode(keyBytes);
    SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    return originalKey;
  }

  public static void encryptFileAES(byte[] content, String fileToWrite, SecretKey key)
      throws Exception {
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    cipher.init(Cipher.ENCRYPT_MODE, key);
    byte[] iv = cipher.getIV();

    try (FileOutputStream fileOut = new FileOutputStream(fileToWrite);
        CipherOutputStream cipherOut = new CipherOutputStream(fileOut, cipher)) {
      fileOut.write(iv);
      cipherOut.write(content);
    }
  }

  public static void decryptFileAES(String contentFile, String fileToWrite, SecretKey key)
      throws Exception {
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

    try (FileInputStream fileIn = new FileInputStream(contentFile);
        FileOutputStream fileOut = new FileOutputStream(fileToWrite)) {
      byte[] fileIv = new byte[16];
      fileIn.read(fileIv);
      cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(fileIv));

      try (CipherInputStream cipherIn = new CipherInputStream(fileIn, cipher);
          InputStreamReader inputReader = new InputStreamReader(cipherIn);
          BufferedReader reader = new BufferedReader(inputReader)
      ) {
        String line;
        while ((line = reader.readLine()) != null) {
          fileOut.write(line.getBytes());
        }
      }
    }

  }

}
