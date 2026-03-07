package com.vbz.dbcards.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import com.vbz.dbcards.entity.RoleMaster;
import com.vbz.dbcards.repository.RoleMasterRepository;


@Configuration
public class RoleInitializer implements CommandLineRunner {
	
	private final RoleMasterRepository roleRepo;
	 
    public RoleInitializer(RoleMasterRepository roleRepo) {
        this.roleRepo = roleRepo;
    }
 
    @Override
    public void run(String... args) {
 
        if (roleRepo.count() == 0) {
 
            roleRepo.save(new RoleMaster((long) 1, "Super Admin"));
            roleRepo.save(new RoleMaster((long) 2, "Admin"));
            roleRepo.save(new RoleMaster((long) 3, "Web User"));
            roleRepo.save(new RoleMaster((long) 4, "Mobile User"));
 
            System.out.println("Role master initialized");
        }
    }

}
