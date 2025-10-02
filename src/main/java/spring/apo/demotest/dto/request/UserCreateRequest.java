package spring.apo.demotest.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreateRequest {
    @NotBlank(message = "USER_USERNAME_NOT_FOUND")
    @Size(min = 5, message = "USER_INVALID_USERNAME")
    String username;

    @Size(min = 8, message = "USER_INVALID_PASSWORD")
    String password;

    String firstName;
    String lastName;
    LocalDate birthDate;
}
