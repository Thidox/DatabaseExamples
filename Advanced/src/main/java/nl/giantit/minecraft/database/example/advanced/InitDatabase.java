package nl.giantit.minecraft.database.example.advanced;

import nl.giantit.minecraft.database.Driver;
import nl.giantit.minecraft.database.query.Column;
import nl.giantit.minecraft.database.query.CreateQuery;
import nl.giantit.minecraft.database.query.InsertQuery;

import java.util.ArrayList;

public class InitDatabase {

	private dbExample p;
	
	public InitDatabase(dbExample p) {
		this.p = p;
		
		this.init();
	}
	
	public void init() {
		Driver db = this.p.getDb();
		
		if(!db.tableExists("#__versions")) {
			// Create table #__versions
			// (tableName VARCHAR(100) NOT NULL,
			// version DOUBLE NOT NULL DEFAULT '1.0');
			
			CreateQuery cQ = db.create("#__versions");
			Column tN = cQ.addColumn("tableName");
			tN.setDataType(Column.DataType.VARCHAR);
			tN.setLength(100);
			
			Column v = cQ.addColumn("tableName");
			v.setDataType(Column.DataType.DOUBLE);
			v.setRawDefault("1.0");
			
			cQ.exec();
			
			p.getLogger().info("Revisions table successfully created!");
		}
		
		if(!db.tableExists("#__playerData")) {
			ArrayList<String> field = new ArrayList<String>();
			field.add("tablename");
			field.add("version");
			
			InsertQuery iQ = db.insert("#__versions");
			iQ.addFields(field);

			iQ.addRow();
			iQ.assignValue("tablename", "playerData");
			iQ.assignValue("version", "1.0", InsertQuery.ValueType.RAW);
			iQ.exec();
			
			CreateQuery cQ = db.create("#__playerData");
			Column id = cQ.addColumn("id");
			id.setDataType(Column.DataType.INT);
			id.setLength(3);
			id.setAutoIncr();
			id.setPrimaryKey();
			
			Column pC = cQ.addColumn("player");
			pC.setDataType(Column.DataType.VARCHAR);
			pC.setLength(100);
			
			Column bB = cQ.addColumn("blocksBroken");
			bB.setDataType(Column.DataType.INT);
			bB.setLength(5);
			bB.setRawDefault("0");
			
			Column mM = cQ.addColumn("movesMade");
			mM.setDataType(Column.DataType.INT);
			mM.setLength(5);
			mM.setRawDefault("0");
			
			Column tC = cQ.addColumn("timesClicked");
			tC.setDataType(Column.DataType.INT);
			tC.setLength(5);
			tC.setRawDefault("0");
			
			cQ.exec();
			
			p.getLogger().info("Player data table successfully created!");
		}
	}
}
