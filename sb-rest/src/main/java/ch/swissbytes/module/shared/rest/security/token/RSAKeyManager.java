package ch.swissbytes.module.shared.rest.security.token;

import org.jboss.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.*;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

@ApplicationScoped
public class RSAKeyManager implements Serializable {

    /**
     * String to hold name of the encryption algorithm.
     */
    private final String ALGORITHM = "RSA";
    @Inject
    protected Logger log;
    /**
     * String to hold name of private key file.
     */
    private String PRIVATE_KEY_FILE = "~/Documents/private.key"; //"C:/keys/private.key";

    /**
     * String to hold name of public key file.
     */
    private String PUBLIC_KEY_FILE = "~/Documents/public.key";// "c:/keys/public.key";


    private PublicKey publicKey;

    private PrivateKey privateKey;

    /**
     * Generate key which contains a pair of private and public key using 1024
     * bytes. Store the set of keys in Private.key and Public.key files.
     *
     * @throws java.security.NoSuchAlgorithmException
     * @throws java.io.IOException
     * @throws java.io.FileNotFoundException
     */
    public void generateKey() {
        try {
//            PRIVATE_KEY_FILE = KeyAppConfiguration.getString(ConfigurationKey.RSA_KEY_PRIVATE_FILE);
//            PUBLIC_KEY_FILE = KeyAppConfiguration.getString(ConfigurationKey.RSA_KEY_PUBLIC_FILE);
            final KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
            keyGen.initialize(1024);
            final KeyPair key = keyGen.generateKeyPair();

            File privateKeyFile = new File(PRIVATE_KEY_FILE);
            File publicKeyFile = new File(PUBLIC_KEY_FILE);

            // Create files to store public and private key
            if (privateKeyFile.getParentFile() != null) {
                privateKeyFile.getParentFile().mkdirs();
            }

            privateKeyFile.createNewFile();

            if (publicKeyFile.getParentFile() != null) {
                publicKeyFile.getParentFile().mkdirs();
            }

            publicKeyFile.createNewFile();

            // Saving the Public key in a file
            ObjectOutputStream publicKeyOS = new ObjectOutputStream(new FileOutputStream(publicKeyFile));
            publicKeyOS.writeObject(key.getPublic());
            publicKeyOS.close();

            // Saving the Private key in a file
            ObjectOutputStream privateKeyOS = new ObjectOutputStream(new FileOutputStream(privateKeyFile));
            privateKeyOS.writeObject(key.getPrivate());
            privateKeyOS.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * The method checks if the pair of public and private key has been generated.
     *
     * @return flag indicating if the pair of keys were generated.
     */
    private boolean areKeysPresent() {
        File privateKey = new File(PRIVATE_KEY_FILE);
        File publicKey = new File(PUBLIC_KEY_FILE);

        return privateKey.exists() && publicKey.exists();
    }

    private void loadKeys() throws Exception {
        if (!areKeysPresent()) {
            generateKey();
        }

        ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(PUBLIC_KEY_FILE));
        publicKey = (PublicKey) inputStream.readObject();

        inputStream = new ObjectInputStream(new FileInputStream(PRIVATE_KEY_FILE));
        privateKey = (PrivateKey) inputStream.readObject();
    }

    @PostConstruct
    public void initialize() throws Exception {
//        PRIVATE_KEY_FILE = KeyAppConfiguration.getString(ConfigurationKey.RSA_KEY_PRIVATE_FILE);
//        PUBLIC_KEY_FILE = KeyAppConfiguration.getString(ConfigurationKey.RSA_KEY_PUBLIC_FILE);
        log.info("Initiate RSAKeyManager");
        loadKeys();
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

}
