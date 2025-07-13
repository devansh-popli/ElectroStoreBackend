package com.lcwd.store.controller;

import com.lcwd.store.dtos.GatePassDto;
import com.lcwd.store.entities.GatePass;
import com.lcwd.store.services.GatePassService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/gatepasses")
public class GatePassController {

    private final GatePassService service;
    private final ModelMapper modelMapper;

    @Autowired
    public GatePassController(GatePassService service, ModelMapper modelMapper) {
        this.service = service;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public List<GatePassDto> list() {
        return service.listAll().stream()
                .map(gatePass -> modelMapper.map(gatePass, GatePassDto.class))
                .collect(Collectors.toList());
    }

    @PostMapping
    public GatePassDto add(@RequestBody GatePassDto GatePassDto) {
        GatePass gatePass = modelMapper.map(GatePassDto, GatePass.class);
        GatePass savedGatePass = service.save(gatePass);
        return modelMapper.map(savedGatePass, GatePassDto.class);
    }
    @PutMapping("/{id}")
    public GatePassDto update(@PathVariable Long id, @RequestBody GatePassDto gatePassDTO) {
        GatePass updatedGatePass = modelMapper.map(gatePassDTO, GatePass.class);
        GatePass gatePass = service.update(id, updatedGatePass);
        return modelMapper.map(gatePass, GatePassDto.class);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
