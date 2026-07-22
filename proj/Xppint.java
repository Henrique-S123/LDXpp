package proj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import proj.parser.*;
import proj.commands.*;

public class Xppint {
	static Parser parser;

    public static void main(String args[]) {
		if (args.length == 0) repl();
		else runFile(args[0]);
    }

	private static void runFile(String filename) {
		Command exp;
		try {
			parser = new Parser(new FileInputStream(filename));
			while (true) {
				try {
					exp = parser.Start();
					if (exp == null) System.exit(0);
					exp.executeCommand();
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
		Command exp;
		System.out.println("LDX++ interpreter\n");
		parser = new Parser(System.in); 
		while (true) {
			try {
				System.out.print("# ");
				exp = parser.Start();
				if (exp==null) System.exit(0);
				exp.executeCommand();
			} catch (Exception e) {
				e.printStackTrace();
				parser.ReInit(System.in);
			}
		}
	}
}
