package car.demo.global.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommonResponse<T> {
    private boolean success;
    private T data;
    private String message;

    public static <T> CommonResponse<T> ok(T data) {
        return new CommonResponse<>(true, data, "Success");
    }

    public static <T> CommonResponse<T> error(String message) {
        return new CommonResponse<>(false, null, message);
    }
}
