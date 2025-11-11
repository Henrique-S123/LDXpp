package proj.env;

import proj.types.*;

import java.util.HashMap;

public class TypeBindList  {

	private HashMap<String,ASTType> lbl;

	public TypeBindList(HashMap<String,ASTType> ll) {
		lbl = ll;
	}

	public HashMap<String, ASTType> getMap() {
		return lbl;
	}
}