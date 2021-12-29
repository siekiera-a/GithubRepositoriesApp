package siekiera.arkadiusz.githubrepositories.services.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import siekiera.arkadiusz.githubrepositories.services.ApiUrlService;

import java.util.regex.Pattern;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GithubApiUrlService implements ApiUrlService {

    @Value("${github-users-api.url}")
    String githubUserApiUrl;

    @Value("${github-users-api.page-size:30}")
    long githubUserApiPageSize;

    final Pattern pattern;

    public GithubApiUrlService() {
        pattern = Pattern.compile("(?<=<.{0,}[&?]page=)\\d+(?=[^>]*>[^>]*rel=\"last\")");
    }

    @Override
    public String getUserRepositoriesUrl(String username, int page) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username can not be null or blank");
        }

        String githubUserReposUrl = String.format("%s/%s/repos?per_page=%d", githubUserApiUrl,
            username,
            githubUserApiPageSize);

        if (page <= 1) {
            return githubUserReposUrl;
        }
        return String.format("%s&page=%d", githubUserReposUrl, page);
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
        var matcher = pattern.matcher(linkHeader);

        if (matcher.find()) {
            return Integer.parseInt(matcher.group());
        }

        return 1;
    }

}
