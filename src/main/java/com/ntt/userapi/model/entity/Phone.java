package com.ntt.userapi.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "phones")
@Getter // Generate getters
@Setter // Generate setters
@NoArgsConstructor
@AllArgsConstructor
// Exclude the 'user' relationship from toString
@ToString(exclude = "user")
// Do NOT use @EqualsAndHashCode here. Rely on default Object identity.
public class Phone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String number;
    private String cityCode;
    private String countryCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // Foreign key column
    // Relationship field - excluded from toString
    private User user;
}