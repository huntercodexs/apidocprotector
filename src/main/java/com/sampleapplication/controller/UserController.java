package com.sampleapplication.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
@RestController
@RequestMapping("${api.prefix}")
@Tag(name = "Open Api Swagger Demo with Controller Advice")
@CrossOrigin(origins = "*")
@Slf4j
public class UserController {

    @Operation(hidden = true)
    @GetMapping(path = {"/", "/welcome"})
    @ResponseBody
    public ResponseEntity<?> welcome() {
        return ResponseEntity.ok().body("Welcome to APIDOC PROTECTOR");
    }

    /**
     * Read User
     */
    @Operation(
            summary = "Get one user by id",
            description = "Description about this function"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Read one user successfully", content = {
                    @Content(mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {
                    @Content(mediaType = "text")
            }),
            @ApiResponse(responseCode = "404", description = "User not found", content = {
                    @Content(mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
                    @Content(mediaType = "application/json")
            })
    })
    @GetMapping(path = "/users/{userid}")
    @ResponseBody
    public ResponseEntity<?> readOne(@PathVariable("userid") String userid) {
        return ResponseEntity.ok().body("Get one user by id");
    }

    /**
     * Read All Users
     */
    @Operation(
            summary = "Get all users",
            description = "Description about this function"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Read all users successfully", content = {
                    @Content(mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {
                    @Content(mediaType = "text")
            }),
            @ApiResponse(responseCode = "404", description = "Users not found", content = {
                    @Content(mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
                    @Content(mediaType = "application/json")
            })
    })
    @GetMapping(path = "/users")
    @ResponseBody
    public ResponseEntity<?> readAll() {
        return ResponseEntity.ok().body("Get all users");
    }

    /**
     * Create User
     */
    @Operation(
            summary = "Add new user by Rest Template",
            description = "Description about this function"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User created successfully", content = {
                    @Content(mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "201", description = "User created successfully", content = {
                    @Content(mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "302", description = "User name already exists", content = {
                    @Content(mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "400", description = "Missing body request", content = {
                    @Content(mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {
                    @Content(mediaType = "text")
            }),
            @ApiResponse(responseCode = "409", description = "User name already exists", content = {
                    @Content(mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Server error", content = {
                    @Content(mediaType = "application/json")
            })
    })
    @PostMapping(path = "/users")
    @ResponseBody
    public ResponseEntity<?> create(@RequestBody Object user) {
        return ResponseEntity.ok().body("Add new user by Rest Template");
    }

    /**
     * Delete User
     */
    @Operation(
            summary = "Delete an user by id",
            description = "Description about this function"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully", content = {
                    @Content(mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {
                    @Content(mediaType = "text")
            }),
            @ApiResponse(responseCode = "404", description = "User not found", content = {
                    @Content(mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
                    @Content(mediaType = "application/json")
            })
    })
    @DeleteMapping(path = "/users/{userid}")
    @ResponseBody
    public ResponseEntity<?> delete(@PathVariable("userid") String userid) {
        return ResponseEntity.ok().body("Delete an user by id");
    }

    /**
     * Update User
     */
    @Operation(
            summary = "Update an user by id",
            description = "Description about this function"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully", content = {
                    @Content(mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "400", description = "Missing body request", content = {
                    @Content(mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {
                    @Content(mediaType = "text")
            }),
            @ApiResponse(responseCode = "404", description = "User not found", content = {
                    @Content(mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "406", description = "Update is not correct, because it should be total data update", content = {
                    @Content(mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Server error", content = {
                    @Content(mediaType = "application/json")
            })
    })
    @PutMapping(path = "/users/{userid}")
    @ResponseBody
    public ResponseEntity<?> update(@PathVariable("userid") String userid, @RequestBody Object user) {
        return ResponseEntity.ok().body("Update an user by id");
    }

    /**
     * Patch User
     */
    @Operation(
            summary = "Fix/Patch an user by id",
            description = "Description about this function"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User patched successfully", content = {
                    @Content(mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "400", description = "Missing body request", content = {
                    @Content(mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {
                    @Content(mediaType = "text")
            }),
            @ApiResponse(responseCode = "404", description = "User not found", content = {
                    @Content(mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "406", description = "Patch is not correct, because it should be partial update", content = {
                    @Content(mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
                    @Content(mediaType = "application/json")
            })
    })
    @PatchMapping(path = "/users/{userid}")
    @ResponseBody
    public ResponseEntity<?> patch(@PathVariable("userid") String userid, @RequestBody Object user) {
        return ResponseEntity.ok().body("Fix/Patch an user by id");
    }

}
