package com.lcwd.store.services;

import com.lcwd.store.entities.GatePass;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface GatePassService {
    List<GatePass> listAll();

    GatePass save(GatePass gatePass);

    @Transactional
    GatePass update(Long id, GatePass updatedGatePass);

    void delete(Long id);
}
