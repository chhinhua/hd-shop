package com.hdshop.controller;

import com.hdshop.dto.address.AddressDTO;
import com.hdshop.service.address.AddressService;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "Get list addresses of current user")
    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public ResponseEntity<List<AddressDTO>> getMyAddress(Principal principal) {
        return ResponseEntity.ok(addressService.getAllAddressForUser(principal));
    }

    @Operation(summary = "Add a new address")
    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<AddressDTO> addAddress(@RequestBody AddressDTO addressDTO, Principal principal) {
        return ResponseEntity.ok(addressService.addAddress(addressDTO, principal));
    }
}
