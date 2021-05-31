package Hello.business;

import Hello.HelloBaseVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;

public class BusinessVisitor extends HelloBaseVisitor<Operator> {
    Operator operator;

    @Override
    public Operator visitTerminal(TerminalNode node) {
        operator.append(node.getSymbol().getText());
        return operator;
    }

    @Override
    protected Operator defaultResult() {
        if(operator == null){
            operator = new Operator();
        }
        return operator;
    }
}
