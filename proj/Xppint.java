package proj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import proj.ast.*;
import proj.values.*;
import proj.parser.*;
import proj.types.*;
import proj.env.*;

public class Xppint {

    public static void main(String args[]) {
		ASTNode exp;
		Parser parser;

		if (args.length > 0) {
			try {
				parser = new Parser(new FileInputStream(args[0]));

				while (true) {
					try {
						exp = parser.Start();
						if (exp == null) System.exit(0);
						ASTType t = exp.typecheck(new Environment<ASTType>());
						IValue v = exp.eval(new Environment<IValue>());
						System.out.println("type: " + t.toStr() + ", value: " + v.toStr());
					} catch (ParseException e) {
						System.err.println("Syntax Error.");
					} catch (Exception e) {
						System.out.println(e.getClass() + ": " + e.getMessage());
					} finally {
						System.out.println("\n");
					}
				}
			} catch (FileNotFoundException e) {
				System.err.println(e.getMessage());
			}
		} else {
			System.out.println("LFX++ interpreter\n");
			parser = new Parser(System.in); 
			while (true) {
			try {
				System.out.print("# ");
				exp = parser.Start();
				if (exp==null) System.exit(0);
				ASTType t = exp.typecheck(new Environment<ASTType>());
				System.out.println(t.toStr());
				IValue v = exp.eval(new Environment<IValue>());
				System.out.println(v.toStr());
			} catch (ParseException e) {
				System.out.println("Syntax Error.");
				parser.ReInit(System.in);
			} catch (Exception e) {
				e.printStackTrace();
				parser.ReInit(System.in);
			}
		}
		}
    }
    
}
