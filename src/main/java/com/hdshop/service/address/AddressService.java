package com.hdshop.service.address;

import com.hdshop.dto.address.AddressDTO;
import com.hdshop.entity.Address;

import java.security.Principal;
import java.util.List;

public interface AddressService {
    Address findById(final Long id);

    List<AddressDTO> getAllAddressForUser(final Principal principal);

    AddressDTO addAddress(final AddressDTO address, final Principal principal);

    AddressDTO updateAddress(final AddressDTO address, final Long addressId);

    AddressDTO getOne(final Long addressId);

    List<AddressDTO> setDefault(final Long addressId, final Principal principal);

    String delete(final Long addressId, final Principal principal);
}
