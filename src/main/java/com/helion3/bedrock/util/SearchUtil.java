package com.helion3.bedrock.util;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author dags <dags@dags.me>
 */
public class SearchUtil {

    public static Stream<String> search(String query, Collection<String> list) {
        String search = query.toLowerCase();
        String noSpace = search.endsWith(" ") ? search : search.replaceAll("[^\\S]+", "");

        List<Result<String>> results = new LinkedList<>();
        for (String value : list) {
            String name = value.toLowerCase();

            if (name.equals(search)) {
                return Stream.of(value);
            }
            if (name.startsWith(search)) {
                results.add(new Result<>(value, 0, name.length() - search.length()));
                continue;
            }
            if (name.contains(search)) {
                results.add(new Result<>(value, 1, name.indexOf(search)));
                continue;
            }

            String noSpaceName = name.replaceAll("[^\\S]+", "");
            if (noSpaceName.startsWith(noSpace)) {
                results.add(new Result<>(value, 2, noSpaceName.length() - noSpace.length()));
                continue;
            }
            if (noSpaceName.contains(noSpace)) {
                results.add(new Result<>(value, 3, noSpaceName.indexOf(noSpace)));
            }
        }

        return results.stream().distinct().sorted().map(Result::getValue);
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
