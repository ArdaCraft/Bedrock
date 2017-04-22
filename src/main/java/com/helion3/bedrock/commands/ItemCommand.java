package com.helion3.bedrock.commands;

import org.spongepowered.api.CatalogType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.trait.BlockTrait;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author dags <dags@dags.me>
 */
public class ItemCommand {

    public static CommandCallable getCommand() {
        return new CommandCallable() {
            @Override
            public CommandResult process(CommandSource source, String arguments) throws CommandException {
                try {
                    Optional<BlockState> state = ItemCommand.getState(arguments);
                    if (state.isPresent()) {
                        if (source instanceof Player) {
                            ItemStack stack = ItemStack.builder().fromBlockState(state.get()).build();
                            ((Player) source).getInventory().offer(stack);
                        } else {
                            source.sendMessage(Text.of(state.get(), TextColors.YELLOW));
                        }
                        return CommandResult.success();
                    }
                } catch (Exception e) {
                    throw new CommandException(Text.of(e.getLocalizedMessage()));
                }
                throw new CommandException(Text.of("Could not a match blockstate for: ", arguments));
            }

            @Override
            public List<String> getSuggestions(CommandSource source, String arguments, @Nullable Location<World> targetPosition) throws CommandException {
                return ItemCommand.getSuggestions(arguments);
            }

            @Override
            public boolean testPermission(CommandSource source) {
                return source.hasPermission("bedrock.item");
            }

            @Override
            public Optional<Text> getShortDescription(CommandSource source) {
                return Optional.of(Text.of("Get an ItemStack for a given BlockState"));
            }

            @Override
            public Optional<Text> getHelp(CommandSource source) {
                return Optional.of(getUsage(source));
            }

            @Override
            public Text getUsage(CommandSource source) {
                return Text.of("/item <block_type> <properties>...");
            }
        };
    }

    private static Optional<BlockState> getState(String input) {
        String[] args = input.split(" ");
        if (args.length == 0) {
            return Optional.empty();
        }

        Optional<BlockType> type = Sponge.getRegistry().getType(BlockType.class, args[0]);
        if (!type.isPresent()) {
            return Optional.empty();
        }

        BlockState.StateMatcher matcher = matcher(type.get(), args);
        return Sponge.getRegistry().getAllOf(BlockState.class).stream().filter(matcher::matches).findFirst();
    }

    private static List<String> getSuggestions(String input) {
        String[] args = input.split(" ");
        if (args.length == 0) {
            return Collections.emptyList();
        }

        // if input ends with a space, we want to tab-complete the next arg
        boolean lookAhead = input.endsWith(" ");

        if (args.length == 1 && !lookAhead) {
            return matchType(args[0]).map(CatalogType::getId).collect(Collectors.toList());
        }

        Optional<BlockType> type = Sponge.getRegistry().getType(BlockType.class, args[0]);
        if (!type.isPresent()) {
            return Collections.emptyList();
        }

        String last = lookAhead ? "" : args[args.length - 1];
        return matchTrait(type.get(), last).stream().map(Object::toString).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private static <T extends Comparable<T>> BlockState.StateMatcher matcher(BlockType type, String[] args) {
        BlockState.MatcherBuilder builder = BlockState.matcher(type);

        for (int i = 1; i < args.length; i++) {
            String choice = args[i];
            for (BlockTrait<?> trait : type.getTraits()) {
                for (Object value : trait.getPossibleValues()) {
                    String match = trait.getName() + "=" + value;
                    if (match.equalsIgnoreCase(choice)) {
                        BlockTrait<T> t = (BlockTrait<T>) trait;
                        T v = (T) value;
                        builder.trait(t, v);
                    }
                }
            }
        }

        return builder.build();
    }

    private static Stream<BlockType> matchType(String input) {
        Pattern pattern;

        if (input.matches("^(.*?):(.*?)")) {
            pattern = Pattern.compile("^" + Pattern.quote(input), Pattern.CASE_INSENSITIVE);
        } else {
            pattern = Pattern.compile("^(.*?):" + Pattern.quote(input), Pattern.CASE_INSENSITIVE);
        }

        return Sponge.getRegistry().getAllOf(BlockType.class).stream()
                .filter(type -> pattern.matcher(type.getId()).find());
    }

    private static List<Object> matchTrait(BlockType type, String input) {
        Pattern pattern = Pattern.compile("^" + Pattern.quote(input), Pattern.CASE_INSENSITIVE);
        boolean explicit = input.matches("^(.*?)=(.*?)");

        List<Object> results = new ArrayList<>();

        for (BlockTrait trait : type.getTraits()) {
            for (Object val : trait.getPossibleValues()) {
                String match = trait.getName() + "=" + val;
                if ((!explicit && pattern.matcher(val.toString()).find()) || pattern.matcher(match).find()) {
                    results.add(match);
                }
            }
        }

        return results;
    }
}
