package com.hdshop.service.address;

import com.hdshop.dto.address.AddressDTO;

import java.security.Principal;
import java.util.List;

public interface AddressService {
    List<AddressDTO> getAllAddressForUser(final Principal principal);

    AddressDTO addAddress(final AddressDTO address, final Principal principal);

    AddressDTO updateAddress(final AddressDTO address, final Long addressId);

    AddressDTO getOneAddress(final Long addressId);

    List<AddressDTO> setDefaultAddress(final Long addressId, final Principal principal);

    String deleteAddress(final Long addressId, final Principal principal);
}
