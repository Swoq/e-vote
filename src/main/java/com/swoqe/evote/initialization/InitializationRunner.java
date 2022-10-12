package com.swoqe.evote.initialization;

import com.swoqe.evote.security.VoterRepository;
import com.swoqe.evote.security.model.Voter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;

@Component
@ConditionalOnProperty(value="initialization_mode", havingValue = "true")
@AllArgsConstructor
@Slf4j
public class InitializationRunner implements CommandLineRunner {

    private final VoterRepository voterRepository;
    private final PasswordEncoder passwordEncoder;
    private final KeyPair cvkKeyPair;

    private final List<Voter> initVoters = List.of(
            new Voter("voter1", "password1"),
            new Voter("voter2", "password2"),
            new Voter("voter3", "password3"),
            new Voter("voter4", "password4"),
            new Voter("voter5", "password5"),
            new Voter("voter6", "password6")
    );

    @Override
    public void run(String... args) throws IOException, NoSuchAlgorithmException {

        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);

        initVoters.forEach(voter -> {
            KeyPair keyPair = generator.generateKeyPair();

            try {
                File newFile = new File("votersPrivate/" + voter.getUsername());
                newFile.createNewFile();

                try (FileOutputStream fos = new FileOutputStream(newFile)) {
                    fos.write(keyPair.getPrivate().getEncoded());
                } catch (IOException e) {
                    log.error(String.valueOf(e));
                }

                voter.setId(UUID.randomUUID());
                voter.setPublicKey(keyPair.getPublic().getEncoded());
                voter.setPassword(passwordEncoder.encode(voter.getPassword()));
                voterRepository.save(voter);

            } catch (IOException e) {
                log.info("Initialization has been aborted.");
                throw new RuntimeException(e);
            }


        });

        File privateKey = new File("cvk/cvk.private");
        privateKey.createNewFile();
        try (FileOutputStream stream = new FileOutputStream(privateKey)) {
            stream.write(cvkKeyPair.getPrivate().getEncoded());
        }

        File publicKey = new File("cvk/cvk.public");
        publicKey.createNewFile();
        try (FileOutputStream stream = new FileOutputStream(publicKey)) {
            stream.write(cvkKeyPair.getPublic().getEncoded());
        }

        log.info("Initialization has been completed successfully.");
    }

}
