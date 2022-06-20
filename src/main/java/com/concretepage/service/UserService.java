package com.concretepage.service;

import com.concretepage.entity.User;
import com.concretepage.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        List<User> users = (List<User>) userRepository.findAll();
        return users;
    }

    public User getByLogin(String login) {
        Optional<User> user = userRepository.findById(login);
        return user.get();
    }

    public void deleteUser(String login) {
        userRepository.deleteById(login);
    }

    public void save(User user) {
        userRepository.save(user);
    }
}
