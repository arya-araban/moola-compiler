package compiler;

import compiler.error.Error;
import compiler.symbolTable.MethodRecord;
import compiler.symbolTable.SymbolTable;
import compiler.symbolTable.ClassRecord;
import compiler.symbolTable.VarRecord;
import gen.MoolaListener;
import gen.MoolaParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.*;

public class SymbolListener implements MoolaListener {
    Stack<String> scopeStack = new Stack<String>();

    @Override
    public void enterProgram(MoolaParser.ProgramContext ctx) {
        SymbolTable.addSymbolTable(new SymbolTable("GLOBAL", ctx.start.getLine(), 0, null));
        this.scopeStack.push(SymbolTable.getLastTableName());
    }

    @Override
    public void exitProgram(MoolaParser.ProgramContext ctx) {
        this.scopeStack.pop();
        SymbolTable.printAllST();
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");

    }

    @Override
    public void enterClassDeclaration(MoolaParser.ClassDeclarationContext ctx) {
        String className = ctx.getChild(1).getText();
        int line = ctx.start.getLine();
        int column = ctx.className.getCharPositionInLine();

        String cls;
        cls = (ctx.parent.getChild(0).getText().equals("entry")) ? "mainClass_" : "Class_";

        boolean exists = SymbolTable.getSymbolTableByKey(this.scopeStack.peek()).lookupCurScope(cls + className);

        if (exists) {
            FindErrors.errors.add(new Error(101, line, column, "class [" + className + "] has been defined already"));
            className += "_" + line + "_" + column;
        }

        if (ctx.getChild(2).getText().equals("inherits")) {
            SymbolTable.getSymbolTableByKey(this.scopeStack.peek()).insert(cls + className,
                    new ClassRecord(className, ctx.classParent.getText()));
        } else
            SymbolTable.getSymbolTableByKey(this.scopeStack.peek()).insert(cls + className,
                    new ClassRecord(className));


        SymbolTable.addSymbolTable(new SymbolTable("CLASS " + className, line, column, SymbolTable.getSymbolTableByKey(this.scopeStack.peek())));
        this.scopeStack.push(SymbolTable.getLastTableName());
    }


    @Override
    public void exitClassDeclaration(MoolaParser.ClassDeclarationContext ctx) {
        this.scopeStack.pop();
    }

    @Override
    public void enterEntryClassDeclaration(MoolaParser.EntryClassDeclarationContext ctx) {

    }

    @Override
    public void exitEntryClassDeclaration(MoolaParser.EntryClassDeclarationContext ctx) {

    }

    @Override
    public void enterFieldDeclaration(MoolaParser.FieldDeclarationContext ctx) {
        String fieldName = ctx.fieldName.getText();
        int line = ctx.start.getLine();
        int column = ctx.fieldName.getCharPositionInLine();
        boolean exists = SymbolTable.getSymbolTableByKey(this.scopeStack.peek()).lookupCurScope(fieldName);
        if (exists) {
            FindErrors.errors.add(new Error(104, line, column, "field [" + fieldName + "] has been defined already"));
            fieldName += "_" + line + "_" + column;
        }
        String am = ctx.getChild(0).getText().equals("field") ? "public" : ctx.fieldAccessModifier.getText();
        SymbolTable.getSymbolTableByKey(this.scopeStack.peek()).insert(fieldName,
                new MethodRecord(fieldName, ctx.fieldType.getText(), am));
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
        String methodName = ctx.methodName.getText();

        int line = ctx.start.getLine();
        int column = ctx.methodName.getCharPositionInLine();

        boolean exists = SymbolTable.getSymbolTableByKey(this.scopeStack.peek()).lookupCurScope(methodName);

        if (exists) {
            FindErrors.errors.add(new Error(102, line, column, "method [" + methodName + "] has been defined already"));
            methodName += "_" + line + "_" + column;
        }

        String am = ctx.getChild(0).getText().equals("function") ? "public" : ctx.methodAccessModifier.getText();
        ArrayList<String> meth = new ArrayList<String>(); //we'll use this to find params!
        for (int i = 0; i < ctx.children.size(); i++) {
            meth.add(ctx.getChild(i).getText());
            if (ctx.getChild(i).getText().equals(")")) break;
        }
        int strt = meth.indexOf("(") + 1, end = meth.indexOf(")") - 1, indx = 1;

        ArrayList<String> params = new ArrayList<String>();
        for (int i = strt; i < end; i += 4) {
            String s = meth.get(i + 2) + "~" + meth.get(i) + "~" + indx;
            indx++;
            params.add(s);
        }
        SymbolTable.getSymbolTableByKey(this.scopeStack.peek()).insert(methodName,
                new MethodRecord(methodName, ctx.t.getText(), am, params));


        SymbolTable.addSymbolTable(new SymbolTable("METHOD " + methodName, line, column, SymbolTable.getSymbolTableByKey(this.scopeStack.peek())));
        this.scopeStack.push(SymbolTable.getLastTableName());

        /* ADD THE PARAMETERS WE SAW TO THE NEW SYMBOL TABLE) */
        for (String prm : params) {
            String[] result = prm.split("~");
            SymbolTable.getSymbolTableByKey(this.scopeStack.peek()).insert(result[1],
                    new VarRecord(result[1], result[0], result[2]));
        }


    }

