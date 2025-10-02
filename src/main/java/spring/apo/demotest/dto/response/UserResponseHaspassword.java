package spring.apo.demotest.dto.response;

import java.time.LocalDate;
import java.util.Set;

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
public class UserResponseHaspassword {
    String id;
    String username;
    String password;
    String firstName;
    String lastName;
    LocalDate birthDate;
    String role;
    // nếu muốn trả thêm usage history
    Set<UsageHistoryResponse> usageHistories;
}
