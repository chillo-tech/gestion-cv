package tech.chillo.msgestioncv.profile;

import java.util.Set;


public record Profile(
        String firstName,
        String lastName,
        String title,
        String about,
        Set<Item> interests,
        Set<Item> languages,
        Set<Item> formations,
        Set<Item> skills,
        Set<Item> expertises,
        Set<Experience> experiences
) {
}
