package net.thumbtack.onlineshop.controller;

import net.thumbtack.onlineshop.database.support.CommonClearDatabaseNode;
import net.thumbtack.onlineshop.dto.EmptyResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DebugController {

    @Autowired
    private CommonClearDatabaseNode commonClearDatabaseNode;

    @PostMapping(value = "/debug/clear")
    public ResponseEntity clearDatabase() {
        System.out.println("hello");
        commonClearDatabaseNode.clearDatabase();
        return ResponseEntity.ok().body(new EmptyResponse());
    }


}
