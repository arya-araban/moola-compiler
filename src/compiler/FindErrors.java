package compiler;

import compiler.error.Error;
import compiler.misc.UsefulMethods;
import compiler.symbolTable.ClassRecord;
import compiler.symbolTable.MethodRecord;
import compiler.symbolTable.Record;
import compiler.symbolTable.SymbolTable;
import gen.MoolaListener;
import gen.MoolaParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import java.util.*;


public class FindErrors implements MoolaListener {
    SymbolTable curST;
    String lastMethodName;
    ArrayList<String> currentFunctions = new ArrayList<String>(); // an arraylist in form of "func-name~~numVariables"

    public static ArrayList<Error> errors = new ArrayList<Error>();

    @Override
    public void enterProgram(MoolaParser.ProgramContext ctx) {
        curST = SymbolTable.getSymbolTableByKey("GLOBAL_" + ctx.start.getLine() + "_0");

        //Catching error 410 in here
        System.out.println("--------------Test-------------");
        Map<String, Record> itemss = curST.getItems();
        String key;
        ClassRecord value;
        String cName;
        String pName;
        Map<String, ClassRecord> temp = new HashMap<>();
        for (Map.Entry<String, Record> entry : itemss.entrySet()) {
            key = entry.getKey();
            if (!key.contains("Class_")) {
                continue;
            }
            value = (ClassRecord) entry.getValue();
            cName = value.getName();
            if (cName != null) {
                temp.put(cName, value);
            }
        }
        String classes = findParent(temp);
//        System.out.println(classes);
        String[] inherits = classes.split("~~");
        for (String inherit : inherits) {
            boolean dup;
            String[] item = inherit.split(",");
            dup = UsefulMethods.haveDuplicate(item);
            if (dup) {
                StringBuilder tempError = new StringBuilder();
                for (String s : item) {
                    tempError.append("[").append(s).append("] ->");
                }
                FindErrors.errors.add(new Error(410, tempError.substring(0, tempError.length() - 3)));
            }
        }
        System.out.println("--------------Test-------------");
    }

    public String findParent(Map<String, ClassRecord> classes) {//410
        String ouput;
        StringBuilder sb = new StringBuilder();
        String[][] name = new String[classes.size()][classes.size()];
        String val, cName, pName, tempPname;
        ClassRecord cr, tempPar;
        for (Map.Entry<String, ClassRecord> c : classes.entrySet()) {
            cr = c.getValue();
            cName = cr.getName();
            pName = cr.getParentClass();
            boolean haveParent = true;
            if (pName != null) {
                if (pName.equals(cName)) {
                    sb.append(cName).append(",").append(pName).append("~~");
                } else {
                    tempPname = pName;
                    sb.append(cName).append(",");
                    while (haveParent) {
                        if (classes.get(tempPname) != null) {
                            tempPar = classes.get(tempPname);
                            if (tempPar.getParentClass() != null) {
//                                tempPname = pName;
                                sb.append(tempPname).append(",");
                                tempPname = tempPar.getParentClass();
                                if (tempPname.equals(cName)) {
                                    haveParent = false;
                                    sb.append(tempPname).append(",").append("~~");
//                                System.out.println(sb.toString());
                                }
                            } else {
                                haveParent = false;
                                sb.append(tempPname).append(",").append("~~");
//                            System.out.println(sb.toString());
                            }
                        }
                    }
                }
            }
//            System.out.println(cName + ": " + pName);
        }
        ouput = sb.toString();
        return ouput;
    }

    @Override
    public void exitProgram(MoolaParser.ProgramContext ctx) {
        curST = curST.getPar();
        SymbolListener.printErrors(FindErrors.errors);
    }

