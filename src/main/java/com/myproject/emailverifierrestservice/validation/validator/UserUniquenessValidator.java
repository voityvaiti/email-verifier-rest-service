package com.myproject.emailverifierrestservice.validation.validator;

import com.myproject.emailverifierrestservice.entity.AppUser;
import com.myproject.emailverifierrestservice.exception.UserDuplicationException;
import com.myproject.emailverifierrestservice.service.abstraction.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;

@Component
@RequiredArgsConstructor
public class UserUniquenessValidator {

    private final UserService userService;


    public void validate(AppUser user) throws UserDuplicationException {

        BeanPropertyBindingResult result = new BeanPropertyBindingResult(user, "user");

        validateUsername(user, result);

        if(result.hasErrors()) {
            throw new UserDuplicationException(result);
        }
    }

    private void validateUsername(AppUser user, BeanPropertyBindingResult result) {

        if (userService.existsWithEmail(user.getEmail())) {
            result.rejectValue("email", "user.email", "User with same email is already exists.");
        }

    }
}
