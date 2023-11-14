package com.hdshop.service.address;

import com.hdshop.dto.address.AddressDTO;
import com.hdshop.entity.Address;
import com.hdshop.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<AddressDTO> getAllAddressForUser(Principal principal) {
        String username = principal.getName();
        return addressRepository.findAllByUserUsername(username)
                .stream()
                .map((element) -> mapEntityToDTO(element))
                .collect(Collectors.toList());
    }

    private AddressDTO mapEntityToDTO(Address address) {
        return modelMapper.map(address, AddressDTO.class);
    }
}
