package compiler;

import gen.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;

public class Compiler {
    protected static int  cur_example = 6;

    public static void main(String[] args) throws IOException {
        CharStream stream = CharStreams.fromFileName("./sample/Sample"+ cur_example +".mla");
        MoolaLexer lexer = new MoolaLexer(stream);
        TokenStream tokens = new CommonTokenStream(lexer);
        MoolaParser parser = new MoolaParser(tokens);
        parser.setBuildParseTree(true);
        ParseTree tree = parser.program();
        ParseTreeWalker walker = new ParseTreeWalker();
        MoolaListener listener = new ProgramPrinter();
        MoolaListener listener2 = new SymbolListener();
        walker.walk(listener,tree);
        walker.walk(listener2,tree);
    }

}
