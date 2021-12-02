package com.example.passwordKeepr.passwordKeeprTest.Passwords;
import com.example.passwordKeepr.passwordKeeprTest.Users.User;
import com.example.passwordKeepr.passwordKeeprTest.Users.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class GetPasswordsByUsersService {

    private final PasswordsRepository passwordsRepository;
    private final UsersRepository usersRepository;

    @Autowired
    public GetPasswordsByUsersService(PasswordsRepository passwordsRepository, UsersRepository usersRepository) {
        this.passwordsRepository = passwordsRepository;
        this.usersRepository = usersRepository;
    }

    public <lookupRequestObject> List getPasswordsByUser(Map<String, Object> lookupRequestObject) throws Exception {
        String uuid = (String) lookupRequestObject.get("sessionUuid");
        User userFromDb = usersRepository.findByUuid(uuid);
        String usersMasterPassword = userFromDb.getMasterPassword();
        AES AESEncryptor = new AES(usersMasterPassword);

        if (userFromDb == null) {
            throw new IllegalStateException("This user doesn't exist or have any passwords!");
        }

        List passwords = userFromDb.getPasswordList();

        for (int i = 0; i < passwords.size(); i++) {
            Password password = (Password) passwords.get(i);
            String passwordText = password.getPassword_text();
            String decryptedPassword = AESEncryptor.decrypt(passwordText);
            password.setPassword_text(decryptedPassword);
        }

        return passwords;
    }
}
