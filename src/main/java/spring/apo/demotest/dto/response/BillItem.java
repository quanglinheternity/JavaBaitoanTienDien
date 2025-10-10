package spring.apo.demotest.dto.response;

public record BillItem(
    
    String itemName,
    String unit,
    Integer quantity,
    Double price,
    Double total
) {

}
