package vn.congdubai.dreamjob.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import vn.congdubai.dreamjob.domain.Role;
import vn.congdubai.dreamjob.repository.RoleRepository;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role fetchById(long id) {
        Optional<Role> rOptional = this.roleRepository.findById(id);
        if (rOptional.isPresent()) {
            return rOptional.get();
        }
        return null;
    }
}
