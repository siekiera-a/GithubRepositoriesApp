# Table of Contents

* [Description](#description)
* [Installation](#installation)
  * [First solution - docker (recommended)](#first-solution---docker-recommended)
  * [Second solution - local build](#second-solution---local-build)
* [Test application](#test-application)    
* [Ideas for the feature improvements](#ideas-for-the-feature-improvements)

## Description

Api allows you to:
* list user repositories (name and stars amount)
* return sum of stars in all user repositories

for any github user.

## Installation

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
##### Template variables:

port - port on local machine

### Second solution - local build

#### Requirements
* JDK - version 11 or above

Environment variables can be also changed in *application.properties* file (before project is compiled).

Compile project with command below (specified for your operating system):

```
// windows
mvnw.cmd clean install

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

Sample api calls (using curl):

```
curl http://localhost:8080/repos/allegro

curl http://localhost:8080/repos/allegro/stars
```

## Ideas for the feature improvements
* Pagination of user repositories
* Sorting user repositories by specified criteria (name, stars count)
* Cache requests (or save somewhere e.g. in database) to github api (github impose limit on api calls)
* Endpoints documentation (swagger)
* Api calls stats - daily api calls count with http result (how much failed, how much succeeds)