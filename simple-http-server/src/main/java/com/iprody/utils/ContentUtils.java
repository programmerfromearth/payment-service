package com.iprody.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class ContentUtils {
    private static final String GET_METHOD = "GET";
    private static final String REGEXP_SPACE = "\\s+";

    public static boolean isValidFile(Path filePath) {
        return Files.exists(filePath) && Files.isRegularFile(filePath);
    }

    public static Optional<String> extractLocationAfterServerUrl(List<String> requestLines) {
        if (requestLines == null || requestLines.isEmpty()) {
            return Optional.empty();
        }

        final String firstLine = requestLines.getFirst();

        if (!firstLine.startsWith(GET_METHOD)) {
            return Optional.empty();
        }

        String[] parts = firstLine.split(REGEXP_SPACE);
        if (parts.length < 2) {
            return Optional.empty();
        }

        return Optional.ofNullable(parts[1]);
    }

    public static String defineContentType(Path filePath) throws IOException {
        String contentType = Files.probeContentType(filePath);

        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return contentType;
    }
}
