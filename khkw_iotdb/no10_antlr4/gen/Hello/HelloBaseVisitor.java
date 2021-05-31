// Generated from /Users/jincheng/work/know_how_know_why/khkw_iotdb/no10_antlr4/src/main/antlr4/Hello/Hello.g4 by ANTLR 4.9.1
package Hello;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;

/**
 * This class provides an empty implementation of {@link HelloVisitor},
 * which can be extended to create a visitor which only needs to handle a subset
 * of the available methods.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public class HelloBaseVisitor<T> extends AbstractParseTreeVisitor<T> implements HelloVisitor<T> {
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public T visitWelcome(HelloParser.WelcomeContext ctx) { return visitChildren(ctx); }
}