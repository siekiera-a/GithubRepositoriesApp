package siekiera.arkadiusz.githubrepositories.services;

import siekiera.arkadiusz.githubrepositories.exceptions.ExternalServiceException;
import siekiera.arkadiusz.githubrepositories.exceptions.InvalidUsernameException;
import siekiera.arkadiusz.githubrepositories.models.Repository;

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

}
