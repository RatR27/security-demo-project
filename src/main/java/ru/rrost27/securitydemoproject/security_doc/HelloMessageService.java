package ru.rrost27.securitydemoproject.security_doc;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class HelloMessageService implements MessageService{

    @PreAuthorize("isAuthenticated()")
    public String getMessage() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return "Hello " + authentication;
    }

}
