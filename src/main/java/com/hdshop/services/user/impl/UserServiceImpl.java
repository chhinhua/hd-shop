package com.hdshop.services.user.impl;

import com.hdshop.dtos.UserDTO;
import com.hdshop.entities.User;
import com.hdshop.exceptions.ResourceNotFoundException;
import com.hdshop.repositories.UserRepository;
import com.hdshop.services.user.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return mapToDTO(user);
    }

    private UserDTO mapToDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }

    // convert DTO to entity
    private User mapToEntity(UserDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }
}
