package com.instagramy.utils;

import com.instagramy.models.Favorite;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HashSets {
    public static Set<String> convertToLiteWeigtSet(List<Favorite> favoriteList) {
        HashSet<String> result = new HashSet<>();
        for (Favorite favorite : favoriteList) {
            result.add(favorite.getPostId());
        }
        return result;
    }
}
