package org.lets_play_be.config;


import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


class JasyptConfigTest {

    @Test
    public void testEncrypt() {

        String testString = "testString";

        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();

        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword("password");
        config.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");
        config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setStringOutputType("base64");

        encryptor.setConfig(config);

        String encryptedString = encryptor.encrypt(testString);
        String decryptedString = encryptor.decrypt(encryptedString);

        Assertions.assertNotEquals(testString, encryptedString);
        assertEquals(testString, decryptedString);
    }

}