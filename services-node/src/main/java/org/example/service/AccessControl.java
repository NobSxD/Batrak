package org.example.service;

import org.example.entity.NodeUser;
import org.example.entity.enams.Role;

public interface AccessControl {
    boolean hasAccess(Object object, Role currentRole);
    boolean isActive(NodeUser nodeUser);
}
