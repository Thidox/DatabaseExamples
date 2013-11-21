package nl.giantit.minecraft.database.example.advanced.Listeners;

import nl.giantit.minecraft.database.Driver;
import nl.giantit.minecraft.database.QueryResult;
import nl.giantit.minecraft.database.example.advanced.dbExample;
import nl.giantit.minecraft.database.query.InsertQuery;
import nl.giantit.minecraft.database.query.SelectQuery;
import nl.giantit.minecraft.database.query.UpdateQuery;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class PlayerListener implements Listener {

	private dbExample plugin;
	private Driver db;
	
	public PlayerListener(dbExample plugin) {
		this.plugin = plugin;
		this.db = plugin.getDb();
	}
	
	private Boolean checkPlayerExists(Player p) {
		SelectQuery sQ = db.select("id");
		sQ.from("#__playerData");
		sQ.where("player", p.getName());
		
		QueryResult resSet = sQ.exec();
		
		return resSet.size() == 1;
	}
	
	private void updateState(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		
		if(!this.checkPlayerExists(p)) {
			// We are going to have to insert!
			ArrayList<String> fields = new ArrayList<String>();
			fields.add("player");
			fields.add("timesClicked");
			
			InsertQuery iQ = db.insert("#__playerData");
			iQ.addFields(fields);
			iQ.addRow();
			iQ.assignValue("player", p.getName());
			iQ.assignValue("timesClicked", "1", InsertQuery.ValueType.RAW);
			
			iQ.exec();
		}else{
			// We can update!
			UpdateQuery uQ = db.update("#__playerData");
			uQ.set("timesClicked", "timesClicked + 1", UpdateQuery.ValueType.SETRAW); // Make the database increment the current value of column timesClicked by 1
			
			uQ.exec();
		}
	}
	
	private void updateState(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		
		if(!this.checkPlayerExists(p)) {
			// We are going to have to insert!
			ArrayList<String> fields = new ArrayList<String>();
			fields.add("player");
			fields.add("movesMade");
			
			InsertQuery iQ = db.insert("#__playerData");
			iQ.addFields(fields);
			iQ.addRow();
			iQ.assignValue("player", p.getName());
			iQ.assignValue("movesMade", "1", InsertQuery.ValueType.RAW);
			
			iQ.exec();
		}else{
			// We can update!
			UpdateQuery uQ = db.update("#__playerData");
			uQ.set("movesMade", "movesMade + 1", UpdateQuery.ValueType.SETRAW); // Make the database increment the current value of column movesMade by 1
			
			uQ.exec();
		}
	}

	// MONITOR because we are only monitoring the event.
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerInteract(final PlayerInteractEvent e) {
		// We don't want to work with a cancelled event!
		if(e.isCancelled())
			return;
		
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				updateState(e);
			}
		});
	}
	
	// MONITOR because we are only monitoring the event.
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerMove(final PlayerMoveEvent e) {
		// We don't want to work with a cancelled event!
		if(e.isCancelled())
			return;
		
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				updateState(e);
			}
		});
	}
}
