package compiler.symbolTable;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class SymbolTable {

    // A hash map storing entries of the form {key = symbol name, value = type}
    Map<String, Record> st = new LinkedHashMap<String, Record>();


    int line;
    String tableName;


    public Map<String, Record> getSt() {
        return st;
    }
    public int getLine() {
        return line;
    }

    // Constructor
    public SymbolTable(String tableName,int line) {
        this.tableName = tableName;
        this.line = line;
    }

    // Insert a new symbol into the symbol table. This operation is UNPROTECTED and
    // should ONLY be done following a negative lookup() operation.
    public void insert(String key, Record value) {
        st.put(key, value);
    }

    public void remove(String key) {
        st.remove(key);
    }

    // Return true if the symbol already exists in the GIVEN SCOPE, otherwise return
    // false.
    public boolean lookup(String key) {
        if (st.get(key) != null) {
            return true;
        }
        return false;
    }

    // Print the symbol table to console
//    public void prettyPrint() {
//
//        System.out.println("\nSymbol table " + tableName);
//        System.out.println("--------------");
//        // According to the javadoc, LinkedHashMap.entrySet() should be in the order
//        // that the elements were inserted initially.
//        Iterator it = st.entrySet().iterator();
//        while (it.hasNext()) {
//            Map.Entry pair = (Map.Entry) it.next();
//            System.out.println("Key = " + pair.getKey().toString() + " | " + "Value = " + pair.getValue().toString());
//            it.remove();
//        }
//        // Print blank line at end of symbol table.
//        System.out.println();
//    }
}
