//package com.vbz.dbcards.service;
//
//import org.springframework.stereotype.Service;
//
//import com.vbz.dbcards.entity.UserLdap;            // ✅ CORRECT ENTITY
//import com.vbz.dbcards.repository.UserLdapRepository;
//
//@Service
//public class UserService {
//
//    private final UserLdapRepository userLdapRepository;
//
//    public UserService(UserLdapRepository userLdapRepository) {
//        this.userLdapRepository = userLdapRepository;
//    }
//
//   
////    public void resetPassword(String username, String newPassword) {
////
////        UserLdap user = userLdapRepository.findByUsername(username)
////                .orElseThrow(() -> new RuntimeException("User not found"));
////
////    
////        user.setPassword(newPassword);
////        userLdapRepository.save(user);AA
////    }
//}
//
//        



package com.vbz.dbcards.service;

import com.vbz.dbcards.entity.User;
import com.vbz.dbcards.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserService {

    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    
    public Map<String, Object> getUserProfile(Long userId) {

        User user = repo.findById(userId).orElse(null);

        if (user == null) {
            return Map.of(
                    "status", 0,
                    "message", "User not found"
            );
        }

        return Map.of(
                "status", 1,
                "message", "Profile fetched successfully",
                "data", user
        );
    }
    public Map<String, Object> updateUserProfile(
            Long userId,
            Map<String, Object> body) {

        User user = repo.findById(userId).orElse(null);

        if (user == null) {
            return Map.of(
                    "status", 0,
                    "message", "User not found"
            );
        }

    
        if (body.containsKey("mobileNumber")) {
            return Map.of(
                    "status", 0,
                    "message", "Mobile number cannot be updated"
            );
        }


        if (body.containsKey("firstName"))
            user.setFirstName((String) body.get("firstName"));

        if (body.containsKey("middleName"))
            user.setMiddleName((String) body.get("middleName"));

        if (body.containsKey("lastName"))
            user.setLastName((String) body.get("lastName"));

        if (body.containsKey("email"))
            user.setEmail((String) body.get("email"));

        repo.save(user);

        return Map.of(
                "status", 1,
                "message", "Profile updated successfully",
                "data", user
        );
    }
}

