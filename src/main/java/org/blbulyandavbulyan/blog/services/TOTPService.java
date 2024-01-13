package org.blbulyandavbulyan.blog.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TOTPService {
    //FIXME: 13.01.2024 implement this service
    public boolean verifyCode(String secret, String code) {
        return false;
    }
}
