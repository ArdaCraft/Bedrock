package com.helion3.bedrock.util;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dags <dags@dags.me>
 */
public class SearchUtil {

    public static List<String> search(String query, Collection<String> data) {
        String search = query.toLowerCase();
        String noSpace = search.endsWith(" ") ? search : search.replaceAll("[^\\S]+", "");

        List<Result<String>> results = new LinkedList<>();
        for (String value : data) {
            if (value.equals(search)) {
                return Collections.singletonList(value);
            } else if (value.startsWith(search)) {
                results.add(new Result<>(value, 0, value.length() - search.length()));
            } else if (value.contains(search)) {
                results.add(new Result<>(value, 1, value.indexOf(search)));
            } else if (value.startsWith(noSpace)) {
                results.add(new Result<>(value, 2, value.length() - noSpace.length()));
            } else if (value.contains(noSpace)) {
                results.add(new Result<>(value, 3, value.indexOf(noSpace)));
            }
        }

        return results.stream().distinct().sorted().map(Result::getValue).collect(Collectors.toList());
    }

    private static class Result<T> implements Comparable<Result<T>> {

        private final T value;
        private final int type;
        private final int score;

        private Result(T value, int type, int score) {
            this.value = value;
            this.type = type;
            this.score = score;
        }

        private T getValue() {
            return value;
        }

        @Override
        public int compareTo(Result<T> other) {
            return other.type == this.type ? this.score - other.score : this.type - other.type;
        }

        @Override
        public boolean equals(Object other) {
            return other != null && other.getClass() == this.getClass() && ((Result) other).value.equals(this.value);
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }
    }
}
