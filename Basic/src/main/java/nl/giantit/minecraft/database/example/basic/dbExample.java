package nl.giantit.minecraft.database.example.basic;

import nl.giantit.minecraft.database.Database;
import nl.giantit.minecraft.database.Driver;
import nl.giantit.minecraft.database.QueryResult;
import nl.giantit.minecraft.database.QueryResult.QueryRow;
import nl.giantit.minecraft.database.query.DeleteQuery;
import nl.giantit.minecraft.database.query.InsertQuery;
import nl.giantit.minecraft.database.query.SelectQuery;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class dbExample extends JavaPlugin {

	private Database dbDriver;

	@Override
	public void onEnable() {
		if(!getDataFolder().exists()) {
			getDataFolder().mkdir();
			getDataFolder().setWritable(true);
			getDataFolder().setExecutable(true);
		}

		HashMap<String, String> dbData = new HashMap<String, String>();
		dbData.put("driver", "SQLite");
		dbData.put("database", "dbExample");
		dbData.put("prefix", "basic_");
		dbData.put("debug", "true");

		this.dbDriver = Database.Obtain(this, null, dbData);

		new InitDatabase(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if(args.length == 0) {
			Boolean isLucky;

			SelectQuery sQ = this.dbDriver.getEngine().select("lucky");
			sQ.from("#__playerData");
			sQ.where("player", sender.getName());

			QueryResult resSet = sQ.exec();
			if(resSet.size() > 0) {
				QueryRow res = resSet.getRow();
				isLucky = res.getString("lucky").equals("1");
			}else{
				Random rand = new Random();
				int x = rand.nextInt(10) > 5 ? 1 : 0;

				ArrayList<String> fields = new ArrayList<String>();
				fields.add("player");
				fields.add("lucky");

				InsertQuery iQ = this.dbDriver.getEngine().insert("#__playerData");
				iQ.addFields(fields);
				
				iQ.addRow();
				iQ.assignValue("player", sender.getName());
				iQ.assignValue("lucky", String.valueOf(x), InsertQuery.ValueType.RAW);
				iQ.exec();

				isLucky = x == 1;
			}

			if(isLucky) {
				sender.sendMessage("You are lucky!");
			}else{
				sender.sendMessage("Sorry, you are not lucky! :(");
			}

			if(sender instanceof Player) {
				sender.sendMessage("type /dbExample reset to try again!");
			}else{
				sender.sendMessage("type dbExample reset to try again!");
			}
		}else{
			DeleteQuery dQ = this.dbDriver.getEngine().delete("#__playerData");
			dQ.where("player", sender.getName());
			dQ.exec();
			sender.sendMessage("Your luckyness has been reset!");
		}

		return true;
	}

	public Driver getDb() {
		return this.dbDriver.getEngine();
	}
}
