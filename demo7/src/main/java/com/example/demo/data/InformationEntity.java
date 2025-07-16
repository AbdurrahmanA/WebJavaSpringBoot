package com.example.demo.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@Entity
@Table(name = "INFORMATION")
public class InformationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;

    @NotNull
    @Size(min = 3, max = 20)
    @Column(name = "TITLE", nullable = false, length = 20)
    private String title;

    @NotNull
    @Size(min = 5, max = 500)
    @Column(name = "CONTENT", nullable = false, length = 500)
    private String content;

    @Size(max = 255)
    @Column(name = "LINK")
    private String link;

    @NotNull
    @Column(name = "DATE_ADDED", nullable = false)
    private LocalDate dateAdded = LocalDate.now();

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "OWNER_ID", nullable = false)
    private UserEntity owner;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "CATEGORY_ID", nullable = false)
    private CategoryEntity category;

    @ManyToOne
    @JoinColumn(name = "SHARED_WITH_ID", nullable = true)
    private UserEntity sharedWith;

    public String getFormattedDate() {
        return dateAdded.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }
}
