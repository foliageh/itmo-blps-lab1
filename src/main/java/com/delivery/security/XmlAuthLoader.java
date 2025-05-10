package com.delivery.security;

import jakarta.annotation.PostConstruct;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class XmlAuthLoader {
    @Getter
    private final Map<String, XmlUserCredentials> credentialsCache = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() throws JAXBException, FileNotFoundException {
        JAXBContext context = JAXBContext.newInstance(AuthData.class);
        var unmarshaller = context.createUnmarshaller();
        var is = new FileInputStream("auth-users.xml");
        //var is = getClass().getResourceAsStream("/auth-users.xml");
        AuthData authData = (AuthData) unmarshaller.unmarshal(is);

        for (XmlUser user : authData.getUsers()) {
            credentialsCache.put(
                    user.getEmail(),
                    new XmlUserCredentials(
                            user.getEmail(),
                            user.getPassword(),
                            UserRole.valueOf(user.getRole())
                    )
            );
        }
    }

    public Optional<XmlUserCredentials> loadUserByEmail(String email) {
        return Optional.ofNullable(credentialsCache.get(email));
    }

    @XmlRootElement(name = "auth-data")
    @XmlAccessorType(XmlAccessType.FIELD)
    @Setter @Getter
    public static class AuthData {
        @XmlElementWrapper(name = "users")
        @XmlElement(name = "user")
        private List<XmlUser> users;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @Setter @Getter
    public static class XmlUser {
        @XmlElement(name = "email")
        private String email;
        @XmlElement(name = "password")
        private String password;
        @XmlElement(name = "role")
        private String role;
    }

    public record XmlUserCredentials(String email, String password, UserRole role) {
    }
}
