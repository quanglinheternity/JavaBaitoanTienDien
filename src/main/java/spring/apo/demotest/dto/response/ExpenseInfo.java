package spring.apo.demotest.dto.response;

public record ExpenseInfo(
    String category,
    String intemName,
    double amount
) {

}
