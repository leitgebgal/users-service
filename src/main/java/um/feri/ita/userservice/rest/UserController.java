package um.feri.ita.userservice.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import um.feri.ita.userservice.service.UserService;
import um.feri.ita.userservice.vao.User;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@CrossOrigin
@RestController
@RequestMapping("/api/users")
public class UserController {
    private static final Logger log = Logger.getLogger(UserController.class.toString());

    private final UserService userService;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getUsers() {
        log.info("Retrieving all users");
        List<User> users = userService.getUsers();
        log.info("Retrieved all users, count: " + users.size());
        return users;
    }
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id){

        Optional<User> user = userService.getUserById(id);
        return user.map(u -> {
            log.info("Found user with ID: " + id);
            return ResponseEntity.ok(u);
        }).orElseGet(() -> {
            log.info("User with ID: " + id + " not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        });
    }

    @PostMapping("/create")
    public @ResponseBody ResponseEntity<User> createUser(@RequestBody User user) {
        User newUser = userService.createUser(user);
        log.info("Created a new user with ID: " + newUser.getId());
        return ResponseEntity.ok(newUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody User updatedUser) {
        try {
            Optional<User> updatedUserOptional = userService.updateUser(id, updatedUser);
            return updatedUserOptional.map(user -> {
                log.info("Updated user with ID: " + user.getId());
                return ResponseEntity.ok(user);
            }).orElseGet(() -> {
                log.info("User with ID: " + id + " not found for updating");
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            });
        } catch (IllegalArgumentException ex) {
            log.info("Invalid request body!");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        return userService.getUserById(id).map(user -> {
            userService.deleteUser(id);
            log.info("Deleted user with ID: " + id);
            return ResponseEntity.ok("User with ID " + id + " successfully deleted.");
        }).orElseGet(() -> {
            log.info("User with ID: " + id + " not found for deletion");
            return ResponseEntity.badRequest().body("User with ID " + id + " not found for deletion.");
        });
    }
}
