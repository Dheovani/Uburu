/**
 *  @file PathController.java
 *  @author Dheovani Xavier da Cruz
 *
 *  Copyright 2023, Dheovani Xavier da Cruz.  All rights reserved.
 *  https://github.com/Dheovani/Uburu
 *  Use of this source code is governed by a MIT license
 *  that can be found in the License file.
 *
 *  Uburu
 */

package br.com.uburu.spring.controller;

import java.util.List;

import javax.swing.JFileChooser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.uburu.spring.document.Path;
import br.com.uburu.spring.service.PathService;

@RestController
@RequestMapping("/api/v1/path")
@CrossOrigin(origins = "http://localhost:3000")
public class PathController {

    @Autowired
    private PathService service;

    @GetMapping
    public ResponseEntity<List<Path>> getAll() {
        List<Path> paths = service.getAll();
        return new ResponseEntity<List<Path>>(paths, HttpStatus.OK);
    }

    @GetMapping("/select")
    public ResponseEntity<Path> select() {
        try {
            final JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            
            int resp = fc.showSaveDialog(null);
            if (resp == JFileChooser.APPROVE_OPTION) {
                final Path path = new Path();
                path.setPath(fc.getSelectedFile().getAbsolutePath());

                return new ResponseEntity<Path>(path, HttpStatus.OK);
            }
        
            return ResponseEntity.notFound().build();
        } catch (final Exception e) {
            e.printStackTrace();            
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<Path> include(@RequestBody Path path) {
        service.save(path);
        return new ResponseEntity<Path>(path, HttpStatus.CREATED);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteAll() {
        service.deleteAll();
        return ResponseEntity.accepted().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@RequestParam String id) {
        if (service.findeById(id) != null) {
            service.deleteById(id);
            return ResponseEntity.accepted().build();
        }

        return ResponseEntity.notFound().build();
    }
    
}