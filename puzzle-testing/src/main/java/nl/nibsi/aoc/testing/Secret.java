package nl.nibsi.aoc.testing;

import java.security.*;
import java.security.spec.*;
import java.util.*;

import javax.crypto.*;
import javax.crypto.spec.*;
import javax.security.auth.*;

import static java.lang.System.arraycopy;

import static java.util.Arrays.copyOf;
import static java.util.Arrays.copyOfRange;
import static java.util.Arrays.fill;

public final class Secret implements AutoCloseable {

  private static final String RANDOM_NUMBER_GENERATOR_ALGORITHM = "SHA1PRNG";

  private static final String KEY_DERIVATION_ALGORITHM = "PBKDF2WithHmacSHA1";

  private static final int KEY_LENGTH_IN_BITS = 256;

  private static final int ITERATION_COUNT = 20000;

  private final byte[] key;
  private final byte[] salt;
  
  private Secret(byte[] key, byte[] salt) {
    this.key  = key;
    this.salt = salt;
  }

  private static byte[] generateSalt() {
    try {
      byte[] salt = new byte[KEY_LENGTH_IN_BITS / 8];
      SecureRandom generator = SecureRandom.getInstance(RANDOM_NUMBER_GENERATOR_ALGORITHM);
      generator.nextBytes(salt);
      return salt;
    }

    catch (NoSuchAlgorithmException ex) {
      throw new AssertionError(ex);
    }
  }

  private static byte[] generateKey(char[] message, byte[] salt) {
    PBEKeySpec spec = new PBEKeySpec(message, salt, ITERATION_COUNT, KEY_LENGTH_IN_BITS);

    try {
      SecretKeyFactory factory = SecretKeyFactory.getInstance(KEY_DERIVATION_ALGORITHM);
      SecretKey key = factory.generateSecret(spec);

      try {
        return key.getEncoded();
      }

      finally {
        try {
          key.destroy();
        }
        catch (DestroyFailedException ex) {}
      }
    }

    catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
      throw new AssertionError(ex);
    }

    finally {
      spec.clearPassword();
    }
  }

  public static Secret create(char[] message) {
    byte[] salt = generateSalt();
    return new Secret(generateKey(message, salt), salt);
  }
  
  public boolean isCorrectAttempt(char[] message) {
    try (
      Secret other = new Secret(generateKey(message, salt), copyOf(salt, salt.length));
    ) {
      return this.equals(other);
    }
  }

  public void destroy() {
    fill(key,  (byte) 0);
    fill(salt, (byte) 0);
  }

  @Override
  public void close() {
    destroy();
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Secret))
      return false;

    Secret other = (Secret) obj;

    return Arrays.equals(this.key,  other.key)
        && Arrays.equals(this.salt, other.salt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, salt);
  }
  
  @Override
  public String toString() {
    int length = key.length + salt.length;
    byte[] data = copyOf(key, length);
    
    try {
      arraycopy(salt, 0, data, key.length, salt.length);
      return Base64.getEncoder().encodeToString(data);
    }

    finally {
      fill(data, (byte) 0);
    }
  }
  
  public static Secret parse(String secret) {
    int length = KEY_LENGTH_IN_BITS / 8;
    byte[] data = Base64.getDecoder().decode(secret);

    try {
      return new Secret(
        copyOfRange(data,      0,      length),
        copyOfRange(data, length, data.length)
      );
    }

    finally {
      fill(data, (byte) 0);
    }
  }
}