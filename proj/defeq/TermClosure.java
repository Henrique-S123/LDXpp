package proj.defeq;

import proj.ast.ASTNode;
import proj.env.Env;
import proj.types.ASTType;

public record TermClosure(ASTNode term, Env<ASTType> env) {}
