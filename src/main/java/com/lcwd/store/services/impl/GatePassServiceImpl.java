package com.lcwd.store.services.impl;


import com.lcwd.store.entities.GatePass;
import com.lcwd.store.repositories.GatePassRepository;
import com.lcwd.store.services.GatePassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GatePassServiceImpl implements GatePassService {
    private final GatePassRepository repository;

    @Autowired
    public GatePassServiceImpl(GatePassRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<GatePass> listAll() {
        return repository.findAll();
    }

    @Override
    public GatePass save(GatePass gatePass) {
        return repository.save(gatePass);
    }
    @Transactional
    @Override
    public GatePass update(Long id, GatePass updatedGatePass) {
        GatePass gatePass = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("GatePass not found with id " + id));

        gatePass.setName(updatedGatePass.getName());
        gatePass.setEdin(updatedGatePass.getEdin());
        gatePass.setTimeIn(updatedGatePass.getTimeIn());
        gatePass.setTimeOut(updatedGatePass.getTimeOut());
        gatePass.setDateIn(updatedGatePass.getDateIn());
        gatePass.setDateOut(updatedGatePass.getDateOut());
        gatePass.setReason(updatedGatePass.getReason());
        return gatePass;
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

}
