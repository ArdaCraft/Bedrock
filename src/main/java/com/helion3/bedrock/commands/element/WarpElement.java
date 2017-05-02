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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

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
        String joined = join(args);

        List<String> all = getMatches(joined);
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
        String joined = safeJoin(args);
        if (!joined.isEmpty()) {
            return getMatches(joined);
        }
        return Collections.emptyList();
    }

    private String safeJoin(CommandArgs args) {
        StringBuilder builder = new StringBuilder();

        do {
            Optional<String> next = args.nextIfPresent();
            if (next.isPresent()) {
                if (builder.length() > 0) {
                    builder.append(' ');
                }
                builder.append(next.get());
            }
        } while (args.hasNext());

        return builder.toString();
    }

    private String join(CommandArgs args) throws ArgumentParseException {
        StringBuilder builder = new StringBuilder(args.next());
        while (args.hasNext()) {
            builder.append(' ').append(args.next());
        }
        return builder.toString();
    }

    private List<String> getMatches(String input) {
        List<String> all = Bedrock.getWarpManager().listWarps();
        return SearchUtil.search(input, all);
    }

    public static CommandElement of(String key) {
        return new WarpElement(Text.of(key));
    }
}
