package vn.congdubai.dreamjob.controller.admin;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.congdubai.dreamjob.domain.User;
import vn.congdubai.dreamjob.domain.response.ResCreateUserDTO;
import vn.congdubai.dreamjob.domain.response.ResultPaginationDTO;
import vn.congdubai.dreamjob.service.UserService;
import vn.congdubai.dreamjob.util.annotation.ApiMessage;
import vn.congdubai.dreamjob.util.error.IdInvalidException;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/users")
    @ApiMessage("Fetch all user success")
    public ResponseEntity<ResultPaginationDTO> fetchAllUser(@Filter Specification<User> spec,
            Pageable pageable) {
        return ResponseEntity.ok(this.userService.getAllUser(spec, pageable));
    }

    @PostMapping("/users")
    @ApiMessage("Create user success")
    public ResponseEntity<ResCreateUserDTO> handleCreateUser(@Valid @RequestBody User postUser)
            throws IdInvalidException {
        boolean isEmailExist = this.userService.isEmailExist(postUser.getEmail());
        if (isEmailExist) {
            throw new IdInvalidException("Email " + postUser.getEmail() + "đã tồn tại, vui lòng sử dụng email khác.");
        }
        String hashPassword = this.passwordEncoder.encode(postUser.getPassword());
        postUser.setPassword(hashPassword);
        User user = this.userService.createUser(postUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreateUserDTO(user));
    }

    @ApiMessage("Delete user")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable("id") long id)
            throws IdInvalidException {
        User user = this.userService.fetchUserById(id);
        if (user == null) {
            throw new IdInvalidException("Id " + id + " Không tồn tại.");
        }
        this.userService.deleteUser(id);
        return ResponseEntity.ok(null);
    }
}
