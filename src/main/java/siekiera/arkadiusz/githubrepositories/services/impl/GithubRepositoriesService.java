package siekiera.arkadiusz.githubrepositories.services.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import siekiera.arkadiusz.githubrepositories.exceptions.ExternalServiceException;
import siekiera.arkadiusz.githubrepositories.exceptions.InvalidUsernameException;
import siekiera.arkadiusz.githubrepositories.models.Repository;
import siekiera.arkadiusz.githubrepositories.models.UserStars;
import siekiera.arkadiusz.githubrepositories.services.RepositoriesService;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GithubRepositoriesService implements RepositoriesService {

    GithubApiUrlService githubApiUrlService;
    RestTemplate restTemplate;
    String apiToken;
    ExecutorService executorService;
    Log log;

    public GithubRepositoriesService(GithubApiUrlService githubApiUrlService,
                                     RestTemplate restTemplate,
                                     @Value("${github-api.token:#{null}}") String apiToken,
                                     @Value("${system.concurrency.max-threads:15}") int threadsCount) {
        int maxThreads = 30;
        if (threadsCount <= 0 || threadsCount > maxThreads) {
            throw new IllegalArgumentException(String.format("Max threads count should be from " +
                "range <1, %d> Provided: %d", maxThreads, threadsCount));
        }

        this.githubApiUrlService = githubApiUrlService;
        this.restTemplate = restTemplate;
        this.apiToken = apiToken;
        executorService = Executors.newFixedThreadPool(threadsCount);
        log = LogFactory.getLog(getClass());
    }

    private HttpEntity getHttpEntity() {
        if (apiToken == null || apiToken.isBlank()) {
            return null;
        }

        var httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "token " + apiToken);
        return new HttpEntity<>(httpHeaders);
    }

    private ResponseEntity<List<Repository>> fetchRepositories(String url) {
        return restTemplate.exchange(url, HttpMethod.GET, getHttpEntity(),
            new ParameterizedTypeReference<>() {
            });
    }

    private List<CompletableFuture<List<Repository>>> createFetchTasks(String username,
                                                                       int toIncluded) {
        List<CompletableFuture<List<Repository>>> tasks = new ArrayList<>();

        for (int page = 2; page <= toIncluded; page++) {
            String url = githubApiUrlService.getUserRepositoriesUrl(username, page);
            tasks.add(CompletableFuture.supplyAsync(
                () -> fetchRepositories(url).getBody(),
                executorService));
        }

        return tasks;
    }

    private List<Repository> joinFetchTasks(List<CompletableFuture<List<Repository>>> tasks) {
        var allTasksDone =
            CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()]));
        allTasksDone.join();
        return tasks.stream()
            .map(CompletableFuture::join)
            .reduce(new ArrayList<>(),
                (acc, repositories) -> {
                    acc.addAll(repositories);
                    return acc;
                });
    }

    private List<Repository> getRepositories(String username) throws ExternalServiceException {
        String url = githubApiUrlService.getUserRepositoriesUrl(username, 1);
        try {
            var response = fetchRepositories(url);
            int pageCount = githubApiUrlService.getPageCount(response.getHeaders());
            var firstPage = response.getBody();

            if (pageCount <= 1 || firstPage == null) {
                return firstPage;
            }

            var tasks = createFetchTasks(username, pageCount);

            List<Repository> allRepositories = new ArrayList<>(firstPage);
            allRepositories.addAll(joinFetchTasks(tasks));

            return allRepositories;
        } catch (HttpClientErrorException e) {
            if (HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
                return null;
            }
            throw new ExternalServiceException(e);
        } catch (Exception e) {
            throw new ExternalServiceException(e);
        }
    }

    private void checkIfUsernameIsValid(String username) {
        if (username == null || username.isBlank()) {
            log.info("Invalid username! " + username);
            throw new InvalidUsernameException("Invalid username: " + username);
        }
    }

    @Override
    public List<Repository> getUserRepositories(String username) throws ExternalServiceException,
        InvalidUsernameException {
        checkIfUsernameIsValid(username);

        try {
            var start = Instant.now();
            var repositories = getRepositories(username);

            if (repositories == null) {
                log.info(String.format("User %s not found on github!", username));
            } else {
                var durationInMillis = Duration.between(start, Instant.now()).toMillis();
                log.info(String.format("Fetched %d %s's repositories in %d ms", repositories.size(),
                    username, durationInMillis));
            }

            return repositories;
        } catch (Exception e) {
            log.error("Error while retrieving repositories", e);
            throw new ExternalServiceException(e);
        }
    }

    @Override
    public UserStars getStarsFromAllUserRepositories(String username) throws ExternalServiceException,
        InvalidUsernameException {
        checkIfUsernameIsValid(username);

        try {
            var start = Instant.now();
            var repositories = getRepositories(username);

            if (repositories == null) {
                log.info(String.format("User %s not found on github!", username));
                return null;
            }

            var durationInMillis = Duration.between(start, Instant.now()).toMillis();
            log.info(String.format("Fetched %d %s's repositories in %d ms", repositories.size(),
                username, durationInMillis));

            var starsSum = repositories.stream()
                .reduce(0L, (acc, repository) -> acc + repository.getStars(), Long::sum);

            return new UserStars(username, starsSum);
        } catch (Exception e) {
            log.error("Error while retrieving repositories", e);
            throw new ExternalServiceException(e);
        }
    }
}
