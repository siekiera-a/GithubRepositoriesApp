package siekiera.arkadiusz.githubrepositories.controllers;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import siekiera.arkadiusz.githubrepositories.exceptions.ExternalServiceException;
import siekiera.arkadiusz.githubrepositories.exceptions.InvalidUsernameException;
import siekiera.arkadiusz.githubrepositories.models.LanguageBytes;
import siekiera.arkadiusz.githubrepositories.models.Repository;
import siekiera.arkadiusz.githubrepositories.models.UserStars;
import siekiera.arkadiusz.githubrepositories.services.RepositoriesService;

import java.util.List;

@RestController
@RequestMapping("/repos")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class RepositoryController {

    RepositoriesService repositoriesService;

    @GetMapping("/{user}")
    public ResponseEntity<List<Repository>> getUserRepositories(@PathVariable String user) {
        try {
            var repositories = repositoriesService.getUserRepositories(user);
            return repositories == null ? new ResponseEntity<>(HttpStatus.NOT_FOUND) :
                ResponseEntity.ok(repositories);
        } catch (InvalidUsernameException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (ExternalServiceException e) {
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    @GetMapping("/{user}/stars")
    public ResponseEntity<UserStars> getUserStarsInAllRepositories(@PathVariable String user) {
        try {
            var userStars = repositoriesService.getStarsFromAllUserRepositories(user);
            return userStars == null ? new ResponseEntity<>(HttpStatus.NOT_FOUND) :
                ResponseEntity.ok(userStars);
        } catch (InvalidUsernameException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (ExternalServiceException e) {
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    @GetMapping("/{user}/favourite-languages")
    public ResponseEntity<List<LanguageBytes>> getMostPopularUserLanguages(@PathVariable String user) {
        try {
            var mostPopularLanguages = repositoriesService.getMostPopularLanguages(user);
            return mostPopularLanguages == null ? new ResponseEntity<>(HttpStatus.NOT_FOUND) :
                ResponseEntity.ok(mostPopularLanguages);
        } catch (InvalidUsernameException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (ExternalServiceException e) {
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

}
