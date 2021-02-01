package compiler.symbolTable;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class SymbolTable {

    // A hash map storing entries of the form {key = symbol name, value = type}
    Map<String, Record> items = new LinkedHashMap<String, Record>();
    int line, column;
    SymbolTable par;

    public String getTableName() {
        return tableName;
    }

    String tableName;

    //these variables static -> for the entire symboltables
    static LinkedHashMap<String, SymbolTable> symbolTables = new LinkedHashMap<String, SymbolTable>();
    static String lastTableName;

    public Map<String, Record> getItems() {
        return items;
    }

    public int getLine() {
        return line;
    }

    // Constructor
    public SymbolTable(String tableName, int line, int column, SymbolTable par) {
        this.tableName = tableName;
        this.line = line;
        this.column = column;
        this.par = par;
    }

    // Insert a new symbol into the symbol table. This operation is UNPROTECTED and
    // should ONLY be done following a negative lookup() operation.
    public void insert(String itemKey, Record value) {
        items.put(itemKey, value);
    }


    public void remove(String key) {
        items.remove(key);
    }

    // Return true if the symbol already exists in the GIVEN SCOPE, otherwise return
    // false.
    public boolean lookup(String key) {
        SymbolTable parent = this;
        while (parent != null) {
            if (parent.items.get(key) != null)
                return true;
            parent = parent.par;
        }
        return false;
    }

    public boolean lookupCurScope(String key) {
        return this.items.get(key) != null;
    }



    /*METHODS THAT REFER TO ENTIRE SYMBOLTABLES FROM HERE ON OUT */


    public static void addSymbolTable(SymbolTable st) {
        symbolTables.put(st.tableName, st);
        lastTableName = st.tableName;
    }

    public static SymbolTable getSymbolTableByKey(String key) {
        return symbolTables.get(key);
    }

    public static String getLastTableName() {
        return lastTableName;
    }

    public static void printAllST() {

        Iterator it1 = symbolTables.entrySet().iterator(); //iterating over the symbol tables we have
        while (it1.hasNext()) {
            Map.Entry st = (Map.Entry) it1.next();
            System.out.println("\n" + st.getKey() + " -~-~-~- LINE: " + ((SymbolTable) st.getValue()).getLine());
            System.out.println("--------------");
            Iterator it2 = ((SymbolTable) st.getValue()).getItems().entrySet().iterator(); //iterating over the entries of the symbol table
            while (it2.hasNext()) {
                Map.Entry pair = (Map.Entry) it2.next();
                System.out.println("Key = " + pair.getKey().toString() + " | " + "Value = " + pair.getValue().toString());
                it2.remove();
            }
            // Print blank line at end of symbol table.
            it1.remove();
            System.out.println();
        }
    }

}
