package compiler.symbolTable;

import java.util.ArrayList;

/* ALSO HANDLES FIELDS! */

public class MethodRecord extends Record {
    String returnType;
    String accessModifier = "public";
    ArrayList<String> parameters;
    String fieldOrMethod = "Method";
    int numberOfParams;

    public MethodRecord(String n, String returnType, String accessModifier, ArrayList<String> parameters) { /*FOR ACTUAL METHODS*/
        super(n);
        this.returnType = returnType;
        this.accessModifier = accessModifier;
        this.parameters = parameters;
        this.numberOfParams = parameters.size();
    }

    public MethodRecord(String n, String returnType, String accessModifier) { /* FOR FIELDS */
        super(n);
        this.returnType = returnType;
        this.accessModifier = accessModifier;
        this.fieldOrMethod = "Field";
    }

    public int getNumberOfParams() {
        return numberOfParams;
    }


    @Override
    public String toString() { //THE VALUE WE HAVE FOR OBJECT
        StringBuilder s = new StringBuilder();


        s.append(fieldOrMethod).append(": (name: ").append(super.getName()).append(") ");

        s.append("(returnType: ").append(this.returnType).append(") ");

        s.append("(accessModifier: ").append(this.accessModifier).append(") ");

        if (fieldOrMethod.equals("Method") && !parameters.isEmpty()) {
            s.append("(parameters: ");
            for (String pt : parameters) {
                String[] result = pt.split("~");
                s.append("[name: ").append(result[1]).append(" ~ Type: ").append(result[0]).append(" ~ index: ").append(result[2]).append("]   ");
            }
            s.append("\b\b\b)");
        }

        return s.toString();

    }
}
