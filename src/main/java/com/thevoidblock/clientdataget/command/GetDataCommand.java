package com.thevoidblock.clientdataget.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.thevoidblock.clientdataget.ClientDataGetConfig;
import com.thevoidblock.clientdataget.TextFormatter;
import dev.xpple.clientarguments.arguments.CEntityArgument;
import dev.xpple.clientarguments.arguments.CEntitySelector;
import dev.xpple.clientarguments.arguments.CNbtPathArgument;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.Iterator;
import java.util.List;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class GetDataCommand {

    private static final SimpleCommandExceptionType GET_MULTIPLE_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.data.get.multiple"));

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess) -> dispatcher.register(
                    literal("getdata")
                            .then(argument("target", CEntityArgument.entity())
                                    .then(argument("path", CNbtPathArgument.nbtPath())
                                            .executes(GetDataCommand::executeGetPath)
                                    )
                                    .executes(GetDataCommand::executeGet)
                            )
        ));
    }

    private static NbtElement getNBTWithPath(CommandContext<FabricClientCommandSource> context, NbtCompound nbt) throws CommandSyntaxException {

        List<NbtElement> collection = CNbtPathArgument.getNbtPath(context, "path").get(nbt);

        Iterator<NbtElement> iterator = collection.iterator();
        NbtElement nbtElement = iterator.next();
        if (iterator.hasNext()) {
            throw GET_MULTIPLE_EXCEPTION.create();
        }
        return nbtElement;
    }

    private static int executeGet(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {

        Entity entity = context.getArgument("target", CEntitySelector.class).getEntity(context.getSource());

        NbtCompound nbt = new NbtCompound();
        entity.writeNbt(nbt);

        Text nbtText = TextFormatter.FormatNBTText(Text.literal(nbt.asString()));

        MutableText returnText = Text.literal(entity.getName().getString());
        returnText.append(String.format(" %s", ClientDataGetConfig.RETURN_TEXT));
        returnText.append(nbtText);

        context.getSource().sendFeedback(returnText);
        return 1;
    }

    private static int executeGetPath(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {

        Entity entity = context.getArgument("target", CEntitySelector.class).getEntity(context.getSource());

        NbtCompound nbt = new NbtCompound();
        entity.writeNbt(nbt);

        NbtElement nbtElement = getNBTWithPath(context, nbt);

        Text nbtText = TextFormatter.FormatNBTText(Text.literal(nbtElement.asString()));

        MutableText returnText = Text.literal(entity.getName().getString());
        returnText.append(String.format(" %s", ClientDataGetConfig.RETURN_TEXT));
        returnText.append(nbtText);

        context.getSource().sendFeedback(returnText);
        return 1;
    }
}
