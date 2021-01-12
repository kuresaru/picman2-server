package top.scraft.picmanserver.data;

import lombok.Data;

import java.util.Set;

@Data
public class UpdatePictureRequest {

    private String description;
    private Set<String> tags;

}
