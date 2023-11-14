package com.hdshop.service.address;

import com.hdshop.dto.address.AddressDTO;

import java.security.Principal;
import java.util.List;

public interface AddressService {
    List<AddressDTO> getAllAddressForUser(final Principal principal);
}