    @Override
    public void enterClassDeclaration(MoolaParser.ClassDeclarationContext ctx) {
        int line = ctx.start.getLine();
        int column = ctx.className.getCharPositionInLine();

        String txt = ctx.getText();

        //finding mismatch for functions -- catching error 220
        ArrayList<String> myList;
        myList = UsefulMethods.getAllMatches(txt,"\\.(.*?)(.\\))");
        myList.removeIf(val -> !val.contains("("));
        for (int i=0; i<myList.size();i++){
            String ts = myList.get(i);
            int cnt = UsefulMethods.countChar(ts, ',');
            ts = ts.substring(1,ts.indexOf("("));
            ts = ts + "~~"+(cnt+1);
            myList.set(i,ts);
        }
        for (String val1:myList){
            String tmp1 = val1.substring(0,val1.indexOf("~"));
            for (String val2:currentFunctions){
               String tmp2 = val2.substring(0,val2.indexOf("~"));
               if (tmp1.equals(tmp2)){
                   if (!val1.endsWith(val2.substring(val2.length() - 1)))
                   {
                       FindErrors.errors.add(new Error(220, line , column,"mismatch arguments for function [" + tmp1+"]"));
                   }

               }
            }
        }

        curST = SymbolTable.getSymbolTableByKey("CLASS_" + line + "_" + column);

    }

    @Override
    public void exitClassDeclaration(MoolaParser.ClassDeclarationContext ctx) {
        curST = curST.getPar();
    }

    @Override
    public void enterEntryClassDeclaration(MoolaParser.EntryClassDeclarationContext ctx) {

    }

    @Override
    public void exitEntryClassDeclaration(MoolaParser.EntryClassDeclarationContext ctx) {

    }

    @Override
    public void enterFieldDeclaration(MoolaParser.FieldDeclarationContext ctx) {

    }

    @Override
    public void exitFieldDeclaration(MoolaParser.FieldDeclarationContext ctx) {

    }

    @Override
    public void enterAccess_modifier(MoolaParser.Access_modifierContext ctx) {

    }

    @Override
    public void exitAccess_modifier(MoolaParser.Access_modifierContext ctx) {

    }

    @Override
    public void enterMethodDeclaration(MoolaParser.MethodDeclarationContext ctx) {
        int line = ctx.start.getLine();
        int column = ctx.methodName.getCharPositionInLine();
        lastMethodName = ctx.methodName.getText();
        String txt = ctx.getText();

        txt = txt.substring(8, txt.indexOf(")")+1);
        txt = txt.substring(0,txt.indexOf("(")) + "~~" + UsefulMethods.countChar(txt,':');
        currentFunctions.add(txt);

        curST = SymbolTable.getSymbolTableByKey("METHOD_" + line + "_" + column);

    }

    @Override
    public void exitMethodDeclaration(MoolaParser.MethodDeclarationContext ctx) {
        //System.out.println(curST.getItem(lastMethodName));
        int line = ctx.start.getLine();
        int column = ctx.methodName.getCharPositionInLine();
        MethodRecord methRec = (MethodRecord) curST.getItem(lastMethodName);
        ArrayList<String> classesToCheck = new ArrayList<String>();
        for (String item : methRec.getParameters()){
           if (item.contains("~")) {
               String sbstr = item.substring(0, item.indexOf("~"));
               System.out.println(sbstr);
               if (!sbstr.equals("bool") && !sbstr.equals("int")){
                   classesToCheck.add("Class_"+sbstr);
               }
           }
        }
        for (int i=0; i < classesToCheck.size();i++){
            if (!curST.lookup(classesToCheck.get(i))){
                FindErrors.errors.add(new Error(105, line, column, "can not find " + classesToCheck.get(i)));
            }
            if (i==classesToCheck.size()-1) classesToCheck.clear();
        }
        curST = curST.getPar();
    }

    @Override
    public void enterClosedStatement(MoolaParser.ClosedStatementContext ctx) {

    }

    @Override
    public void exitClosedStatement(MoolaParser.ClosedStatementContext ctx) {

    }

    @Override
    public void enterClosedConditional(MoolaParser.ClosedConditionalContext ctx) {

    }

    @Override
    public void exitClosedConditional(MoolaParser.ClosedConditionalContext ctx) {

    }

    @Override
    public void enterOpenConditional(MoolaParser.OpenConditionalContext ctx) {

    }

