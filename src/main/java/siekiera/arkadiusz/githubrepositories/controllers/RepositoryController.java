package siekiera.arkadiusz.githubrepositories.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import siekiera.arkadiusz.githubrepositories.models.GithubRepo;
import siekiera.arkadiusz.githubrepositories.models.UserStars;

import java.util.List;

@RestController
@RequestMapping("/repos")
public class RepositoryController {

    @GetMapping("/{user}")
    public ResponseEntity<List<GithubRepo>> getUserRepositories(@PathVariable String user) {
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/{user}/stars")
    public ResponseEntity<UserStars> getUserStarsInAllRepositories(@PathVariable String user) {
        return ResponseEntity.ok(new UserStars(user, 0));
    }

}
