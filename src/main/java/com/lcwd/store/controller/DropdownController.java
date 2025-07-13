package com.lcwd.store.controller;

import com.lcwd.store.entities.UOM;
import com.lcwd.store.services.DropdownService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("dropdown/")
public class DropdownController {

    @Autowired
    private DropdownService dropdownService;

    @GetMapping("/fetch/uom")
    public ResponseEntity<List<UOM>> fetchDataFromDatabase() {
        try {
            // Fetch data from the database
            List<UOM> uqcList = dropdownService.getAllUQCData(); // Implement this method in your service or repository

            return ResponseEntity.ok(uqcList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null); // Handle errors as needed
        }
    }
}
