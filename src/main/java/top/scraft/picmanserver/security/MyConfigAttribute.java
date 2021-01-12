package top.scraft.picmanserver.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.access.ConfigAttribute;

@Deprecated
@Data
@AllArgsConstructor
public class MyConfigAttribute implements ConfigAttribute {

    private String attribute;

}
