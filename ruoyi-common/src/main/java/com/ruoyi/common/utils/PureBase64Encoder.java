package com.ruoyi.common.utils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class PureBase64Encoder {

    /**
     * 加密（实际上是编码）: 使用 Java 自带的 Base64 库
     * @param originalString 7-9位长的原字符串
     * @return Base64 编码后的字符串 (长度约 10-12 位)
     */
    public static String encode(String originalString) {
        if (originalString == null) {
            return null;
        }

        // 将字符串转换为字节数组
        byte[] originalBytes = originalString.getBytes(StandardCharsets.UTF_8);

        // 使用 Base64 URL 安全编码，不带填充符，生成最终字符串
        return Base64.getUrlEncoder().withoutPadding().encodeToString(originalBytes);
    }

    /**
     * 解密（实际上是解码）
     * @param encodedData Base64 编码的字符串
     * @return 解码后的原字符串
     */
    public static String decode(String encodedData) {
        if (encodedData == null) {
            return null;
        }

        // Base64 解码
        byte[] decodedBytes = Base64.getUrlDecoder().decode(encodedData);

        // 转换回字符串
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }



    public static void main(String[] args) {
        String original = "ABC12345"; // 8位原串

        // 加密/编码
        String encoded = PureBase64Encoder.encode(original);
        System.out.println("原串: " + original);
        System.out.println("编码串: " + encoded + " (长度: " + encoded.length() + ")");

        // 解密/解码
        String decoded = PureBase64Encoder.decode(encoded);
        System.out.println("解码串: " + decoded);
        System.out.println("是否相同: " + original.equals(decoded));
    }

}
