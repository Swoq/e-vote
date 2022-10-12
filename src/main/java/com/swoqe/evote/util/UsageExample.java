package com.swoqe.evote.util;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class UsageExample {

    public static void main(String[] args) throws Exception {
        // init

        Cipher encryptCipher = Cipher.getInstance("RSA");
        File ballot = new File("ballot_before.json");
        byte[] ballotBytes = Files.readAllBytes(ballot.toPath());

//         encrypt with private user key
        encryptCipher.init(Cipher.ENCRYPT_MODE, getVoterPrivateKey("voter4"));
        byte[] encryptedFileBytes = encryptCipher.doFinal(ballotBytes);

        // encrypt with public CVK key

        encryptCipher.init(Cipher.ENCRYPT_MODE, getPublicCvk());
        byte[] twiceEncryptedBytes = encryptCipher.doFinal(encryptedFileBytes);

        // create new encrypted file
        File encFile = new File("ballot.enc");
        encFile.createNewFile();
        try (FileOutputStream stream = new FileOutputStream(encFile)) {
            stream.write(twiceEncryptedBytes);
        }
    }

    public static PrivateKey getVoterPrivateKey(String voter) {
        try {
            File privateKeyFile = new File("votersPrivate/" + voter);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            EncodedKeySpec privateKey = new PKCS8EncodedKeySpec(Files.readAllBytes(privateKeyFile.toPath()));
            return keyFactory.generatePrivate(privateKey);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static PublicKey getPublicCvk() {
        try {
            File publicKeyFile = new File("cvk/cvk.public");
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            EncodedKeySpec publicKey = new X509EncodedKeySpec(Files.readAllBytes(publicKeyFile.toPath()));
            return keyFactory.generatePublic(publicKey);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
