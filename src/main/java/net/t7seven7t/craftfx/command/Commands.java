package net.t7seven7t.craftfx.command;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.MapMaker;

import com.sk89q.intake.CommandCallable;
import com.sk89q.intake.CommandException;
import com.sk89q.intake.CommandMapping;
import com.sk89q.intake.Intake;
import com.sk89q.intake.InvalidUsageException;
import com.sk89q.intake.InvocationCommandException;
import com.sk89q.intake.argument.ArgumentException;
import com.sk89q.intake.argument.Namespace;
import com.sk89q.intake.completion.CommandCompleter;
import com.sk89q.intake.dispatcher.Dispatcher;
import com.sk89q.intake.fluent.CommandGraph;
import com.sk89q.intake.parametric.ArgumentParser;
import com.sk89q.intake.parametric.Binding;
import com.sk89q.intake.parametric.Injector;
import com.sk89q.intake.parametric.ParametricBuilder;
import com.sk89q.intake.parametric.provider.PrimitivesModule;
import com.sk89q.intake.util.auth.AuthorizationException;

import net.t7seven7t.craftfx.CraftFX;
import net.t7seven7t.craftfx.util.MessageUtil;
import net.t7seven7t.util.intake.module.BukkitModule;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class Commands implements TabExecutor, CommandCompleter {

    private static final Joiner SPACE_JOINER = Joiner.on(" ");
    private final Dispatcher dispatcher;
    private final CommandGraph graph;
    private final Map<CommandMapping, List<Binding<?>>> bindingMap = new MapMaker().makeMap();

    public Commands() {
        Injector injector = Intake.createInjector();
        injector.install(new PrimitivesModule());
        injector.install(new BukkitModule());
        injector.install(new CraftFXModule());

        ParametricBuilder builder = new ParametricBuilder(injector);
        builder.setAuthorizer((namespace, permission) -> {
            CommandSender sender = namespace.get(CommandSender.class);
            return sender.isOp() || sender.hasPermission(permission);
        });

        graph = new CommandGraph().builder(builder);
        dispatcher = graph.getDispatcher();

        registerSubCommands("fx", new AdminCommands());
        fixCommandCompletion(dispatcher.getCommands());
    }

    private void registerCommands(Object o) {
        graph.commands().registerMethods(o);
    }

    private void registerSubCommands(String group, Object o) {
        graph.commands().group(group).registerMethods(o);
    }

    private void fixCommandCompletion(Collection<CommandMapping> commandMappings) {
        for (CommandMapping commandMapping : commandMappings) {
            final CommandCallable callable = commandMapping.getCallable();
            if (callable instanceof Dispatcher) {
                fixCommandCompletion(((Dispatcher) callable).getCommands());
            } else if (callable.getClass().getSimpleName().equals("MethodCallable")) {
                try {
                    Field field = callable.getClass().getSuperclass().getDeclaredField("builder");
                    field.setAccessible(true);
                    final ParametricBuilder builder = (ParametricBuilder) field.get(callable);
                    field = callable.getClass().getSuperclass().getDeclaredField("parser");
                    field.setAccessible(true);
                    final ArgumentParser parser = (ArgumentParser) field.get(callable);
                    builder.setDefaultCompleter(this);
                    field = parser.getClass().getDeclaredField("parameters");
                    field.setAccessible(true);
                    final List parameterList = (List) field.get(parser);
                    if (parameterList.isEmpty()) continue;
                    final Method bindingsMethod = parameterList.get(0).getClass()
                            .getMethod("getBinding");
                    bindingsMethod.setAccessible(true);
                    final List<Binding<?>> bindings = new ArrayList<>();
                    for (Object o : parameterList) {
                        final Binding b = (Binding) bindingsMethod.invoke(o);
                        if (!b.getProvider().isProvided()) bindings.add(b);
                    }
                    bindingMap.put(commandMapping, bindings);
                } catch (Exception e) {
                    CraftFX.log().severe("Error occurred while fixing tab completion for " +
                                    "command %s",
                            MessageUtil.translate(null, callable.getDescription().getUsage()), e);
                }
            }
        }
    }

    public Dispatcher getDispatcher() {
        return dispatcher;
    }

    public CommandMapping getCommand(String alias) {
        String[] arguments = alias.split(" ");
        Dispatcher d = dispatcher;
        CommandMapping result = null;
        for (String s : arguments) {
            result = d.get(s);
            if (result != null && result.getCallable() instanceof Dispatcher) {
                d = (Dispatcher) result.getCallable();
                continue;
            }
            break; // not a dispatcher
        }
        return result;
    }

    public void printHelp(CommandSender sender, CommandMapping mapping) {
        printHelp(sender, mapping, ImmutableList.of());
    }

    void printHelp(CommandSender sender, CommandMapping mapping, List<String> parentAliases) {
        if (mapping.getCallable() instanceof Dispatcher) {
            List<String> l = ImmutableList.<String>builder().addAll(parentAliases)
                    .add(mapping.getPrimaryAlias()).build();
            ((Dispatcher) mapping.getCallable()).getCommands()
                    .forEach(c -> printHelp(sender, c, l));
            return;
        }
        Namespace n = new Namespace();
        n.put(CommandSender.class, sender);
        if (!mapping.getCallable().testPermission(n)) return;
        String parent = "";
        for (String p : parentAliases) parent += p + " ";
        MessageUtil.message(sender, "command-help", parent,
                mapping.getPrimaryAlias(),
                MessageUtil.translate(sender, mapping.getDescription().getUsage()),
                MessageUtil.translate(sender, mapping.getDescription().getShortDescription()));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Namespace namespace = new Namespace();
        namespace.put(CommandSender.class, sender);

        try {
            dispatcher.call(joinCommandArgs(command, args), namespace, ImmutableList.of());
        } catch (CommandException e) {
            if (e instanceof InvalidUsageException && e.getMessage()
                    .equals("Please choose a sub-command.")) {
                String alias = SPACE_JOINER.join(((InvalidUsageException) e).getAliasStack());
                MessageUtil.message(sender, "command-help-header", alias);
                printHelp(sender, getCommand(alias));
                return true;
            }
            MessageUtil.message(sender, "&c" + e.getMessage());
            final CommandMapping mapping = getCommand(joinCommandArgs(command, args));
            if (mapping != null) {
                MessageUtil.message(sender, "&cHelp: %s", MessageUtil.translate(sender,
                        // show desc if help is null
                        mapping.getDescription().getHelp() == null ? mapping.getDescription()
                                .getShortDescription() : mapping.getDescription().getHelp()));
            }
        } catch (AuthorizationException e) {
            MessageUtil.message(sender, "command-authorization-exception");
        } catch (InvocationCommandException e) {
            final Throwable cause = getCause(e);
            if (cause instanceof NumberFormatException ||
                    cause instanceof ArgumentException) {
                MessageUtil.message(sender, "&c" + cause.getMessage());
            } else if (cause instanceof InvalidUsageException && cause.getMessage()
                    .equals("Please choose a sub-command.")) {
                final String alias = SPACE_JOINER
                        .join(((InvalidUsageException) cause).getAliasStack());
                MessageUtil.message(sender, "command-help-header", alias);
                printHelp(sender, getCommand(alias));
            } else {
                MessageUtil.message(sender, "&cAn error occurred. Tell an admin.");
                cause.printStackTrace();
            }
        }

        return true;
    }

    private Throwable getCause(InvocationCommandException e) {
        if (e.getCause() instanceof InvocationCommandException) {
            return getCause((InvocationCommandException) e.getCause());
        }

        return e.getCause();
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias,
                                      String[] args) {
        Namespace namespace = new Namespace();
        namespace.put(CommandSender.class, sender);

        if (dispatcher.testPermission(namespace)) {
            final String joinedArgs = joinCommandArgs(command, args);
            final CommandMapping commandMapping = getCommand(joinedArgs);
            namespace.put(CommandMapping.class, commandMapping);
            try {
                // todo: fix tab completion in intake or replace with something else
                return dispatcher.getSuggestions(joinedArgs, namespace);
            } catch (CommandException e) {
                // o:
            }
        }

        return ImmutableList.of();
    }

    private String joinCommandArgs(Command command, String[] args) {
        return command.getName() + " " + SPACE_JOINER.join(args);
    }

    @Override
    public List<String> getSuggestions(String arguments, Namespace locals) throws CommandException {
        final CommandMapping mapping = locals.get(CommandMapping.class);
        final List<Binding<?>> bindings = bindingMap.get(mapping);
        if (bindings == null || bindings.isEmpty()) return ImmutableList.of();
        final String[] args = arguments.split(" ");
        if (args.length > bindings.size()) return ImmutableList.of();
        return bindings.get(args.length - 1).getProvider().getSuggestions(args[args.length - 1]);
    }
}
