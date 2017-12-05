package com.helion3.bedrock.commands.element;

import com.helion3.bedrock.Bedrock;
import com.helion3.bedrock.util.SearchUtil;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.World;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author dags <dags@dags.me>
 */
public class WarpElement extends CommandElement {

    private WarpElement(@Nullable Text key) {
        super(key);
    }

    @Nullable
    @Override
    protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
        String joined = args.getRaw();
        goToEnd(args);

        List<String> all = getMatches(joined).collect(Collectors.toList());
        if (all.size() == 1) {
            String name = all.get(0);
            Optional<Transform<World>> warp = Bedrock.getWarpManager().getWarp2(all.get(0));
            if (warp.isPresent()) {
                return Tuple.of(name, warp.get());
            }
        }

        if (all.size() > 1) {
            List<Tuple<String, Transform<World>>> transforms = new LinkedList<>();
            for (String name : all) {
                Optional<Transform<World>> warp = Bedrock.getWarpManager().getWarp2(name);
                if (warp.isPresent()) {
                    transforms.add(Tuple.of(name, warp.get()));
                }
            }
            return transforms;
        }

        throw args.createError(Text.of("No warp found for input '", joined, "'"));
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
        String input = args.getRaw().toLowerCase();
        int start = input.lastIndexOf(' ') + 1;

        List<String> warps = Bedrock.getWarpManager().listWarps();
        List<String> matches = new ArrayList<>(warps.size());
        for (String s : warps) {
            if (s.toLowerCase().startsWith(input)) {
                matches.add(s.substring(start));
            }
        }

        List<String> completions = new LinkedList<>();
        Set<String> unique = new HashSet<>();
        for (int i = 0; i < matches.size(); i++) {
            String s0 = matches.get(i);
            String shortest = s0;

            for (int j = 0; j < matches.size(); j++) {
                if (i == j) {
                    continue;
                }

                String s1 = matches.get(j);
                int index = commonIndex(s0, s1);
                int end = s0.lastIndexOf(' ', index);
                if (end < 0) {
                    end = s0.indexOf(' ', end);
                    end = end == -1 ? s0.length() : end;
                }

                if (end > 0 && end < shortest.length()) {
                    shortest = s0.substring(0, end);
                }
            }

            if (unique.add(shortest)) {
                completions.add(shortest);
            }
        }

        return completions;
    }

    private static int commonIndex(String s0, String s1) {
        for (int i = 0; i < s0.length() && i < s1.length(); i++) {
            char c0 = Character.toLowerCase(s0.charAt(i));
            char c1 = Character.toLowerCase(s1.charAt(i));
            if (c0 != c1) {
                return i;
            }
        }
        return Math.min(s0.length(), s1.length());
    }

    private void goToEnd(CommandArgs args) {
        try {
            while (args.hasNext()) {
                args.next();
            }
        } catch (ArgumentParseException ignored) {
        }
    }

    private Stream<String> getMatches(String input) {
        List<String> all = Bedrock.getWarpManager().listWarps();
        return SearchUtil.search(input, all);
    }

    public static CommandElement of(String key) {
        return new WarpElement(Text.of(key));
    }
}
