package com.hdshop.dtos;

import com.hdshop.entities.Address;
import com.hdshop.entities.Review;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    private String firstName;

    private String lastName;

    @Column(nullable = false, unique = true)
    @Email
    private String email;

    private String id_card;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    private String gender;

    private String avatar;

    private Date createAt;

    private Date updateAt;
}
