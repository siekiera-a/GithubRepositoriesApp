package siekiera.arkadiusz.githubrepositories.services;

import siekiera.arkadiusz.githubrepositories.exceptions.ExternalServiceException;
import siekiera.arkadiusz.githubrepositories.exceptions.InvalidUsernameException;
import siekiera.arkadiusz.githubrepositories.models.LanguageBytes;
import siekiera.arkadiusz.githubrepositories.models.Repository;
import siekiera.arkadiusz.githubrepositories.models.UserStars;

import java.util.List;

public interface RepositoriesService {

    /***
     * Get user repositories from external service, e.g. Github
     * @param username username
     * @return null if user no exists, otherwise list of repositories
     * @throws InvalidUsernameException while username is null or blank
     * @throws ExternalServiceException while unexpected error occurred during fetching repositories
     */
    List<Repository> getUserRepositories(String username) throws ExternalServiceException, InvalidUsernameException;

    /***
     * Get sum of stars from user's repositories from external service, e.g. Github
     * @param username username
     * @return null if user no exists, otherwise sum of stars and username
     * @throws InvalidUsernameException while username is null or blank
     * @throws ExternalServiceException while unexpected error occurred during fetching repositories
     */
    UserStars getStarsFromAllUserRepositories(String username) throws ExternalServiceException, InvalidUsernameException;

    /***
     * Get list of user the most popular languages
     * @param username username
     * @return null if user no exists, otherwise sorted list of the most popular languages
     * @throws InvalidUsernameException while username is null or blank
     * @throws ExternalServiceException while unexpected error occurred during fetching data
     */
    List<LanguageBytes> getMostPopularLanguages(String username) throws ExternalServiceException, InvalidUsernameException;

}
