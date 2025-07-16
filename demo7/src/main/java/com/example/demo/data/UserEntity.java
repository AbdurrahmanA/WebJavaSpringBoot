    package com.example.demo.data;

    import jakarta.persistence.*;
    import jakarta.validation.constraints.NotNull;
    import jakarta.validation.constraints.Size;
    import lombok.Getter;
    import lombok.Setter;

    @Getter
    @Setter
    @Entity
    @Table(name = "USERS")
    public class UserEntity {

        public enum Role {
            ROLE_ADMIN,
            ROLE_FULL_USER,
            ROLE_LIMITED_USER;
        }

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "ID", nullable = false)
        private Integer id;

        @Size(max = 20)
        @NotNull
        @Column(name = "FIRST_NAME", nullable = false, length = 20)
        private String firstName;

        @Size(max = 50)
        @NotNull
        @Column(name = "LAST_NAME", nullable = false, length = 50)
        private String lastName;

        @Size(max = 20)
        @NotNull
        @Column(name = "LOGIN", nullable = false, length = 20, unique = true)
        private String login;

        @Size(max = 255)
        @NotNull
        @Column(name = "PASSWORD", nullable = false)
        private String password;

        @NotNull
        @Column(name = "AGE", nullable = false)
        private Integer age;

        @Enumerated(EnumType.STRING)
        @Column(name = "ROLE", length = 50)
        private Role role;

        @Size(max = 50)
        @Column(name = "SORT_PREFERENCE", length = 50)
        private String sortPreference;
    }
