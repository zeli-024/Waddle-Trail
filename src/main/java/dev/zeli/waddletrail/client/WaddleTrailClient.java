package dev.zeli.waddletrail.client;

import dev.zeli.waddletrail.WaddleTrail;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

/** Client-only entry point. Xaero rendering hooks and local map access belong here. */
@Mod(value = WaddleTrail.MOD_ID, dist = Dist.CLIENT)
public final class WaddleTrailClient {
    public WaddleTrailClient() {
        WaddleTrail.LOGGER.info("Waddle Trail client services are ready");
    }
}