    @Override
    public void exitMethodDeclaration(MoolaParser.MethodDeclarationContext ctx) {
        this.scopeStack.pop();
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
        int line = ctx.start.getLine();
        int column = ctx.start.getCharPositionInLine();

        List<String> list = new ArrayList<String>(Arrays.asList(ctx.getText().split(",(?![^()]*+\\))"))); //doing split where comma not in parantheses
        list.set(0, list.get(0).replace("var", ""));
        list.set(list.size() - 1, list.get(list.size() - 1).replace(";", ""));
        String varName = ctx.getChild(1).getText();

        for (String elem : list) {
            String[] result = elem.split("=");
            boolean exists = SymbolTable.getSymbolTableByKey(this.scopeStack.peek()).lookupCurScope(result[0]);

            if (exists) {
                FindErrors.errors.add(new Error(103, line, column, "var [" + result[0] + "] has been defined already"));
                result[0] += "_" + line + "_" + column;
            }

            SymbolTable.getSymbolTableByKey(this.scopeStack.peek()).insert(result[0],
                    new VarRecord(result[0], result[1]));
        }
    }

    @Override
    public void exitStatementVarDef(MoolaParser.StatementVarDefContext ctx) {

    }

    @Override
    public void enterStatementBlock(MoolaParser.StatementBlockContext ctx) {
        int line = ctx.start.getLine();
        int column = ctx.start.getCharPositionInLine();
        SymbolTable.addSymbolTable(new SymbolTable("BLOCK", line , column, SymbolTable.getSymbolTableByKey(this.scopeStack.peek())));
        this.scopeStack.push(SymbolTable.getLastTableName());

    }

    @Override
    public void exitStatementBlock(MoolaParser.StatementBlockContext ctx) {
        this.scopeStack.pop();
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
    public void enterStatementAssignment(MoolaParser.StatementAssignmentContext ctx) {

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


    public static void printErrors(ArrayList<Error> ers) {
        for (Error er : ers) {
            System.out.println(er.toString());
        }
    }
//    public void printAllST() {
//
//        Iterator it1 = stt.entrySet().iterator(); //iterating over the symbol tables we have
//        while (it1.hasNext()) {
//            Map.Entry st = (Map.Entry) it1.next();
//            System.out.println("\n" + st.getKey() + " -~-~-~- LINE: " +((SymbolTable) st.getValue()).getLine());
//            System.out.println("--------------");
//            Iterator it2 = ((SymbolTable) st.getValue()).getSt().entrySet().iterator(); //iterating over the entries of the symbol table
//            while (it2.hasNext()) {
//                Map.Entry pair = (Map.Entry) it2.next();
//                System.out.println("Key = " + pair.getKey().toString() + " | " + "Value = " + pair.getValue().toString());
//                it2.remove();
//            }
//            // Print blank line at end of symbol table.
//            it1.remove();
//            System.out.println();
//        }
//    }

}
