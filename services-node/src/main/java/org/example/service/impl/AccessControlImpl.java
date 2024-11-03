package org.example.service.impl;

import org.example.command.RoleProvider;
import org.example.entity.NodeUser;
import org.example.entity.enams.Role;
import org.example.service.AccessControl;
import org.springframework.stereotype.Service;

/**
 * Данный клас проверяет доступ прав пользователей до класса командера;
 */
@Service
public class AccessControlImpl implements AccessControl {

    /**
     * Проверяет доступ к объекту на основе его требуемой роли и текущей роли пользователя.
     *
     * @param object     объект, доступ к которому проверяется. Должен реализовывать интерфейс {@link RoleProvider}.
     * @param currentRole текущая роль пользователя, которая проверяется на доступ.
     * @return           {@code true}, если пользователь с текущей ролью имеет доступ к объекту; {@code false} иначе.
     *
     * <p>
     * Логика доступа следующая:
     * <ul>
     *   <li>Если требуемая роль объекта {@link Role#USER}, доступ предоставляется всем ролям.</li>
     *   <li>Если требуемая роль объекта {@link Role#ADMIN}, доступ предоставляется только ролям {@link Role#ADMIN} и {@link Role#SUPERUSER}.</li>
     *   <li>Если требуемая роль объекта {@link Role#SUPERUSER}, доступ предоставляется только роли {@link Role#SUPERUSER}.</li>
     * </ul>
     * </p>
     *
     * <p>Если объект не реализует интерфейс {@link RoleProvider}, метод возвращает {@code false}.</p>
     */
    @Override
    public boolean hasAccess(Object object, Role currentRole) {
        if (object instanceof RoleProvider) {
            Role requiredRole = ((RoleProvider) object).getRole();

            return switch (requiredRole) {
                case USER -> true;
                case ADMIN -> currentRole == Role.ADMIN || currentRole == Role.SUPERUSER;
                case SUPERUSER -> currentRole == Role.SUPERUSER;
                default -> false;
            };
        }
        return false;
    }

    @Override
    public boolean isActive(NodeUser nodeUser) {
        return nodeUser.getIsActive();
    }
}
