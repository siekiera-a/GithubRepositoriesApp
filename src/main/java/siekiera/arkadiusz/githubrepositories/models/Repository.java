package siekiera.arkadiusz.githubrepositories.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class Repository {

    String name;
    long stars;
    String language;

    @JsonCreator
    public Repository(@JsonProperty(value = "name", required = true) String name,
                      @JsonProperty(value = "stargazers_count", required = true) long stars,
                      @JsonProperty(value = "language", required = true) String language) {
        this.name = name;
        this.stars = stars;
        this.language = language;
    }

    @JsonGetter("stars")
    public long getStars() {
        return stars;
    }

    @JsonIgnore
    public String getLanguage() {
        return language;
    }
}
