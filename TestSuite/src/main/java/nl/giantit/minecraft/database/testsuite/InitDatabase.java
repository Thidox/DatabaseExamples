package nl.giantit.minecraft.database.testsuite;

import nl.giantit.minecraft.database.Driver;
import nl.giantit.minecraft.database.query.AlterQuery;
import nl.giantit.minecraft.database.query.Column;
import nl.giantit.minecraft.database.query.CreateQuery;
import nl.giantit.minecraft.database.query.DeleteQuery;
import nl.giantit.minecraft.database.query.Group;
import nl.giantit.minecraft.database.query.InsertQuery;
import nl.giantit.minecraft.database.query.UpdateQuery;

/**
 *
 * @author Giant
 */
public class InitDatabase {

	private final TestSuite p;
	private String table = "#__testSuite";
	private String table2 = "#__testSuite2";
	
	public InitDatabase(TestSuite p) {
		this.p = p;
	}
	
	public void init() {
		Driver db = p.getDb();
		
		CreateQuery cQ = db.create(this.table);
		Column id = cQ.addColumn("id");
		id.setDataType(Column.DataType.INT);
		id.setLength(3);
		id.setAutoIncr();
		id.setPrimaryKey();
		
		Column tS = cQ.addColumn("testSuite1");
		tS.setDataType(Column.DataType.INT);
		tS.setLength(3);
		
		Column tS2 = cQ.addColumn("testSuite2");
		tS2.setDataType(Column.DataType.DATETIME);
		
		Column tS3 = cQ.addColumn("testSuite3");
		tS3.setDataType(Column.DataType.VARCHAR);
		tS3.setLength(100);
		
		cQ.parse().exec(true);
	}
	
	public void fill(int count) {
		Driver db = p.getDb();
		InsertQuery iQ = db.insert(this.table);
		iQ.addFields("testSuite1", "testSuite2", "testSuite3");
		
		for(int i = 0; i < count; i++) {
			iQ.addRow();
			iQ.assignValue("testSuite1", "1", InsertQuery.ValueType.RAW);
			iQ.assignValue("testSuite2", "NOW()", InsertQuery.ValueType.RAW);
			iQ.assignValue("testSuite3", "asfskjfakjbfkjabsd");
		}
		
		iQ.parse().exec(true);
	}
	
	public void delete(int count) {
		Driver db = p.getDb();
		DeleteQuery dQ = db.delete(this.table);
		dQ.where("id", String.valueOf(count), Group.ValueType.EQUALSRAW);
		
		dQ.parse().exec(true);
	}
	
	public void truncate() {
		Driver db = p.getDb();
		db.Truncate(this.table).parse().exec(true);
	}
	
	public void update() {
		Driver db = p.getDb();
		UpdateQuery uQ = db.update(this.table);
		uQ.set("testSuite1", "testSuite1 + 1", UpdateQuery.ValueType.SETRAW);
		uQ.set("testSuite2", "NOW()", UpdateQuery.ValueType.SETRAW);
		
		uQ.parse().exec(true);
	}
	
	public void alter(boolean add) {
		Driver db = p.getDb();
		AlterQuery aQ = db.alter(this.table);
		if(add) {
			Column tS4 = aQ.addColumn("testSuite4");
			tS4.setDataType(Column.DataType.VARCHAR);
			tS4.setLength(100);
			tS4.setNull();
		}else{
			aQ.rename(this.table2);
			String tmp = this.table;
			this.table = this.table2;
			this.table2 = tmp;
		}
		aQ.parse().exec(true);
	}
	
	public void drop() {
		Driver db = p.getDb();
		db.drop(this.table).parse().exec(true);
	}
}
