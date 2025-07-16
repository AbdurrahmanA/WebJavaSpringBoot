package com.example.demo.services;

import com.example.demo.data.UserEntity;
import com.example.demo.repositories.UserEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserEntityRepository userRepository;

    @Autowired
    public UserServiceImpl(UserEntityRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public void saveUserSortPreference(UserEntity user, String sortPreference) {
        if (user != null) {
            user.setSortPreference(sortPreference);
            userRepository.save(user);
        }
    }

    @Override
    public UserEntity createUser(UserEntity user) {
        if (user.getRole() == null) {
            user.setRole(UserEntity.Role.ROLE_LIMITED_USER);
        }
        return userRepository.save(user);
    }

    @Override
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<UserEntity> getUserById(Integer id) {
        return userRepository.findById(id);
    }

    @Override
    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }

    @Override
    public void changeUserRole(int userId, String roleName) {
        Optional<UserEntity> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();
            user.setRole(UserEntity.Role.valueOf(roleName));
            userRepository.save(user);
        }
    }

    @Override
    public Optional<UserEntity> findByLogin(String login) {
        return userRepository.findByLogin(login);
    }
}
