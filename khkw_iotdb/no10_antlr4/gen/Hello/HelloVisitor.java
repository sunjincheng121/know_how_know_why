// Generated from /Users/jincheng/work/know_how_know_why/khkw_iotdb/no10_antlr4/src/main/antlr4/Hello/Hello.g4 by ANTLR 4.9.1
package Hello;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link HelloParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface HelloVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link HelloParser#welcome}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWelcome(HelloParser.WelcomeContext ctx);
}