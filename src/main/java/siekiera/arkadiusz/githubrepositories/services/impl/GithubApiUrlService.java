package siekiera.arkadiusz.githubrepositories.services.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import siekiera.arkadiusz.githubrepositories.services.ApiUrlService;

import java.util.Arrays;
import java.util.regex.Pattern;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GithubApiUrlService implements ApiUrlService {

    @Value("${github-api.url}")
    String githubApiUrl;

    @Value("${github-users-api.page-size:30}")
    long githubUserApiPageSize;

    final Pattern pageNumberPattern;

    public GithubApiUrlService() {
        // <https://api.github.com/user/6154722/repos?page=152&per_page=50>; -> 152
        // <https://api.github.com/user/6154722/repos?per_page=50&page=30>; -> 30
        pageNumberPattern = Pattern.compile("(?<=[?&]page=)\\d+");
    }

    @Override
    public String getUserRepositoriesUrl(String username, int page) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username can not be null or blank");
        }

        String githubUserReposUrl = String.format("%s/users/%s/repos?per_page=%d", githubApiUrl,
            username,
            githubUserApiPageSize);

        if (page <= 1) {
            return githubUserReposUrl;
        }
        return String.format("%s&page=%d", githubUserReposUrl, page);
    }

    @Override
    public String getRepositoryLanguagesUrl(String username, String repository) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username can not be null or blank");
        }

        if (repository == null || repository.isBlank()) {
            throw new IllegalArgumentException("Repository can not be null or blank");
        }

        return String.format("%s/repos/%s/%s/languages", githubApiUrl, username, repository);
    }

    /***
     * Get page count based od link header
     * @param headers response headers
     * @return page count
     */
    public int getPageCount(HttpHeaders headers) {
        var linkHeaderList = headers.get("Link");

        if (linkHeaderList == null) {
            return 1;
        }

        String linkHeader = String.join("", linkHeaderList);

        return Arrays.stream(linkHeader.split(","))
            .filter(link -> link.trim().toLowerCase().endsWith("rel=\"last\""))
            .findFirst()
            .map(linkWithLastRel -> {
                var matcher = pageNumberPattern.matcher(linkWithLastRel);
                if (matcher.find()) {
                    return Integer.parseInt(matcher.group());
                }
                return 1;
            }).orElse(1);
    }

}
