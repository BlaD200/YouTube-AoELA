package com.vsynytsyn.userservice.service;

import com.vsynytsyn.userservice.domain.UserEntity;
import com.vsynytsyn.userservice.dto.UserDTO;
import com.vsynytsyn.userservice.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper mapper;
    private final BCryptPasswordEncoder encoder;


    @Autowired
    protected UserService(UserRepository repository, ModelMapper mapper, BCryptPasswordEncoder encoder) {
        this.userRepository = repository;
        this.mapper = mapper;
        this.encoder = encoder;
    }


    public Page<UserEntity> getAll(Pageable pageable, String username) {
        if (username != null)
            return userRepository.findAllByUsernameContains(pageable, username);
        return userRepository.findAll(pageable);
    }


    public UserEntity getOne(Long id) {
        return userRepository.findById(id).orElse(null);
    }


    public UserEntity create(UserEntity userEntity) throws RuntimeException {
        try {
            userEntity.setPassword(encoder.encode(userEntity.getPassword()));
            return userRepository.save(userEntity);
        } catch (TransactionSystemException e) {
            throw new RuntimeException(e.getMessage());
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException(e.getCause().getCause().getMessage().split("\n")[1].trim());
        }
    }


    public UserEntity update(UserEntity userEntity, UserDTO userDTO) {
        mapper.map(userDTO, userEntity);

        if (userDTO.getPassword() != null)
            userEntity.setPassword(encoder.encode(userDTO.getPassword()));

        return userRepository.save(userEntity);
    }


    public boolean delete(UserEntity userEntity, UserEntity currentUser) {
        if (userEntity == null)
            return false;
        if (currentUser != null && userEntity.getUserId().equals(currentUser.getUserId()))
            throw new RuntimeException(currentUser.getUsername() + " cannot delete him/herself.");
        try {
            userRepository.delete(userEntity);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
