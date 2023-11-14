package com.hdshop.service.address;

import com.hdshop.dto.address.AddressDTO;
import com.hdshop.entity.Address;

import java.security.Principal;
import java.util.List;

public interface AddressService {
    List<AddressDTO> getAllAddressForUser(final Principal principal);

    AddressDTO addAddress(final AddressDTO address, final Principal principal);
}
