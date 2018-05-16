package com.helion3.bedrock.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

/**
 * @author dags <dags@dags.me>
 */
public class URLUtils {

    private static final String URL = "((ht|f)tp(s?):\\/\\/|www\\.)(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)";
    private static final String PROTOCOL = "((ht|f)tp(s?):\\/\\/|www\\.)*";
    private static final Pattern URL_PATTERN = Pattern.compile(URL, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
    private static final Pattern PROTOCOL_PATTERN = Pattern.compile(PROTOCOL, Pattern.CASE_INSENSITIVE);

    public static Text replaceURLs(String input) {
        StringBuilder sb = new StringBuilder(input);
        Text.Builder builder = Text.builder();
        Matcher matcher = URL_PATTERN.matcher(input);
        int index = 0;
        while (matcher.find()) {
            String url = matcher.group().trim();
            builder.append(Text.of(sb.subSequence(index, index = sb.indexOf(url))));
            try {
                String domain = domain(url);
                Text link = Text.builder(domain)
                        .onClick(TextActions.openUrl(new URL(url.matches("^https?://.*$") ? url : "http://" + url)))
                        .onHover(TextActions.showText(Text.builder(url).color(TextColors.BLUE).build()))
                        .style(TextStyles.UNDERLINE)
                        .build();

                builder.append(link);
            } catch (MalformedURLException e) {
                builder.append(Text.of(url));
            }
            index += url.length();
        }
        if (index < sb.length()) {
            builder.append(Text.of(sb.subSequence(index, sb.length())));
        }
        return builder.build();
    }

    private static String domain(String url) {
        Matcher matcher = PROTOCOL_PATTERN.matcher(url);
        int start = matcher.find() ? matcher.end() : 0, end = url.length();
        for (int i = start; i < url.length(); i++) {
            char c = url.charAt(i);
            if (c == '/' || c == '?') {
                end = i;
                break;
            }
        }
        return url.substring(start, end);
    }
}
