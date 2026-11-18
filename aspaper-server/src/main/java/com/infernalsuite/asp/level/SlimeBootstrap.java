package com.infernalsuite.asp.level;

import com.infernalsuite.asp.api.world.SlimeWorld;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public record SlimeBootstrap(
        SlimeWorld initial
) {

    /**
     * Generates a stable world UUID from the slime world name, so the same
     * slime world keeps its UUID across server restarts and reloads.
     */
    public UUID generateId() {
        return UUID.nameUUIDFromBytes(("SlimeWorld" + initial.getName()).getBytes(StandardCharsets.UTF_8));
    }
}
