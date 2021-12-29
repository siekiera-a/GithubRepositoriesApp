package siekiera.arkadiusz.githubrepositories.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class Repository {

    String name;
    long stars;

    @JsonCreator
    public Repository(@JsonProperty(value = "name", required = true) String name,
                      @JsonProperty(value = "stargazers_count", required = true) long stars) {
        this.name = name;
        this.stars = stars;
    }

    @JsonGetter("stars")
    public long getStars() {
        return stars;
    }
}
