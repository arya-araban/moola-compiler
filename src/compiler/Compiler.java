package compiler;

import gen.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;

public class Compiler {
    protected static int  cur_example = 7;

    public static void main(String[] args) throws IOException {
        CharStream stream = CharStreams.fromFileName("./sample/Sample"+ cur_example +".mla");
        MoolaLexer lexer = new MoolaLexer(stream);
        TokenStream tokens = new CommonTokenStream(lexer);
        MoolaParser parser = new MoolaParser(tokens);
        parser.setBuildParseTree(true);
        ParseTree tree = parser.program();
        ParseTreeWalker walker = new ParseTreeWalker();
        MoolaListener listener = new ProgramPrinter();
        walker.walk(listener,tree);

        //phase2
        MoolaListener listener2 = new SymbolListener();
        walker.walk(listener2,tree);

        //phase3
        MoolaListener findErrors = new FindErrors();
        walker.walk(findErrors, tree);
    }

}
