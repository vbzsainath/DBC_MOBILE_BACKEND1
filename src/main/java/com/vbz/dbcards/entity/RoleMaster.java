package com.vbz.dbcards.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "role_master")
public class RoleMaster {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	private Long roleId;
	private String roleName;
	
	public RoleMaster() {
    }
	
	public RoleMaster(Long roleId, String roleName) {
        this.roleId = roleId;
        this.roleName = roleName;
    }
	

}
