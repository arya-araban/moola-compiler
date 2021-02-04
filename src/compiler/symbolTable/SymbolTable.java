package compiler.symbolTable;

import java.util.LinkedHashMap;
import java.util.Map;

public class SymbolTable {

    // A hash map storing entries of the form {key = symbol name, value = type}
    Map<String, Record> items = new LinkedHashMap<String, Record>();
    int line, column;
    SymbolTable par;
    String tableName;

    //these variables static -> for the entire symboltables
    static LinkedHashMap<String, SymbolTable> symbolTables = new LinkedHashMap<String, SymbolTable>();
    static String lastTableKey;


//    public static void printAllTables() {
//        for (Object value : symbolTables.values()) {
//            System.out.println(value);
//        }
//    }

    public String getTableName() {
        return tableName;
    }

    public SymbolTable getPar() {
        return par;
    }

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
        String finalName;
        if (st.tableName.contains(" "))
            finalName = st.tableName.substring(0, st.tableName.indexOf(" "));
        else
            finalName = st.tableName;

        finalName = finalName + "_" + st.line + "_" + st.column;
        System.out.println(finalName);
        symbolTables.put(finalName, st);
        lastTableKey = finalName;
    }

    public Record getItem(String key) {
        SymbolTable cur = this;
        while (cur != null) {
            if (cur.items.get(key) != null)
                return cur.items.get(key);
            cur = cur.par;
        }
        return null;
    }

    public static SymbolTable getSymbolTableByKey(String key) {
        return symbolTables.get(key);
    }

    public static String getLastTableKey() {
        return lastTableKey;
    }

    public static void printAllST() {

        for (Map.Entry<String, SymbolTable> entry : symbolTables.entrySet()) {
            String STKey = entry.getKey();
            SymbolTable STValue = entry.getValue();
            System.out.println("\n" + STValue.tableName + " -~-~-~- LINE: " + STValue.getLine());
            System.out.println("--------------");
            for (Map.Entry<String, Record> entry2 : STValue.getItems().entrySet()) {
                String recordKey = entry2.getKey();
                Record recordValue = entry2.getValue();
                System.out.println("Key = " + recordKey.toString() + " | " + "Value = " + recordValue.toString());
            }
        }
        System.out.println();

    }


}
