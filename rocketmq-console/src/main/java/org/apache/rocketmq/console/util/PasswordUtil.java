/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.apache.rocketmq.console.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

public class PasswordUtil {
    private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256";

    public static String encodePassword(String password, String salt) throws InvalidKeySpecException, NoSuchAlgorithmException {
        return PBKDF2(password, salt.getBytes(), 10000, 50);
    }

    public static String PBKDF2(String password, byte[] salt, int iter, int kenLen) throws NoSuchAlgorithmException, InvalidKeySpecException {
        final KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, iter, kenLen * 8);
        final SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
        return bytesToHex(keyFactory.generateSecret(keySpec).getEncoded());
    }

    public static byte[] fromHex(String str) {
        byte[] bytes = new byte[str.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(str.substring(i * 2, 2 * i + 2), 16);
        }
        return bytes;
    }

    public static String bytesToHex(byte[] bytes) {
        final StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            final String hex = Integer.toHexString(aByte & 0xFF);
            if (hex.length() < 2) {
                sb.append(0);
            }
            sb.append(hex);
        }

        return sb.toString();
    }

//    public static void main(String[] args) throws InvalidKeySpecException, NoSuchAlgorithmException {
//        final String encodePwd = "c27e4d3fc3003aeb3a255e6feeaed98b365beabfe119ac453a38a5bec150af33e05f51bd10657efebcabb2a5c91ce624b6a2";
//        System.out.println(PasswordUtil.encodePassword("airparking", "tTGXy1Od7R").equals(encodePwd));
//    }
}