    @Override
    public void exitOpenConditional(MoolaParser.OpenConditionalContext ctx) {

    }

    @Override
    public void enterOpenStatement(MoolaParser.OpenStatementContext ctx) {

    }

    @Override
    public void exitOpenStatement(MoolaParser.OpenStatementContext ctx) {

    }

    @Override
    public void enterStatement(MoolaParser.StatementContext ctx) {

    }

    @Override
    public void exitStatement(MoolaParser.StatementContext ctx) {

    }

    @Override
    public void enterStatementVarDef(MoolaParser.StatementVarDefContext ctx) {

    }

    @Override
    public void exitStatementVarDef(MoolaParser.StatementVarDefContext ctx) {

    }

    @Override
    public void enterStatementBlock(MoolaParser.StatementBlockContext ctx) {
        int line = ctx.start.getLine();
        int column = ctx.start.getCharPositionInLine();
        curST = SymbolTable.getSymbolTableByKey("BLOCK_" + line + "_" + column);

    }

    @Override
    public void exitStatementBlock(MoolaParser.StatementBlockContext ctx) {
        curST = curST.getPar();
    }

    @Override
    public void enterStatementContinue(MoolaParser.StatementContinueContext ctx) {

    }

    @Override
    public void exitStatementContinue(MoolaParser.StatementContinueContext ctx) {

    }

    @Override
    public void enterStatementBreak(MoolaParser.StatementBreakContext ctx) {

    }

    @Override
    public void exitStatementBreak(MoolaParser.StatementBreakContext ctx) {

    }

    @Override
    public void enterStatementReturn(MoolaParser.StatementReturnContext ctx) {

    }

    @Override
    public void exitStatementReturn(MoolaParser.StatementReturnContext ctx) {

    }

    @Override
    public void enterStatementClosedLoop(MoolaParser.StatementClosedLoopContext ctx) {

    }

    @Override
    public void exitStatementClosedLoop(MoolaParser.StatementClosedLoopContext ctx) {

    }

    @Override
    public void enterStatementOpenLoop(MoolaParser.StatementOpenLoopContext ctx) {

    }

    @Override
    public void exitStatementOpenLoop(MoolaParser.StatementOpenLoopContext ctx) {

    }

    @Override
    public void enterStatementWrite(MoolaParser.StatementWriteContext ctx) {

    }

    @Override
    public void exitStatementWrite(MoolaParser.StatementWriteContext ctx) {

    }

    @Override
    public void enterStatementAssignment(MoolaParser.StatementAssignmentContext ctx) { //catching error 106
       // System.out.println(ctx.getText());
        String curAssigned = ctx.getChild(0).getText();
        int line = ctx.start.getLine();
        int column = ctx.start.getCharPositionInLine();
        if (curST.getItem(curAssigned)==null){
            FindErrors.errors.add(new Error(106, line, column, "can not find variable [" + curAssigned + "]"));
        }
    }

    @Override
    public void exitStatementAssignment(MoolaParser.StatementAssignmentContext ctx) {

    }

    @Override
    public void enterStatementInc(MoolaParser.StatementIncContext ctx) {
    }

    @Override
    public void exitStatementInc(MoolaParser.StatementIncContext ctx) {

    }

    @Override
    public void enterStatementDec(MoolaParser.StatementDecContext ctx) {

    }

    @Override
    public void exitStatementDec(MoolaParser.StatementDecContext ctx) {

    }

    @Override
    public void enterExpression(MoolaParser.ExpressionContext ctx) {

    }

    @Override
    public void exitExpression(MoolaParser.ExpressionContext ctx) {

    }

    @Override
    public void enterExpressionOr(MoolaParser.ExpressionOrContext ctx) {

    }

    @Override
    public void exitExpressionOr(MoolaParser.ExpressionOrContext ctx) {

    }

    @Override
    public void enterExpressionOrTemp(MoolaParser.ExpressionOrTempContext ctx) {

    }

    @Override
    public void exitExpressionOrTemp(MoolaParser.ExpressionOrTempContext ctx) {

    }

