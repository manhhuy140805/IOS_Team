package io.volunteerapp.volunteer_app.Util;


import io.volunteerapp.volunteer_app.Util.Anotation.ApiMessage;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;


@ControllerAdvice //
public class FormatResponse implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true; // true để xử lý các response body
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        HttpServletResponse servletResponse = ((ServletServerHttpResponse) response).getServletResponse();
        int status = servletResponse.getStatus();
        ;

        if (body instanceof RestResponse) {
            return body;
        }

        RestResponse<Object> restResponse = new RestResponse<>();

        restResponse.setStatus(status);
        String path = request.getURI().getPath(); // để nếu sau này xài swagger thì ko bị ảnh hưởng
        if (path.startsWith("/v3/api-docs") || path.startsWith("/swagger-ui")) {
            return body;
        }

        if (body instanceof RestResponse || body instanceof String || body instanceof Resource) {
            return body;
        }

        if (status >= 400) {
            if (body instanceof String) {
                restResponse.setMessage((String) body);
            } else {
                restResponse.setMessage("Call api error");
                restResponse.setError(body != null ? body.toString() : "No error details");
            }
        } else {
            restResponse.setData(body);
            ApiMessage message = returnType.getMethodAnnotation(ApiMessage.class); // get annotation
            restResponse.setMessage(message != null ? message.value() : "Call api success");
        }
        return restResponse;

    }
}
