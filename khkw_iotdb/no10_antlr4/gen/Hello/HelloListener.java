// Generated from /Users/jincheng/work/know_how_know_why/khkw_iotdb/no10_antlr4/src/main/antlr4/Hello/Hello.g4 by ANTLR 4.9.1
package Hello;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link HelloParser}.
 */
public interface HelloListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link HelloParser#welcome}.
	 * @param ctx the parse tree
	 */
	void enterWelcome(HelloParser.WelcomeContext ctx);
	/**
	 * Exit a parse tree produced by {@link HelloParser#welcome}.
	 * @param ctx the parse tree
	 */
	void exitWelcome(HelloParser.WelcomeContext ctx);
}