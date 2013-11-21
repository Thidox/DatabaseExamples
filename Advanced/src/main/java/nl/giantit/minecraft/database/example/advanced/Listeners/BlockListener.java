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
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class BlockListener implements Listener {

	private dbExample plugin;
	private Driver db;
	
	public BlockListener(dbExample plugin) {
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
	
	private void updateState(BlockBreakEvent e) {
		Player p = e.getPlayer();
		
		if(!this.checkPlayerExists(p)) {
			// We are going to have to insert!
			ArrayList<String> fields = new ArrayList<String>();
			fields.add("player");
			fields.add("blocksBroken");
			
			HashMap<Integer, HashMap<String, String>> values = new HashMap<Integer, HashMap<String, String>>();
			for(int i = 0; i < fields.size(); i++) {
				HashMap<String, String> value = new HashMap<String, String>();
				String field = fields.get(i);
				if(field.equalsIgnoreCase("player")) {
					value.put("data", p.getName());
				}else if(field.equalsIgnoreCase("blocksBroken")) {
					value.put("kind", "INT");
					value.put("data", "1");
				}
				
				values.put(i, value);
			}
			
			InsertQuery iQ = db.insert("#__playerData");
			iQ.addFields(fields);
			iQ.addRow();
			iQ.assignValue("player", p.getName());
			iQ.assignValue("blocksBroken", "1", InsertQuery.ValueType.RAW);
			
			iQ.exec();
		}else{
			// We can update!
			UpdateQuery uQ = db.update("#__playerData");
			uQ.set("blocksBroken", "blocksBroken + 1", UpdateQuery.ValueType.SETRAW); // Make the database increment the current value of column blocksBroken by 1
			
			uQ.exec();
		}
	}

	// MONITOR because we are only monitoring the event.
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockBreak(final BlockBreakEvent e) {
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
