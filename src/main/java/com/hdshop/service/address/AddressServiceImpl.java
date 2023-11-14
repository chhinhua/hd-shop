package com.hdshop.service.address;

import com.hdshop.dto.address.AddressDTO;
import com.hdshop.entity.Address;
import com.hdshop.entity.User;
import com.hdshop.exception.ResourceNotFoundException;
import com.hdshop.repository.AddressRepository;
import com.hdshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final MessageSource messageSource;
    private final ModelMapper modelMapper;

    @Override
    public List<AddressDTO> getAllAddressForUser(Principal principal) {
        String username = principal.getName();
        return addressRepository.findAllByUserUsername(username)
                .stream()
                .map((element) -> mapEntityToDTO(element))
                .collect(Collectors.toList());
    }

    @Override
    public AddressDTO addAddress(AddressDTO addressDTO, Principal principal) {
        String username = principal.getName();

        User user = getUserByUsername(username);

        Address address = modelMapper.map(addressDTO, Address.class);
        address.setUser(user);
        address.setIsDefault(user.getAddresses().isEmpty() ? true : false);

        Address newAddress = addressRepository.save(address);

        return mapEntityToDTO(newAddress);
    }

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(getMessage("user-not-found")));
    }

    private AddressDTO mapEntityToDTO(Address address) {
        return modelMapper.map(address, AddressDTO.class);
    }

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}
