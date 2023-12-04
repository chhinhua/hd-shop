package com.hdshop.controller;

import com.hdshop.dto.address.AddressDTO;
import com.hdshop.service.address.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Tag(name = "Address")
@RestController
@RequestMapping("/api/v1/address")
public class AddressController {
    @Autowired
    private AddressService addressService;

    @SecurityRequirement(name = "Bear Authentication")
    @Operation(summary = "Get list addresses of current user", description = "Role user")
    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public ResponseEntity<List<AddressDTO>> getMyAddress(Principal principal) {
        return ResponseEntity.ok(addressService.getAllAddressForUser(principal));
    }

    @SecurityRequirement(name = "Bear Authentication")
    @Operation(summary = "Get addresses by addressId", description = "Role user")
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}")
    public ResponseEntity<AddressDTO> getOneAddress(@PathVariable(value = "id") Long addressId) {
        return ResponseEntity.ok(addressService.getOneAddress(addressId));
    }

    @SecurityRequirement(name = "Bear Authentication")
    @Operation(summary = "Add a new address", description = "Role user")
    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<AddressDTO> create(@RequestBody AddressDTO addressDTO, Principal principal) {
        return ResponseEntity.ok(addressService.addAddress(addressDTO, principal));
    }

    @SecurityRequirement(name = "Bear Authentication")
    @Operation(summary = "Update address", description = "Role user")
    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{id}")
    public ResponseEntity<AddressDTO> updateAddress(@PathVariable(value = "id") Long addressId,
                                                    @RequestBody AddressDTO addressDTO) {
        return ResponseEntity.ok(addressService.updateAddress(addressDTO, addressId));
    }

    @SecurityRequirement(name = "Bear Authentication")
    @Operation(summary = "Set isDefault for address", description = "Role user")
    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{id}/default")
    public ResponseEntity<List<AddressDTO>> setDefault(@PathVariable(value = "id") Long addressId, Principal principal) {
        return ResponseEntity.ok(addressService.setDefaultAddress(addressId, principal));
    }

    @SecurityRequirement(name = "Bear Authentication")
    @Operation(summary = "Delete address", description = "Role user")
    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAddress(@PathVariable(value = "id") Long addressId, Principal principal) {
        return ResponseEntity.ok(addressService.deleteAddress(addressId, principal));
    }
}
