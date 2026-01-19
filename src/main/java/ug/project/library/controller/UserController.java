package ug.project.library.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ug.project.library.dto.UserDto;
import ug.project.library.service.UserService;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Zarządzanie użytkownikami")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Pobierz wszystkich użytkowników z paginacją")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista użytkowników została pobrana")
    })
    @GetMapping
    public ResponseEntity<Page<UserDto>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @Operation(summary = "Pobierz użytkownika po ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Użytkownik znaleziony"),
            @ApiResponse(responseCode = "404", description = "Nie znaleziono użytkownika o podanym ID")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserDtoById(id));
    }

    @Operation(summary = "Aktualizuj użytkownika po ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Użytkownik został zaktualizowany"),
            @ApiResponse(responseCode = "404", description = "Nie znaleziono użytkownika o podanym ID"),
            @ApiResponse(responseCode = "400", description = "Niepoprawne dane wejściowe")
    })
    @PutMapping("/update/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @Valid @RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.updateUser(id, userDto));
    }

    @Operation(summary = "Usuń użytkownika po ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Użytkownik został usunięty"),
            @ApiResponse(responseCode = "404", description = "Nie znaleziono użytkownika o podanym ID")
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
