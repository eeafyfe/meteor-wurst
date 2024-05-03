package meteordevelopment.meteorclient.commands.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.command.CommandSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static meteordevelopment.meteorclient.MeteorClient.mc;

public final class InvSee extends Command implements RenderListener
{
	private String targetName;
	
	public InvSee()
	{
		super("invsee",
			"Allows you to see parts of another player's inventory.",
			".invsee <player>");
	}
	
	@Override
	public void call(String[] args) throws CmdException
	{
		if(args.length != 1)
			throw new CmdSyntaxError();
		
		if(MC.player.getAbilities().creativeMode)
		{
			ChatUtils.error("Survival mode only.");
			return;
		}
		
		targetName = args[0];
		EVENTS.add(RenderListener.class, this);
	}
	
	@Override
	public void onRender(MatrixStack matrixStack, float partialTicks)
	{
		boolean found = false;
		
		for(Entity entity : MC.world.getEntities())
		{
			if(!(entity instanceof OtherClientPlayerEntity))
				continue;
			
			OtherClientPlayerEntity player = (OtherClientPlayerEntity)entity;
			
			String otherPlayerName = player.getName().getString();
			if(!otherPlayerName.equalsIgnoreCase(targetName))
				continue;
			
			ChatUtils.message("Showing inventory of " + otherPlayerName + ".");
			MC.setScreen(new InventoryScreen(player));
			found = true;
			break;
		}
		
		if(!found)
			ChatUtils.error("Player not found.");
		
		targetName = null;
		EVENTS.remove(RenderListener.class, this);
	}
}
