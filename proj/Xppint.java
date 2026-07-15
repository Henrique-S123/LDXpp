package proj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import proj.ast.*;
import proj.values.*;
import proj.parser.*;
import proj.types.*;
import proj.env.*;

public class Xppint {
	static Parser parser;

    public static void main(String args[]) {
		if (args.length == 0) repl();
		else runFile(args[0]);
    }

	private static void runFile(String filename) {
		ASTNode exp;
		try {
			parser = new Parser(new FileInputStream(filename));
			while (true) {
				try {
					exp = parser.Start();
					if (exp == null) System.exit(0);
					ASTType t = exp.typecheck(new EnvSet(), null);
					IValue v = exp.eval(new Env<IValue>());
					System.out.println("type: " + t + ", value: " + v);
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
	}

	private static void repl() {
		ASTNode exp;
		System.out.println("LDX++ interpreter\n");
		parser = new Parser(System.in); 
		while (true) {
			try {
				System.out.print("# ");
				exp = parser.Start();
				if (exp==null) System.exit(0);
				System.out.println(exp);
				System.out.println(exp.typecheck(new EnvSet(), null));
				System.out.println(exp.eval(new Env<IValue>()));
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
