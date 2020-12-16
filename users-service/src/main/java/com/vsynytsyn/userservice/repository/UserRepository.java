package com.vsynytsyn.userservice.repository;

import com.vsynytsyn.userservice.domain.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Page<UserEntity> findAllByUsernameContains(Pageable pageable, String username);
}
