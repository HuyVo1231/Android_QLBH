package com.example.myapplication.vnpay;

import android.os.Build;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.io.UnsupportedEncodingException;

public class VNPayURLBuilder {
    public static String generateVNPayURL(long amount) throws Exception {
        // Format current date and expiration date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String createDate = dateFormat.format(new Date());

        // Set expiration time (1 hour later)
        Date expireDate = new Date(System.currentTimeMillis() + 3600000);
        String expireDateStr = dateFormat.format(expireDate);

        // Generate Transaction Reference
        String txnRef = generateTransactionRef();

        // Set parameters
        Map<String, String> params = new HashMap<>();
        params.put("vnp_Amount", String.valueOf(amount));
        params.put("vnp_TmnCode", VNPAYConfig.TMN_CODE);
        params.put("vnp_ReturnUrl", VNPAYConfig.RETURN_URL);
        params.put("vnp_TxnRef", txnRef);
        params.put("vnp_CreateDate", createDate);
        params.put("vnp_ExpireDate", expireDateStr);
        params.put("vnp_IpAddr", "127.0.0.1");
        params.put("vnp_OrderInfo", "Thanh toan don hang");
        params.put("vnp_OrderType", "other");
        params.put("vnp_Locale", "vn");
        params.put("vnp_Version", "1.0.25");
        params.put("vnp_Command", "pay");
        params.put("vnp_CurrCode", "VND");

        // Generate Secure Hash with SHA-512
        String hashData = generateHashData(params);
        String secureHash = sha512(hashData + VNPAYConfig.SECRET_KEY);

        params.put("vnp_SecureHash", secureHash);

        // Build URL
        StringBuilder query = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (Build.VERSION.SDK_INT >= 33) {
                query.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8))
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                        .append("&");
            }
        }

        return VNPAYConfig.VNP_URL + "?" + query.toString();
    }

    // SHA-512 hashing method
    public static String sha512(String message) {
        String digest = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] hash = md.digest(message.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                sb.append(String.format("%02x", b & 0xff));
            }
            digest = sb.toString();
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException ex) {
            digest = "";
        }
        return digest;
    }

    // Helper method to generate hash data for secure hash
    private static String generateHashData(Map<String, String> params) {
        StringBuilder hashData = new StringBuilder();
        params.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    hashData.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
                });
        // Remove the trailing "&"
        if (hashData.length() > 0) {
            hashData.deleteCharAt(hashData.length() - 1);
        }
        return hashData.toString();
    }

    // Generate a unique transaction reference
    private static String generateTransactionRef() {
        return java.util.UUID.randomUUID().toString();
    }
}
