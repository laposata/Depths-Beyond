package com.dreamtea.depths_beyond.save;

import com.dreamtea.depths_beyond.data.PlayerPreGameState;
import com.dreamtea.depths_beyond.effects.SpellQueue;
import com.dreamtea.depths_beyond.effects.on_going.OnGoingEffect;
import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.resources.Identifier;

import java.util.List;

import static com.dreamtea.depths_beyond.DepthsBeyondMod.ofDB;

public class DungeonRunAttachments {
    public static <T> AttachmentType<T> viaCodec(String name, Codec<T> codec){
        return AttachmentRegistry.create(
                ofDB(name), // The ID of your Attachment
                builder -> builder
                        .persistent(codec)
        );
    }
    public static AttachmentType<List<Identifier>> createIdListAttachment(String name){
        return AttachmentRegistry.create(
                ofDB(name), // The ID of your Attachment
                builder -> builder
                        .persistent(Identifier.CODEC.listOf())
                        .initializer(List::of)
        );
    }
    public static AttachmentType<Float> createStatAttachment(String name, float defaultValue){
        return AttachmentRegistry.create(
                ofDB(name), // The ID of your Attachment
                builder -> builder
                        .persistent(Codec.FLOAT)
                        .initializer(() -> defaultValue)
        );
    }
    public static AttachmentType<Boolean> createBooleanAttachment(String name){
        return AttachmentRegistry.create(
                ofDB(name), // The ID of your Attachment
                builder -> builder
                        .persistent(Codec.BOOL)
        );
    }
    public static final AttachmentType<PlayerPreGameState> INIT_STATE_ATTACHMENT = AttachmentRegistry.create(
            ofDB("init_state"), // The ID of your Attachment
            builder -> builder
                    .persistent(PlayerPreGameState.CODEC)
                    .copyOnDeath()
    );
    public static final AttachmentType<Float> GREED = createStatAttachment("greed", 10f);
    public static final AttachmentType<Float> WIT = createStatAttachment("wit", 10f);
    public static final AttachmentType<Float> DECADENCE = createStatAttachment("decadence", 10f);
    public static final AttachmentType<Float> LUCK = createStatAttachment("luck", 0);
    public static final AttachmentType<Float> FOCUS = createStatAttachment("focus", 0);
    public static final AttachmentType<Float> FEAR = createStatAttachment("fear", 0);
    public static final AttachmentType<Boolean> STARTED = createBooleanAttachment("started");
    public static final AttachmentType<Boolean> GOAL = createBooleanAttachment("goal");
    public static final AttachmentType<List<Identifier>> STARTING_DECK = createIdListAttachment("starting_deck");
    public static final AttachmentType<List<Identifier>> CURRENT_DECK = createIdListAttachment("current_deck");
    public static final AttachmentType<List<Identifier>> DISCARD_DECK = createIdListAttachment("discard_deck");
    public static final AttachmentType<List<Identifier>> GENERATED_CARDS = createIdListAttachment("generated");
    public static final AttachmentType<List<OnGoingEffect>> ON_GOING_EFFECT = viaCodec("on_going", OnGoingEffect.CODEC.codec().listOf());
    public static final AttachmentType<List<SpellQueue.SavedTimedSpell>> SPELL_QUEUE = viaCodec("spells", SpellQueue.SavedTimedSpell.CODEC.codec().listOf());


}
