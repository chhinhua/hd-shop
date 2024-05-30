package com.hdshop.service.address;

import com.hdshop.dto.address.AddressDTO;
import com.hdshop.entity.Address;

import java.security.Principal;
import java.util.List;

public interface AddressService {
    AddressDTO getOne(final Long addressId);

    Address findById(final Long id);

    List<AddressDTO> getYourAdresses(final Principal principal);

    List<AddressDTO> setDefault(final Long addressId, final Principal principal);

    AddressDTO add(final AddressDTO address, final Principal principal);

    AddressDTO update(final AddressDTO address, final Long addressId);

    String delete(final Long addressId, final Principal principal);
}
