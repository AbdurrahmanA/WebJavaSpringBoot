package com.example.demo.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "CATEGORY")
public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;

    @NotNull
    @Size(min = 3, max = 20)
    @Pattern(regexp = "^[a-z]{3,20}$", message = "Category name must be lowercase and 3-20 letters long")
    @Column(name = "NAME", nullable = false, unique = true, length = 20)
    private String name;
}
