package com.ut.flutter_gm_cipher;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.asn1.gm.GMNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.*;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 *  SM2 加解密工具类
 * @author pengsheng
 * @version 1.0
 * @date 2020/7/22 15:09
 */
public class Sm2Engine {

    private X9ECParameters sm2ECParameters;

    private ECDomainParameters ecDomainParameters;

    private AsymmetricCipherKeyPair asymmetricCipherKeyPair;

    private SM2Engine sm2Engine;

    public static final String INVALID_CIPHER_TEXT = "文本内容或者密钥不正确";

    public static final String NO_SHA1PRNG_ALGORITHM = "没有SHA1PRNG算法";

    public Sm2Engine() throws NoSuchAlgorithmException {
        X9ECParameters sm2ECParameters = GMNamedCurves.getByName("sm2p256v1");
        ECDomainParameters ecDomainParameters = new ECDomainParameters(sm2ECParameters.getCurve(), sm2ECParameters.getG(), sm2ECParameters.getN());
        ECKeyPairGenerator keyPairGenerator = new ECKeyPairGenerator();
        SecureRandom random;
        try {
            random = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            throw new NoSuchAlgorithmException(NO_SHA1PRNG_ALGORITHM);
        }
        keyPairGenerator.init(new ECKeyGenerationParameters(ecDomainParameters, random));
        AsymmetricCipherKeyPair asymmetricCipherKeyPair = keyPairGenerator.generateKeyPair();
        this.sm2ECParameters = sm2ECParameters;
        this.ecDomainParameters = ecDomainParameters;
        this.asymmetricCipherKeyPair = asymmetricCipherKeyPair;
        this.sm2Engine = new SM2Engine();
    }

    /**
     *  获取私钥
     * @return
     */
    public String getPrivateKey() {
        BigInteger privateKey = ((ECPrivateKeyParameters) asymmetricCipherKeyPair.getPrivate()).getD();
        return privateKey.toString(16);
    }

    /**
     *  获取公钥
     * @return
     */
    public String getPublicKey() {
        ECPoint ecPoint = ((ECPublicKeyParameters) asymmetricCipherKeyPair.getPublic()).getQ();
        return Hex.toHexString(ecPoint.getEncoded(false));
    }

    /**
     *  公钥加密
     * @param text : 文本内容
     * @return
     * @throws InvalidCipherTextException
     */
    public String encodeData(String text) throws InvalidCipherTextException {
        String publicKeyHex = getPublicKey();
        return encodeDataWithPublicKey(text, publicKeyHex);
    }

    /**
     *  公钥加密
     * @param text ： 文本内容
     * @param publicKeyHex ： 公钥
     * @return
     * @throws InvalidCipherTextException
     */
    public String encodeDataWithPublicKey(String text, String publicKeyHex) throws InvalidCipherTextException {
        if (StringUtils.isBlank(text) || StringUtils.isBlank(publicKeyHex)) {
            throw new InvalidCipherTextException(INVALID_CIPHER_TEXT);
        }
        ECPoint ecp = sm2ECParameters.getCurve().decodePoint(Hex.decode(publicKeyHex));
        ECPublicKeyParameters publicKeyParameters = new ECPublicKeyParameters(ecp, ecDomainParameters);
        sm2Engine.init(true, new ParametersWithRandom(publicKeyParameters, new SecureRandom()));
        byte[] m = sm2Engine.processBlock(text.getBytes(), 0, text.length());
        return Hex.toHexString(m);
    }

    /**
     *  私钥解密
     * @param text : 加密内容
     * @return
     * @throws InvalidCipherTextException
     */
    public String decodeData(String text) throws InvalidCipherTextException {
        String privateKeyHex = getPrivateKey();
        return decodeDataWithPrivateKey(text, privateKeyHex);
    }

    /**
     *  私钥解密
     * @param text ： 加密文本
     * @param privateKeyHex ： 私钥
     * @return
     * @throws InvalidCipherTextException
     */
    public String decodeDataWithPrivateKey(String text, String privateKeyHex) throws InvalidCipherTextException {
        if (StringUtils.isBlank(text) || StringUtils.isBlank(privateKeyHex)) {
            throw new InvalidCipherTextException(INVALID_CIPHER_TEXT);
        }
        byte[] cipherDataByte = Hex.decode(text);
        BigInteger privateKeyD = new BigInteger(privateKeyHex, 16);
        ECPrivateKeyParameters privateKeyParameters = new ECPrivateKeyParameters(privateKeyD, ecDomainParameters);
        sm2Engine.init(false, privateKeyParameters);
        byte[] arrayOfBytes = sm2Engine.processBlock(cipherDataByte, 0, cipherDataByte.length);
        return new String(arrayOfBytes);
    }

//    public static void main(String[] args) {
//        try {
//            Sm2Engine sm2Engine = new Sm2Engine();
//            String cipherData = sm2Engine.encodeDataWithPublicKey("111111", Constant.Sm2_public_key);
//            System.out.println("密文：：：" + cipherData);
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (InvalidCipherTextException e) {
//            e.printStackTrace();
//        }
//    }
}
