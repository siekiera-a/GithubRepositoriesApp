package siekiera.arkadiusz.githubrepositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import siekiera.arkadiusz.githubrepositories.services.impl.GithubApiUrlService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class GithubApiUrlServiceTest {

    @Autowired
    GithubApiUrlService githubApiUrlService;

    private HttpHeaders prepareHeaders(String linkHeader) {
        var headers = new HttpHeaders();
        headers.add("Link", linkHeader);
        return headers;
    }

    @Test
    void contextLoads() {
    }

    @Test
    void testGetPageCount_PageNotSpecified_Returns1() {
        int actual = githubApiUrlService.getPageCount(prepareHeaders("<https://api.github" +
            ".com/user/6154722/repos?page=9>;"));
        assertEquals(1, actual);
    }

    @Test
    void testGetPageCount_PageSpecifiedWithoutAdditionalParams_ReturnsValidValue() {
        int actual = githubApiUrlService.getPageCount(prepareHeaders("<https://api.github" +
            ".com/user/6154722/repos?page=9>; rel=\"prev\", <https://api.github" +
            ".com/user/6154722/repos?page=11>; rel=\"next\", <https://api.github" +
            ".com/user/6154722/repos?page=152>; rel=\"last\", <https://api.github" +
            ".com/user/6154722/repos?page=1>; rel=\"first\""));
        assertEquals(152, actual);
    }

    @Test
    void testGetPageCount_PageSpecifiedWithAdditionalParams_ReturnsValidValue() {
        int actual = githubApiUrlService.getPageCount(prepareHeaders("<https://api.github" +
            ".com/user/6154722/repos?page=9&per_page=50>; rel=\"prev\", <https://api.github" +
            ".com/user/6154722/repos?page=11>; rel=\"next\", <https://api.github" +
            ".com/user/6154722/repos?page=152&per_page=50>; rel=\"last\", <https://api.github" +
            ".com/user/6154722/repos?page=1&per_page=50>; rel=\"first\""));
        assertEquals(152, actual);
    }

    @Test
    void testGetPageCount_PageSpecifiedWithAdditionalParamsInRandomOrder_ReturnsValidValue() {
        int actual = githubApiUrlService.getPageCount(prepareHeaders("<https://api.github" +
            ".com/user/6154722/repos?per_page=50>; rel=\"prev\", <https://api.github" +
            ".com/user/6154722/repos?page=11>; rel=\"next\", <https://api.github" +
            ".com/user/6154722/repos?per_page=50&page=152>; rel=\"last\", <https://api.github" +
            ".com/user/6154722/repos?per_page=50&page=1>; rel=\"first\""));
        assertEquals(152, actual);
    }

    @Test
    void testGetRepositoryLanguagesUrl_BlankOrNullUsernameOrRepository_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> githubApiUrlService.getRepositoryLanguagesUrl(null,"repo"));
        assertThrows(IllegalArgumentException.class, () -> githubApiUrlService.getRepositoryLanguagesUrl(" ","repo"));
        assertThrows(IllegalArgumentException.class, () -> githubApiUrlService.getRepositoryLanguagesUrl("user",null));
        assertThrows(IllegalArgumentException.class, () -> githubApiUrlService.getRepositoryLanguagesUrl("user"," "));
    }

    @Test
    void testGetRepositoryLanguagesUrl_ValidUsernameAndRepository_ReturnsValidUrl() {
        assertEquals("https://api.github.com/repos/allegro/bigcache/languages", githubApiUrlService.getRepositoryLanguagesUrl("allegro", "bigcache"));
    }

}
