package com.delivery.config;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.AuthorizationService;
import org.camunda.bpm.engine.FilterService;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.authorization.Authorization;
import org.camunda.bpm.engine.authorization.Permissions;
import org.camunda.bpm.engine.authorization.Resources;
import org.camunda.bpm.engine.filter.Filter;
import org.camunda.bpm.engine.identity.Group;
import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.engine.task.TaskQuery;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
@RequiredArgsConstructor
public class CamundaConfig {
    private final IdentityService identityService;
    private final AuthorizationService authorizationService;
    private final FilterService filterService;
    private final TaskService taskService;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        // Create user groups
        createGroup("store", "Store Group", "Store users group");
        createGroup("courier", "Courier Group", "Courier users group");

        // Create users
        //createUser("store1", "a", "store", "1", null, "store");
        //createUser("courier1", "a", "courier", "1", null, "courier");

        // Create authorizations
        createGroupAuthorization(Authorization.AUTH_TYPE_GLOBAL, Authorization.ANY, Resources.APPLICATION, "tasklist", Permissions.ALL);

        // Create filters
        createMyTasksFilter();
    }
    
    public void createGroup(String id, String name, String type) {
        if (identityService.createGroupQuery().groupId(id).count() == 0) {
            Group group = identityService.newGroup(id);
            group.setName(name);
            group.setType(type);
            identityService.saveGroup(group);
        }
    }

    public void createUser(String id, String password, String firstName, String lastName, String email, String... groups) {
        if (identityService.createUserQuery().userId(id).count() == 0) {
            User user = identityService.newUser(id);
            user.setPassword(password);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            identityService.saveUser(user);
            
            for (String groupId : groups) {
                identityService.createMembership(id, groupId);
            }
        }
    }

    public void createGroupAuthorization(int authorizationType, String groupId, Resources resource, String resourceId, Permissions... permissions) {
        Authorization authorization = authorizationService.createNewAuthorization(authorizationType);
        if (authorizationType != Authorization.AUTH_TYPE_GLOBAL)
            authorization.setGroupId(groupId);
        authorization.setResource(resource);
        authorization.setResourceId(resourceId);

        for (Permissions permission : permissions) {
            authorization.addPermission(permission);
        }

        authorizationService.saveAuthorization(authorization);
    }

    public void createMyTasksFilter() {
        if (filterService.createFilterQuery().filterName("My Tasks").singleResult() != null)
            return;

        TaskQuery taskQuery = taskService.createTaskQuery().taskAssigneeExpression("${currentUser()}");

        Filter taskFilter = filterService.newTaskFilter("My Tasks");
        taskFilter.setOwner("whatever");
        taskFilter.setQuery(taskQuery);
        taskFilter = filterService.saveFilter(taskFilter);

        Authorization authorization = authorizationService.createNewAuthorization(Authorization.AUTH_TYPE_GLOBAL);
        authorization.setUserId(Authorization.ANY);
        authorization.setResource(Resources.FILTER);
        authorization.setResourceId(taskFilter.getId());
        authorization.addPermission(Permissions.READ);
        authorizationService.saveAuthorization(authorization);
    }
}
