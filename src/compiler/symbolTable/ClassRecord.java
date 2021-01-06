package compiler.symbolTable;

import compiler.symbolTable.Record;

public class ClassRecord extends Record {
    String parentClass;
    boolean isMainClass;

    public ClassRecord(String n) {
        super(n);
    }

    public ClassRecord(String n, String parentClass) {
        super(n);
        this.parentClass = parentClass;
    }


    @Override
    public String toString() { //THE VALUE WE HAVE FOR OBJECT
        String s = "";
        if (isMainClass)
            s += "main";

        s += "Class: (name: " + super.getName() + ") ";

        if (this.parentClass != null)
            s += "(inherits: " + this.parentClass + ")";
        return s;

    }

}
