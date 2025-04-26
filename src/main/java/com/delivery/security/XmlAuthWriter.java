package com.delivery.security;

import com.delivery.exception.ApiException;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@RequiredArgsConstructor
public class XmlAuthWriter {
    private final XmlAuthLoader authLoader;
    //private final ResourceLoader resourceLoader;

    public boolean userExists(String email) {
        return authLoader.getCredentialsCache().containsKey(email);
    }

    public void addUser(String email, String encodedPassword, UserRole role) {
        authLoader.getCredentialsCache().put(
                email,
                new XmlAuthLoader.XmlUserCredentials(email, encodedPassword, role)
        );

        try {
            var xmlFile = new File("auth-users.xml");
            //Path xmlPath = Paths.get(resourceLoader.getResource("classpath:auth-users.xml").getURI());

            XmlAuthLoader.AuthData data = new XmlAuthLoader.AuthData();
            data.setUsers(
                    authLoader.getCredentialsCache().values().stream()
                            .map(c -> {
                                XmlAuthLoader.XmlUser user = new XmlAuthLoader.XmlUser();
                                user.setEmail(c.email());
                                user.setPassword(encodedPassword);
                                user.setRole(c.role().name());
                                return user;
                            })
                            .toList()
            );

            JAXBContext context = JAXBContext.newInstance(XmlAuthLoader.AuthData.class);
            var marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(data, xmlFile);  //xmlPath.toFile()

        } catch (Exception e) {
            throw new ApiException("Failed to update auth data", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
