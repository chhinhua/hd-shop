package com.duck.service.address;

import com.duck.dto.address.AddressDTO;
import com.duck.entity.Address;
import com.duck.entity.User;
import com.duck.exception.InvalidException;
import com.duck.exception.ResourceNotFoundException;
import com.duck.repository.AddressRepository;
import com.duck.service.user.UserService;
import com.duck.utils.PhoneNumberUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AddressServiceImpl implements AddressService {
    AddressRepository addressRepository;
    UserService userService;
    MessageSource messageSource;
    ModelMapper modelMapper;

    @Override
    public List<AddressDTO> getYourAdresses(Principal principal) {
        String username = principal.getName();
        return addressRepository.findAllByUserUsernameAndIsDeletedIsFalse(username)
                .stream()
                .map(this::mapEntityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AddressDTO add(AddressDTO addressDTO, Principal principal) {
        validate(addressDTO);

        String username = principal.getName();
        User user = userService.findByUsername(username);

        Address address = modelMapper.map(addressDTO, Address.class);
        address.setUser(user);
        address.setIsDefault(user.getAddresses().isEmpty());
        address.setIsDeleted(false);

        Address newAddress = addressRepository.save(address);

        return mapEntityToDTO(newAddress);
    }

    @Override
    public AddressDTO update(AddressDTO address, Long addressId) {
        Address existingAddress = findById(addressId);
        validate(address);

        // update
        existingAddress.setFullName(address.getFullName());
        existingAddress.setPhoneNumber(address.getPhoneNumber());
        existingAddress.setProvince(address.getProvince());
        existingAddress.setProvinceId(address.getProvinceId());
        existingAddress.setDistrict(address.getDistrict());
        existingAddress.setDistrictId(address.getDistrictId());
        existingAddress.setWard(address.getWard());
        existingAddress.setWardCode(address.getWardCode());
        existingAddress.setOrderDetails(address.getOrderDetails());

        // save change
        Address updatedAddress = addressRepository.save(existingAddress);
        return mapEntityToDTO(updatedAddress);
    }

    @Override
    public AddressDTO getOne(Long addressId) {
        Address address = findById(addressId);
        return mapEntityToDTO(address);
    }

    @Override
    public List<AddressDTO> setDefault(Long addressId, Principal principal) {
        String username = principal.getName();

        // find the address
        Address newDefaultAddress = findById(addressId);

        User user = userService.findByUsername(username);

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

        return addressRepository.findAllByUserUsernameAndIsDeletedIsFalse(username)
                .stream()
                .map(this::mapEntityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public String delete(Long addressId, Principal principal) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException(getMessage("address-not-found")));
        address.setIsDeleted(true);
        addressRepository.save(address);

        return getMessage("deleted-successfully");
    }

    @Override
    public Address findById(Long addressId) {
        return addressRepository.findById(addressId).orElseThrow(() ->
                new ResourceNotFoundException(getMessage("retrieving-address-information-failed"))
        );
    }

    private void validate(AddressDTO address) {
        if (address.getFullName().isBlank()) {
            throw new InvalidException(getMessage("fullname-must-not-be-empty"));
        }
        if (address.getPhoneNumber().isBlank()) {
            throw new InvalidException(getMessage("phone-number-must-not-be-empty"));
        }
        if (!PhoneNumberUtils.isValidPhoneNumber(address.getPhoneNumber())) {
            throw new InvalidException(getMessage("invalid-phone-number"));
        }
        if (address.getProvince().isBlank()) {
            throw new InvalidException(getMessage("province-must-not-be-empty"));
        }
        if (address.getProvinceId().toString().isBlank()) {
            throw new InvalidException(getMessage("province-id-must-not-be-empty"));
        }
        if (address.getDistrict().isBlank()) {
            throw new InvalidException(getMessage("district-must-not-be-empty"));
        }
        if (address.getDistrictId().toString().isBlank()) {
            throw new InvalidException(getMessage("district-id-must-not-be-empty"));
        }
        if (address.getWard().isBlank()) {
            throw new InvalidException(getMessage("ward-must-not-be-empty"));
        }
        if (address.getWardCode().isBlank()) {
            throw new InvalidException(getMessage("ward-code-must-not-be-empty"));
        }
    }

    private AddressDTO mapEntityToDTO(Address address) {
        return modelMapper.map(address, AddressDTO.class);
    }

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}
