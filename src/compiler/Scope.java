package compiler;

import java.util.Hashtable;

public class Scope {

     Scope par;
     Hashtable<String, String> symbolTable;

    public Scope() {
        symbolTable = new Hashtable<String, String>();
    }

}
