package siekiera.arkadiusz.githubrepositories.services;

public interface ApiUrlService {

    /***
     * Create url to data about user's repositories with pagination
     * @param username username
     * @param page starts from 1
     * @return url to resource
     */
    String getUserRepositoriesUrl(String username, int page);

    /***
     * Get url to data about languages usage in repository
     * @param username username
     * @param repository user repository
     * @return url to resource
     */
    String getRepositoryLanguagesUrl(String username, String repository);

}
