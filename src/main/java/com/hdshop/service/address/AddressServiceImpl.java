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
                .map(this::mapEntityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AddressDTO addAddress(AddressDTO addressDTO, Principal principal) {
        String username = principal.getName();

        User user = getUserByUsername(username);

        Address address = modelMapper.map(addressDTO, Address.class);
        address.setUser(user);
        address.setIsDefault(user.getAddresses().isEmpty());

        Address newAddress = addressRepository.save(address);

        return mapEntityToDTO(newAddress);
    }

    @Override
    public AddressDTO updateAddress(AddressDTO address, Long addressId) {
        Address existingAddress = getAddressById(addressId);

        // Cập nhật các trường từ addressDTO vào existingAddress
        existingAddress.setFullName(address.getFullName());
        existingAddress.setPhoneNumber(address.getPhoneNumber());
        existingAddress.setDistrict(address.getDistrict());
        existingAddress.setWard(address.getWard());
        existingAddress.setOrderDetails(address.getOrderDetails());

        // Lưu cập nhật vào cơ sở dữ liệu
        Address updatedAddress = addressRepository.save(existingAddress);

        return mapEntityToDTO(updatedAddress);
    }

    @Override
    public AddressDTO getOneAddress(Long addressId) {
        Address address = getAddressById(addressId);
        return mapEntityToDTO(address);
    }

    @Override
    public List<AddressDTO> setDefaultAddress(Long addressId, Principal principal) {
        String username = principal.getName();

        // find the address
        Address newDefaultAddress = getAddressById(addressId);

        User user = getUserByUsername(username);

        // check the user's current default address
        Address currentDefaultAddress = user.getAddresses().stream()
                .filter(Address::getIsDefault)
                .findFirst()
                .orElse(null);

        if (currentDefaultAddress != null) {
            // cancel the current default address
            currentDefaultAddress.setIsDefault(false);
            addressRepository.save(currentDefaultAddress);
        }

        // set default
        newDefaultAddress.setIsDefault(true);
        addressRepository.save(newDefaultAddress);

        return addressRepository.findAllByUserUsername(username)
                .stream()
                .map(this::mapEntityToDTO)
                .collect(Collectors.toList());
    }

    private Address getAddressById(Long addressId) {
        return addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException(getMessage("retrieving-address-information-failed")));
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
