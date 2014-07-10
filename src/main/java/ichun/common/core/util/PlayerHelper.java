package ichun.common.core.util;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

import com.mojang.authlib.GameProfile;

public class PlayerHelper {
	public static boolean checkOp(UUID uid) {
		GameProfile[] agameprofile = MinecraftServer.getServer().func_152357_F();
		int i = agameprofile.length;

		for (int j = 0; j < i; j++) {
			GameProfile gameprofile = agameprofile[j];

			if (MinecraftServer.getServer().getConfigurationManager().func_152596_g(gameprofile) && gameprofile.getId().equals(uid)) {
				return true;
			}
		}
		return false;
	}
	
	public static EntityPlayer getPlayerFromUsername(String username) {
		for (Object ExistingPlayer : MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
			if (ExistingPlayer instanceof EntityPlayer) {
				if (((EntityPlayer) ExistingPlayer).getCommandSenderName().startsWith(username)) {
					return (EntityPlayer) ExistingPlayer;
				}
			}
		}
		return null;
	}
}