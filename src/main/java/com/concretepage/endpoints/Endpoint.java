package com.concretepage.endpoints;

import java.util.*;

import com.concretepage.codemark_ws.*;
import com.concretepage.entity.Role;
import com.concretepage.entity.User;
import com.concretepage.service.RoleService;
import com.concretepage.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@org.springframework.ws.server.endpoint.annotation.Endpoint
public class Endpoint {
	private static final String NAMESPACE_URI = "http://www.concretepage.com/codemark-ws";
	@Autowired
	private UserService userService;

	@Autowired
	private RoleService roleService;

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getAllUsersRequest")
	@ResponsePayload
	public GetAllUsersResponse getUsers(@RequestPayload GetAllUsersRequest request) {
		GetAllUsersResponse response = new GetAllUsersResponse();
		List<SimpleUser> simpleUserList = new ArrayList<>();
		List<User> users = userService.getAllUsers();
		for(int i = 0; i < users.size(); i++) {
			SimpleUser user = new SimpleUser();
			BeanUtils.copyProperties(users.get(i), user);
			simpleUserList.add(user);
		}
		response.getUser().addAll(simpleUserList);
		return response;
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getUserByLoginRequest")
	@ResponsePayload
	public GetUserByLoginResponse getUser(@RequestPayload GetUserByLoginRequest request) {
		GetUserByLoginResponse response = new GetUserByLoginResponse();
		FullUser user = new FullUser();
		User userEntity = userService.getByLogin(request.getUserLogin());
		System.out.println(userEntity.getAuthorities());
		BeanUtils.copyProperties(userEntity, user);
		HashSet<UserRole> userRoles = new HashSet<>();
		System.out.println(userEntity.getAuthorities());
		for(var role: userEntity.getAuthorities()) {
			UserRole userRole = new UserRole();
			BeanUtils.copyProperties(role, userRole);
			userRoles.add(userRole);
		}

		user.getRole().addAll(userRoles);
		response.setUserWithRoles(user);
		System.out.println(user.getRole());
		return response;
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "deleteUserRequest")
	@ResponsePayload
	public DeleteUserResponse deleteUser(@RequestPayload DeleteUserRequest request) {
		DeleteUserResponse response = new DeleteUserResponse();
		String userName = request.getLogin();
		userService.deleteUser(userName);
		return response;
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "addUserRequest")
	@ResponsePayload
	public AddUserResponse addUser(@RequestPayload AddUserRequest request) {
		ArrayList<String> errors = new ArrayList<>();
		AddUserResponse response = new AddUserResponse();
		User user = new User();
		if(request.getLogin() == null) {
			errors.add("Логин не может быть пустым");
		}
		if(request.getName() == null) {
			errors.add("Имя не может быть пустым");
		}
		if(request.getPassword() == null) {
			errors.add("Пароль не должен быть пустым");
		}
		if(!isCorrectPassword(request.getPassword())) {
			errors.add("Пароль должен сождержать хотя бы одну заглавную букву и цыфру");
		}
		System.out.println(errors.size());
		if(errors.size() != 0) {
			response.getError().addAll(errors);
			response.setSuccess(false);
			return response;
		}
		user.setLogin(request.getLogin());
		user.setName(request.getName());
		user.setPassword(request.getPassword());
		user.setRoles(mapRoles(request.getRole()));
		userService.save(user);
		response.setSuccess(true);
		return response;
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "updateUserRequest")
	@ResponsePayload
	public UpdateUserResponse updateUser(@RequestPayload UpdateUserRequest request) {
		ArrayList<String> errors = new ArrayList<>();
		UpdateUserResponse response = new UpdateUserResponse();
		User user = userService.getByLogin(request.getLogin());
		if(user == null) {
			response.setSuccess(false);
			response.getError().add("Пользователь с таким login не найден");
			return response;
		}
		if(request.getName() == null) {
			errors.add("Имя не может быть пустым");
		}
		if(request.getPassword() == null) {
			errors.add("Пароль не должен быть пустым");
		}
		if(!isCorrectPassword(request.getPassword())) {
			errors.add("Пароль должен сождержать хотя бы одну заглавную букву и цыфру");
		}
		if(errors.size() != 0) {
			response.getError().addAll(errors);
			response.setSuccess(false);
			return response;
		}
		user.setRoles(mapRoles(request.getRole()));
		user.setPassword(request.getPassword());
		user.setName(request.getName());
		userService.save(user);
		response.setSuccess(true);
		return response;
	}

	private HashSet<Role> mapRoles(List<String> rawRoles) {
		HashSet<Role> roles = new HashSet<>();
		for(var currentRole: rawRoles) {
			System.out.println(currentRole);
			Role role = roleService.getRoleByName(currentRole);
			roles.add(role);
			System.out.println(role.getName());
		}
		return roles;
	}

	private boolean isCorrectPassword(String password) {
		boolean hasUpperCase = false;
		boolean hasNum = false;
		for(int i = 0; i < password.length(); i++) {
			if(Character.isUpperCase(password.charAt(i))) hasUpperCase = true;
			if(Character.isDigit(password.charAt(i))) hasNum = true;
		}

		return hasUpperCase && hasNum;
	}
}
