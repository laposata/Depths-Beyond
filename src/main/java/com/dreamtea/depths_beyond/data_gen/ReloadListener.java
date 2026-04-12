package com.dreamtea.depths_beyond.data_gen;

import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.reloader.SimpleReloadListener;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.StrictJsonParser;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static com.dreamtea.depths_beyond.DepthsBeyondMod.LOGGER;

public class ReloadListener<T, R> extends SimpleReloadListener<R> {

    private final String resourceFile;
    private final Codec<T> codec;
    private final Function<Map<Identifier, T>, R> generateOutput;

    public ReloadListener(String resourceFile, Codec<T> codec, Function<Map<Identifier, T>, R> generateOutput) {
        this.resourceFile = resourceFile;
        this.codec = codec;
        this.generateOutput = generateOutput;
    }

    @Override
    protected R prepare(SharedState state) {
        Map<Identifier, T> items = new HashMap<>();
        HolderLookup.Provider registries = state.get(ResourceLoader.REGISTRY_LOOKUP_KEY);
        var ops = registries.createSerializationContext(JsonOps.INSTANCE);
        Map<Identifier, Resource> cardResources = state.resourceManager().listResources(resourceFile, (id) -> id.getPath().endsWith(".json"));
        for (Map.Entry<Identifier, Resource> entry : cardResources.entrySet()) {
            Identifier location = entry.getKey();
            Identifier id = cutId(location);
            try {
                Reader reader = (entry.getValue()).openAsReader();
                try {
                    codec.parse(ops, StrictJsonParser.parse(reader)).ifSuccess(parsed -> {
                        if (items.putIfAbsent(location, parsed) != null) {
                            throw new IllegalStateException("Duplicate data file ignored with ID " + id);
                        }
                        LOGGER.info("Parsed and stored: {}", id);
                    }).ifError(error -> LOGGER.error("Couldn't parse data file '{}' from '{}': {}", id, location, error));
                } catch (Throwable var13 ) {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (Throwable var12) {
                            var13.addSuppressed(var12);
                        }
                    }
                    LOGGER.error(var13);
                    throw var13;
                }

                if (reader != null) {
                    reader.close();
                }
            } catch (IllegalArgumentException | IOException | JsonParseException var14) {
                LOGGER.error("Couldn't parse data file '{}' from '{}'", id, location, var14);
            } catch (Exception e){
                LOGGER.error("Other Error: {}, {}", id, entry.getValue().source(),  e );
            }
        }
        return generateOutput.apply(items);
    }

    @Override
    protected void apply(R prepared, SharedState state) {

    }

    private static Identifier cutId(Identifier id){
        String namespace = id.getNamespace();
        String path = id.getPath();
        if(path.contains("/")){
            path = path.substring(path.lastIndexOf("/") + 1);
        }
        if(path.contains(".")){
            path = path.substring(0, path.indexOf("."));
        }
        return Identifier.fromNamespaceAndPath(namespace, path);
    }
}
