package proj.env;

import proj.ast.*;

import java.util.HashMap;

public class ExprBindList  {

        private HashMap<String, ASTNode> lbl;

        public ExprBindList(HashMap<String, ASTNode> ll) {
                lbl = ll;
        }

        public HashMap<String, ASTNode> getMap() {
                return lbl;
        }
}