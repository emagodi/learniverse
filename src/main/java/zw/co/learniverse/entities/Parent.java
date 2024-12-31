package zw.co.learniverse.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import zw.co.learniverse.handlers.BaseEntity;


@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "parents")
@EntityListeners(AuditingEntityListener.class)
public class Parent extends BaseEntity {

    private String firstName;

    private String lastName;

    private String middleName;

    //    @Pattern(regexp = "^\\+?[0-9. ()-]{7,}$", message = "Invalid phone number format!")
    private String phonenumber;

    private String whatsappNumber;

    @Email
    private String email;

    private String address;

    private String district;

    private String province;

    private String country;

    private Long userId;
}
