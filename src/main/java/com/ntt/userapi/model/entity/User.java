package com.ntt.userapi.model.entity;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter // Generate getters
@Setter // Generate setters
@NoArgsConstructor
@AllArgsConstructor
// Exclude the 'phones' relationship from toString to avoid LazyInitializationException/N+1
@ToString(exclude = "phones")
// Do NOT use @EqualsAndHashCode here. Rely on default Object identity.
// If you absolutely need content-based equals/hashCode (e.g., for Sets),
// consider implementing manually or configuring carefully (e.g., only on ID,
// but be aware of transient entity issues).
// For most JPA scenarios, relying on identity is safer.
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @Column(unique = true)
    private String email;

    private String password; // Storing the encoded password

    private LocalDateTime created;
    private LocalDateTime modified;
    private LocalDateTime lastLogin;

    private String token; // Store the generated JWT token

    private boolean isActive;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    // Relationship field - excluded from toString
    private List<Phone> phones;

    // Manual implementation of equals and hashCode is often preferred for complex cases,
    // but excluding the Lombok auto-generation is the first step to fix the warning.
    // Example if you needed it based on ID for persisted entities:
    /*
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        // Compare based on ID, only if ID is not null (entity is persisted)
        return id != null && Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        // Hash code based on ID, only if ID is not null
        return id != null ? Objects.hash(id) : super.hashCode(); // Use Object hash code for transient entities
    }
     */
}