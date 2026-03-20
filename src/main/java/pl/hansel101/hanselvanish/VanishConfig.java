package pl.hansel101.hanselvanish;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class VanishConfig {
    public static final BuilderCodec<VanishConfig> CODEC = BuilderCodec
            .builder(VanishConfig.class, VanishConfig::new)
            .append(new KeyedCodec<>("EnableFakeJoinAndLeaveMessages", Codec.BOOLEAN),
                    (c, v) -> c.fakeJoinAndLeaveMessagesEnabled = v,
                    (c) -> c.fakeJoinAndLeaveMessagesEnabled).add()
            .append(new KeyedCodec<>("UseCustomFakeMessages", Codec.BOOLEAN),
                    (c, v) -> c.useCustomFakeMessages = v,
                    (c) -> c.useCustomFakeMessages).add()
            .append(new KeyedCodec<>("CustomFakeJoinMessage", Codec.STRING),
                    (c, v) -> c.customFakeJoinMessage = v,
                    (c) -> c.customFakeJoinMessage).add()
            .append(new KeyedCodec<>("CustomFakeLeaveMessage", Codec.STRING),
                    (c, v) -> c.customFakeLeaveMessage = v,
                    (c) -> c.customFakeLeaveMessage).add()
            .build();

    private boolean fakeJoinAndLeaveMessagesEnabled = false;
    private boolean useCustomFakeMessages = false;
    private String customFakeJoinMessage = "{username} has joined {world}";
    private String customFakeLeaveMessage = "{username} has left {world}";

    public VanishConfig() {
    }

    public boolean isFakeJoinAndLeaveMessagesEnabled() {
        return fakeJoinAndLeaveMessagesEnabled;
    }

    public boolean useCustomFakeMessages() {
        return useCustomFakeMessages;
    }

    public String getCustomFakeJoinMessage() {
        return customFakeJoinMessage;
    }

    public String getCustomFakeLeaveMessage() {
        return customFakeLeaveMessage;
    }
}
