package org.blbulyandavbulyan.blog.services;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;

@Service
public class KeyProviderService {
    public Key getFromBytesOrElseRandom(byte[] bytes) {
        if (bytes != null && bytes.length > 0) {
            return Keys.hmacShaKeyFor(bytes);

        } else return Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }

}
