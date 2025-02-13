package vn.congdubai.dreamjob.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.congdubai.dreamjob.domain.Role;
import vn.congdubai.dreamjob.domain.User;
import vn.congdubai.dreamjob.domain.response.ResCreateUserDTO;
import vn.congdubai.dreamjob.domain.response.ResUserDTO;
import vn.congdubai.dreamjob.domain.response.ResultPaginationDTO;
import vn.congdubai.dreamjob.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleService roleService;

    public UserService(UserRepository userRepository, RoleService roleService) {
        this.userRepository = userRepository;
        this.roleService = roleService;
    }

    // fetch all user
    public ResultPaginationDTO getAllUser(Specification<User> spec, Pageable pageable) {
        Page<User> userPage = this.userRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(userPage.getTotalPages());
        mt.setTotal(userPage.getTotalElements());
        rs.setMeta(mt);

        List<ResUserDTO> listUser = userPage.getContent()
                .stream().map(item -> this.convertToResUserDTO(item))
                .collect(Collectors.toList());
        rs.setResult(listUser);
        return rs;
    }

    // convert user to ResUserDTO
    public ResUserDTO convertToResUserDTO(User user) {
        ResUserDTO res = new ResUserDTO();
        ResUserDTO.RoleUser role = new ResUserDTO.RoleUser();

        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setUpdatedAt(user.getUpdatedAt());
        res.setCreatedAt(user.getCreatedAt());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());

        if (user.getRole() != null) {
            role.setId(user.getRole().getId());
            role.setName(user.getRole().getName());
            res.setRole(role);
        }
        return res;
    }

    // create new user
    public User createUser(User user) {
        if (user.getRole() != null) {
            Role role = this.roleService.fetchById(user.getRole().getId());
            user.setRole(role != null ? role : null);
        }
        return this.userRepository.save(user);
    }

    // Convert to ResCreateUserDTO
    public ResCreateUserDTO convertToResCreateUserDTO(User user) {
        ResCreateUserDTO res = new ResCreateUserDTO();

        res.setId(user.getId());
        res.setName(user.getName());
        res.setEmail(user.getEmail());
        res.setAge(user.getAge());
        res.setCreatedAt(user.getCreatedAt());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());

        return res;
    }

    // fetch user by id
    public User fetchUserById(long id) {
        Optional<User> userOptional = this.userRepository.findById(id);
        if (userOptional.isPresent()) {
            return userOptional.get();
        }
        return null;
    }

    // check email Exist
    public boolean isEmailExist(String email) {
        return this.userRepository.existsByEmail(email);
    }

    // delete a user
    public void deleteUser(long id) {
        this.userRepository.softDeleteUser(id);
    }
}
