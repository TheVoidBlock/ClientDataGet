package com.thevoidblock.clientdataget;

import com.thevoidblock.clientdataget.command.Commands;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientDataGet implements ClientModInitializer {

    public static final String MOD_ID = "clientdataget";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {

        Commands.registerCommands();

        LOGGER.info("{} initialized!", MOD_ID);

    }
}