    @Override
    public void enterExpressionAnd(MoolaParser.ExpressionAndContext ctx) {

    }

    @Override
    public void exitExpressionAnd(MoolaParser.ExpressionAndContext ctx) {

    }

    @Override
    public void enterExpressionAndTemp(MoolaParser.ExpressionAndTempContext ctx) {

    }

    @Override
    public void exitExpressionAndTemp(MoolaParser.ExpressionAndTempContext ctx) {

    }

    @Override
    public void enterExpressionEq(MoolaParser.ExpressionEqContext ctx) {

    }

    @Override
    public void exitExpressionEq(MoolaParser.ExpressionEqContext ctx) {

    }

    @Override
    public void enterExpressionEqTemp(MoolaParser.ExpressionEqTempContext ctx) {

    }

    @Override
    public void exitExpressionEqTemp(MoolaParser.ExpressionEqTempContext ctx) {

    }

    @Override
    public void enterExpressionCmp(MoolaParser.ExpressionCmpContext ctx) {

    }

    @Override
    public void exitExpressionCmp(MoolaParser.ExpressionCmpContext ctx) {

    }

    @Override
    public void enterExpressionCmpTemp(MoolaParser.ExpressionCmpTempContext ctx) {

    }

    @Override
    public void exitExpressionCmpTemp(MoolaParser.ExpressionCmpTempContext ctx) {

    }

    @Override
    public void enterExpressionAdd(MoolaParser.ExpressionAddContext ctx) {

    }

    @Override
    public void exitExpressionAdd(MoolaParser.ExpressionAddContext ctx) {

    }

    @Override
    public void enterExpressionAddTemp(MoolaParser.ExpressionAddTempContext ctx) {

    }

    @Override
    public void exitExpressionAddTemp(MoolaParser.ExpressionAddTempContext ctx) {

    }

    @Override
    public void enterExpressionMultMod(MoolaParser.ExpressionMultModContext ctx) {

    }

    @Override
    public void exitExpressionMultMod(MoolaParser.ExpressionMultModContext ctx) {

    }

    @Override
    public void enterExpressionMultModTemp(MoolaParser.ExpressionMultModTempContext ctx) {

    }

    @Override
    public void exitExpressionMultModTemp(MoolaParser.ExpressionMultModTempContext ctx) {

    }

    @Override
    public void enterExpressionUnary(MoolaParser.ExpressionUnaryContext ctx) {

    }

    @Override
    public void exitExpressionUnary(MoolaParser.ExpressionUnaryContext ctx) {

    }

    @Override
    public void enterExpressionMethods(MoolaParser.ExpressionMethodsContext ctx) {

    }

    @Override
    public void exitExpressionMethods(MoolaParser.ExpressionMethodsContext ctx) {

    }

    @Override
    public void enterExpressionMethodsTemp(MoolaParser.ExpressionMethodsTempContext ctx) {

    }

    @Override
    public void exitExpressionMethodsTemp(MoolaParser.ExpressionMethodsTempContext ctx) {

    }

    @Override
    public void enterExpressionOther(MoolaParser.ExpressionOtherContext ctx) {

    }

    @Override
    public void exitExpressionOther(MoolaParser.ExpressionOtherContext ctx) {

    }

    @Override
    public void enterMoolaType(MoolaParser.MoolaTypeContext ctx) {

    }

    @Override
    public void exitMoolaType(MoolaParser.MoolaTypeContext ctx) {

    }

    @Override
    public void enterSingleType(MoolaParser.SingleTypeContext ctx) {

    }

    @Override
    public void exitSingleType(MoolaParser.SingleTypeContext ctx) {

    }

    @Override
    public void visitTerminal(TerminalNode terminalNode) {

    }

    @Override
    public void visitErrorNode(ErrorNode errorNode) {

    }

    @Override
    public void enterEveryRule(ParserRuleContext parserRuleContext) {

    }

    @Override
    public void exitEveryRule(ParserRuleContext parserRuleContext) {

    }


}
