package com.lcwd.store.services;

import com.lcwd.store.entities.UOM;
import com.lcwd.store.repositories.UOMRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DropdownService {

        @Autowired
        private UOMRepository uqcRepository; // Inject your repository

        public List<UOM> getAllUQCData() {
            return uqcRepository.findAll(); // Use your repository method to fetch data
        }
}
