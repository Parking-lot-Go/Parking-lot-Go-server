package car.demo.domain.SeoulAPI.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CursorResponseDto<T> {
    private List<T> content;
    private Long nextCursor;
    private Boolean hasNext;

    public static <T> CursorResponseDto<T> of(List<T> content, Long nextCursor, Boolean hasNext) {
        return CursorResponseDto.<T>builder()
                .content(content)
                .nextCursor(nextCursor)
                .hasNext(hasNext)
                .build();
    }
}
