package com.app.flashcash.service;

import com.app.flashcash.entity.Account;
import com.app.flashcash.entity.User;
import com.app.flashcash.repository.UserRepository;
import com.app.flashcash.service.form.SignupForm;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@Transactional //assure l'intégralité des données
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Constructeur pour l'injection des dépendances
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Ajout de la méthode pour récupérer un utilisateur par email
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'email: " + email));
    }

    // La méthode registration qui retourne maintenant un User
    public User registration(SignupForm form) {
        //  Vérification de l'email
        if (userRepository.existsByEmail(form.getEmail())) {
            throw new RuntimeException("Email déjà utilisé");
        }
        if (!form.getPassword().equals(form.getConfirmPassword())) {
            throw new RuntimeException("Les mots de passe ne correspondent pas");
        }


        // Crée un user et set les champs
        User user = new User();
        user.setFirstName(form.getFirstName());
        user.setLastName(form.getLastName());
        user.setEmail(form.getEmail());
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setBalance(BigDecimal.ZERO);

        //  Création du compte
        Account account = new Account();
        account.setUser(user);
        account.setAccountNumber(generateAccountNumber());
        account.setIban(generateIban());
        account.setBalance(BigDecimal.ZERO);

        //  Ajout du compte à l'utilisateur
        List<Account> accounts = new ArrayList<>();
        accounts.add(account);
        user.setAccounts(accounts);

        //  Sauvegarde de l'utilisateur
        return userRepository.save(user);
    }

    // génère un numéro de compte
    private String generateAccountNumber() {
        LocalDateTime now = LocalDateTime.now();
        Random random = new Random();
        return String.format("FC-%s-%04d",
                now.format(DateTimeFormatter.BASIC_ISO_DATE),
                random.nextInt(10000));
    }

    // génère un IBAN
    private String generateIban() {
        Random random = new Random();
        return String.format("FR76-%012d", random.nextInt(1000000000));
    }
}