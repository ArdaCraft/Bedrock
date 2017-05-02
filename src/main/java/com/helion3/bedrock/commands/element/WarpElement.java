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
        String joined = args.getRaw();
        goToEnd(args);

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
        String joined = args.getRaw();
        goToEnd(args);

        if (!joined.isEmpty()) {
            List<String> matches = getMatches(joined);
            matches.remove(joined);
            return matches;
        }

        return Collections.emptyList();
    }

    private void goToEnd(CommandArgs args) {
        try {
            while (args.hasNext()) {
                args.next();
            }
        } catch (ArgumentParseException ignored) {
        }
    }

    private List<String> getMatches(String input) {
        List<String> all = Bedrock.getWarpManager().listWarps();
        return SearchUtil.search(input, all);
    }

    public static CommandElement of(String key) {
        return new WarpElement(Text.of(key));
    }
}
