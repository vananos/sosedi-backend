package io.github.vananos.sosedi.security.permission;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(fluent = true)
public class TargetObjectInfo {
    public Object targetObject;
    public String targetType;
    public Serializable targetId;
}
