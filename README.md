# Table of Contents

* [Description](#description)
* [Installation](#installation)
  * [First solution - docker (recommended)](#first-solution---docker-recommended)
  * [Second solution - local build](#second-solution---local-build)
* [Test application](#test-application)
* [Additional task - implementation proposal (Implemented!)](#additional-task---implementation-proposal) 
* [Ideas for the feature improvements](#ideas-for-the-feature-improvements)
* [Known issues](#known-issues)

## Description

Api allows you to:
* list user repositories (name and stars amount)
* return sum of stars in all user repositories
* [new] get list of the most popular user programming languages

for any github user.

## Installation

In case of choosing the first solution (docker) you can skip this step and go to Environment Variables. 

Clone project using:

```git clone https://github.com/siekiera-a/GithubRepositoriesApp.git```

Go to project directory.

### Environment Variables (all variables are optional):
* GITHUB_API_TOKEN - (string) personal github token (in case when api calls limit is exceed) - default not set (To generate token go to: https://github.com/settings/tokens)
* SERVER_MAX_THREADS - (number) server concurrent threads (<1-30> permitted) - default 15

### First solution - docker (recommended)

#### Requirements
* docker

Run commands below: 
```
docker pull arczi31/github-repositories:latest

// template: docker run -p {port}:8080 [...AdditionalVariables] arczi31/github-repositories:latest
// sample command with custom environment variables:
// docker run -p 8080:8080 -e SERVER_MAX_THREADS=20 -e GITHUB_API_TOKEN=token arczi31/github-repositories:latest

docker run -p 8080:8080 arczi31/github-repositories:latest
```
**Template variables:**

port - port on local machine

### Second solution - local build

#### Requirements
* JDK - version 11 or above

Environment variables can be also changed in *application.properties* file (before compiling).

Compile app using command below in project directory (specified for your operating system):

```
// windows
.\mvnw.cmd clean install

// linux and ios (check if file have executable permissions)
./mvnw clean install 
```

Then go to target directory and run:
```
// you can customize environment variables by adding parameter: -Dvariable=value
// java -jar -DSERVER_MAX_THREADS=20 -DGITHUB_API_TOKEN=personal_token

java -jar server.jar
```

## Test application

When the server is up you should be able to access API:

* ```/repos/{username}``` - get list of user repositories (username is required)
* ```/repos/{username}/stars``` - get stars from all user repositories (username is required)
* ```/repos/{username}/favourite-languages``` - get the most popular user programming languages (username is required)

Sample api calls (using curl):

```
curl http://localhost:8080/repos/allegro

curl http://localhost:8080/repos/allegro/stars

curl http://localhost:8080/repos/allegro/favourite-languages
```

## Additional task - implementation proposal

Description: List the most popular user programming languages (name and bytes count)

### Update (30.12.2021)

Implementation provided! :fire:

Github provides api for that purpose ```https://api.github.com/repos/{username}/{repository}/languages``` which returns map of languages and number of bytes written in that language.
After fetching repositories, I'd take set of the most popular languages (based on language field of repository response) 
and take bytes count of that languages from each repository (one request per repository).
Next I'd group data by language name and sum number of bytes.


## Ideas for the feature improvements
* Pagination of user repositories
* Sorting user repositories by specified criteria (name, stars count)
* Cache requests (or save somewhere e.g. in database) to github api (github impose limit on api calls)
* Endpoints documentation (swagger)
* Api calls stats - daily api calls count with http result (how much failed, how much succeeds)

## Known issues
[03.01.2022] In some jdk implementations (e.g. adopt openjdk 11) application failed to start. 
It is caused by regex engine implementation (different look behind implementations across jdk versions). 
Regex ```(?<=<.{0,}[&?]page=)\\d+(?=[^>]*>[^>]*rel=\"last\")``` throws exception when application is starting. 
Temporary fix (in case when described error occurred): replace existing regex in ```src/main/java/siekiera/arkadiusz/githubrepositories/services/impl/GithubApiUrlService.java``` 
file with ```(?<=<.*[&?]page=)\\d+(?=[^>]*>[^>]*rel=\"last\")```. 
Fix will be available soon!