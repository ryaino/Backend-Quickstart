package field.ryan.backendquickstart.db.dtos;

import lombok.Data;

import java.util.*;

@Data
public class UserDto {

    private UUID id;
    private String email;
    private List<String> roles = new ArrayList<>();

}
