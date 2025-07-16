package com.example.demo.services;

import com.example.demo.data.UserEntity;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserEntity createUser(UserEntity user);
    List<UserEntity> getAllUsers();
    Optional<UserEntity> getUserById(Integer id);
    void deleteUser(Integer id);
    void changeUserRole(int userId, String roleName);
    Optional<UserEntity> findByLogin(String login);
    void saveUserSortPreference(UserEntity user, String sortPreference);

}
