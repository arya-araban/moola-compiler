package compiler.error;

public class Error {
    public int type;
    public int line;
    public int column;
    String text;

    public Error(int type, int line, int column, String text) {
        this.column = column;
        this.line = line;
        this.text = text;
        this.type = type;
    }

    public Error(int type, String text) {
        this.text = text;
        this.type = type;
    }

    public String toString() {
        if (this.type == 410) { //error of type 410
            return "Error" + type + " : Invalid inheritance " + text;
        } else { //other errors
            return "Error" + type + " : in line [" + line + ":" + column + "], " + text;
        }

    }

}
