package nl.giantit.minecraft.database.testsuite;

import nl.giantit.minecraft.database.Database;
import nl.giantit.minecraft.database.Driver;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

/**
 *
 * @author Giant
 */
public class TestSuite extends JavaPlugin {

	private Database dbDriver;
	private InitDatabase iD;

	@Override
	public void onEnable() {
		if(!getDataFolder().exists()) {
			getDataFolder().mkdir();
			getDataFolder().setWritable(true);
			getDataFolder().setExecutable(true);
		}

		HashMap<String, String> dbData = new HashMap<String, String>();
		dbData.put("driver", "MySQL");
		dbData.put("host", "localhost");
		dbData.put("port", "3306");
		dbData.put("database", "minecraft");
		dbData.put("user", "minecraft");
		dbData.put("password", "minecraft");
		dbData.put("prefix", "tS_");
		dbData.put("debug", "true");

		this.dbDriver = Database.Obtain(this, null, dbData);

		this.iD = new InitDatabase(this);
	}
	
	@Override
	public void onDisable() {
		this.dbDriver.getEngine().close();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if(args.length > 0) {
			if(args[0].equals("init")) {
				sender.sendMessage("Creating table!");
				this.iD.init();
				sender.sendMessage("Finished creating table!");
			}else if(args[0].equals("fill")) {
				if(args.length > 1) {
					try {
						int count = Integer.parseInt(args[1]);
						sender.sendMessage("Filling table with " + String.valueOf(count) + " rows!");
						this.iD.fill(count);
						sender.sendMessage("Finished filling table!");
					}catch(NumberFormatException e) {
						sender.sendMessage("Y U NO SMART?!");
					}
				}
			}else if(args[0].equals("delete")) {
				if(args.length > 1) {
					try {
						int count = Integer.parseInt(args[1]);
						sender.sendMessage("Deleting row " + String.valueOf(count) + " from table!");
						this.iD.delete(count);
						sender.sendMessage("Finished deleting row!");
					}catch(NumberFormatException e) {
						sender.sendMessage("Y U NO SMART?!");
					}
				}
			}else if(args[0].equals("truncate")) {
				sender.sendMessage("Truncating table!");
				this.iD.truncate();
				sender.sendMessage("Finished truncating rows!");
			}else if(args[0].equals("update")) {
				sender.sendMessage("Updating rows!");
				this.iD.update();
				sender.sendMessage("Finished updating rows!");
			}else if(args[0].equals("alter")) {
				if(args.length > 1) {
					if(args[1].equals("rename")) {
						sender.sendMessage("Renaming table!");
						this.iD.alter(false);
						sender.sendMessage("Finished renaming table!");
					}else{
						sender.sendMessage("Adding column!");
						this.iD.alter(true);
						sender.sendMessage("Finished adding column!");
					}
				}else{
					sender.sendMessage("Y U NO SMART?!");
				}
			}else if(args[0].equals("drop")) {
				sender.sendMessage("Dropping table!");
				this.iD.drop();
				sender.sendMessage("Finished dropping rows!");
			}else{
				sender.sendMessage("Y U NO SMART?!");
			}
		}else{
			sender.sendMessage("Y U NO SMART?!");
		}
		return true;
	}

	public Driver getDb() {
		return this.dbDriver.getEngine();
	}
	
}
