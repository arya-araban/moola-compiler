package compiler.symbolTable;

import java.util.ArrayList;

public class VarRecord extends Record { /*ALSO HANDLES PARAMETER RECORDS */
    String varLocation = "LocalVar";
    String type;
    String index = "-1";
    private String typeOrValue = "type";

    public VarRecord(String n, String type) { /*FOR ACTUAL VARS IN METHOD*/
        super(n);
        this.type = type;
        this.typeOrValue = "value";
    }

    public VarRecord(String n, String type, String index) { /* for PARAMETERS OF METHODS */
        super(n);

        this.index = index;
        this.varLocation = "Parameter";
        if (!type.matches("int|string|bool")) {
            type = "ClassType="+type;
        }
        this.type = type;
    }

    public String toString() { //THE VALUE WE HAVE FOR OBJECT
        String s = "";
        s += this.varLocation + ": " + "(name: " + super.getName() + ")" + "(" + typeOrValue + ": " + this.type + ")";
        if (!index.equals("-1"))
            s += "(index: " + this.index + ") ";
        return s;
    }

}
