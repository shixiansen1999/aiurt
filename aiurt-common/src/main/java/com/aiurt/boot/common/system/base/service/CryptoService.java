package com.aiurt.boot.common.system.base.service;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class CryptoService {

    public static final int DEFAULT_SALT_BYTES = 10;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final byte[] PASS_SALT_SEP = "_".getBytes(Charsets.UTF_8);

    public Pair<String, String> createSaltHash(String password) {
        byte[] saltBytes = new byte[DEFAULT_SALT_BYTES];
        SECURE_RANDOM.nextBytes(saltBytes);
        String passSaltHash = hash(password, saltBytes);
        return Pair.of(passSaltHash, HashCode.fromBytes(saltBytes).toString());
    }


    public boolean validatePassword(String toCheckPassword, String hashedPassword, String hexSalt) {
        byte[] saltBytes = null;
        if (hexSalt != null && !hexSalt.isEmpty()) {
            saltBytes = HashCode.fromString(hexSalt).asBytes();
        }
        String passSaltHash = hash(toCheckPassword, saltBytes);
        return hashedPassword.equals(passSaltHash);
    }

    private String hash(String password, byte[] saltBytes) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(password));
        HashFunction hashFunction = Hashing.md5();
        Hasher hasher = hashFunction.newHasher();
        hasher.putBytes(password.getBytes(Charsets.UTF_8));
        if (saltBytes != null && saltBytes.length > 0) {
            hasher.putBytes(PASS_SALT_SEP);
            hasher.putBytes(saltBytes);
        }
        return hasher.hash().toString();
    }

}
