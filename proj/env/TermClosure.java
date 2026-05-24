package proj.env;

import proj.ast.ASTNode;
import proj.types.ASTType;

public record TermClosure(ASTNode term, Env<ASTType> env) {}
