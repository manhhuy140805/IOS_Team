package io.volunteerapp.volunteer_app.Util;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // nếu null thì ko hiển thị trong json cho nó chuyên nghiệp nhé
public class RestResponse<T> {
private int status = 200;
    private String message = "Success";
    private T data;
    private String error;
}
