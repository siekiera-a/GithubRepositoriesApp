package siekiera.arkadiusz.githubrepositories.models;

import lombok.Value;

@Value
public class Pair<K,V> {

    K key;
    V value;

}
