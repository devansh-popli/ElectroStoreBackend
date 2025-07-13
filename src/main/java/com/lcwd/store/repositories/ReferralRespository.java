package com.lcwd.store.repositories;

import com.lcwd.store.entities.Referral;
import com.lcwd.store.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReferralRespository extends JpaRepository<Referral,Integer> {
    Optional<Referral> findByReferralCode(String referralCode);

    Referral findByUser(User map);
}
