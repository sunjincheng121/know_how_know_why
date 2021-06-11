package Hello.business;

import Hello.HelloLexer;
import Hello.HelloParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.tree.ParseTree;

public class LogicalGen {
    public static void main(String[] args) throws Exception {
        String sql = "IoTDB is Powerful";
        Operator op = toLogical(sql);
        System.out.println(op.getStr());
    }

    public static Operator toLogical(String sql) {
        BusinessVisitor visitor = new BusinessVisitor();
        CharStream charStream = CharStreams.fromString(sql);
        HelloLexer lexer = new HelloLexer(charStream);
        CommonTokenStream tokens1 = new CommonTokenStream(lexer);
        HelloParser parser = new HelloParser(tokens1);
        parser.getInterpreter().setPredictionMode(PredictionMode.SLL);
        ParseTree tree;

        tree = parser.welcome();

        return visitor.visit(tree);

    }
}
