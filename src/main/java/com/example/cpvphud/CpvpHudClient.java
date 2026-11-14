package com.example.cpvphud;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

public class CpvpHudClient implements ClientModInitializer {

	private static final int WHITE = 0xFFFFFFFF;
	private static final int RED = 0xFFFF5555;
	private static final int YELLOW = 0xFFFFFF55;
	private static final int GREEN = 0xFF55FF55;

	@Override
	public void onInitializeClient() {
		HudRenderCallback.EVENT.register(this::render);
	}

	private void render(DrawContext ctx, net.minecraft.client.render.RenderTickCounter tick) {
		MinecraftClient mc = MinecraftClient.getInstance();
		if (mc.player == null || mc.options.hudHidden || mc.getDebugHud().shouldShowDebugHud()) return;

		int x = 6;
		int y = 6;
		int line = 11;

		y = row(ctx, mc, "Crystals", count(mc, Items.END_CRYSTAL), x, y, line, 16);
		y = row(ctx, mc, "Obsidian", count(mc, Items.OBSIDIAN), x, y, line, 16);
		y = row(ctx, mc, "Totems", count(mc, Items.TOTEM_OF_UNDYING), x, y, line, 2);
		y = row(ctx, mc, "Gapples", count(mc, Items.ENCHANTED_GOLDEN_APPLE), x, y, line, 4);

		int ping = ping(mc);
		int pingColor = ping < 0 ? WHITE : ping < 80 ? GREEN : ping < 160 ? YELLOW : RED;
		ctx.drawTextWithShadow(mc.textRenderer, "Ping: " + (ping < 0 ? "-" : ping + "ms"), x, y, pingColor);
		y += line;
		ctx.drawTextWithShadow(mc.textRenderer, "FPS: " + mc.getCurrentFps(), x, y, WHITE);
	}

	// Colors the number red when you're running low (below 'low').
	private int row(DrawContext ctx, MinecraftClient mc, String label, int n, int x, int y, int line, int low) {
		int color = n == 0 ? RED : n < low ? YELLOW : GREEN;
		ctx.drawTextWithShadow(mc.textRenderer, label + ": " + n, x, y, color);
		return y + line;
	}

	private int count(MinecraftClient mc, Item item) {
		PlayerInventory inv = mc.player.getInventory();
		int total = 0;
		for (int i = 0; i < inv.size(); i++) {
			var stack = inv.getStack(i);
			if (stack.isOf(item)) total += stack.getCount();
		}
		return total;
	}

	private int ping(MinecraftClient mc) {
		if (mc.getNetworkHandler() == null) return -1;
		PlayerListEntry e = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
		return e == null ? -1 : e.getLatency();
	}
                  }
