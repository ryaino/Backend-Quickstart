package field.ryan.backendquickstart.dto;

import lombok.Data;

import java.util.Map;

@Data
public class ActionPayload<T> {

    private ActionName action;
    private T input;
    private String request_query;
    private Map<String, String> session_variables;

}
