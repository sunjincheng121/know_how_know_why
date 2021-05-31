// Generated from /Users/jincheng/work/know_how_know_why/khkw_iotdb/no10_antlr4/src/main/antlr4/Hello/Hello.g4 by ANTLR 4.9.1
package Hello;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class HelloLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.9.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, DESC=2, POWER=3, OPEN=4, APACHE=5, WS=6;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "DESC", "POWER", "OPEN", "APACHE", "WS"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'IoTDB'", null, "'is Powerful'", "'is Open Source'", "'is Apache Project'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, "DESC", "POWER", "OPEN", "APACHE", "WS"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public HelloLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Hello.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\bN\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3"+
		"\3\3\5\3\31\n\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\5\3"+
		"\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3\6"+
		"\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\7\6\7I\n\7"+
		"\r\7\16\7J\3\7\3\7\2\2\b\3\3\5\4\7\5\t\6\13\7\r\b\3\2\3\5\2\13\f\17\17"+
		"\"\"\2P\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2"+
		"\r\3\2\2\2\3\17\3\2\2\2\5\30\3\2\2\2\7\32\3\2\2\2\t&\3\2\2\2\13\65\3\2"+
		"\2\2\rH\3\2\2\2\17\20\7K\2\2\20\21\7q\2\2\21\22\7V\2\2\22\23\7F\2\2\23"+
		"\24\7D\2\2\24\4\3\2\2\2\25\31\5\7\4\2\26\31\5\t\5\2\27\31\5\13\6\2\30"+
		"\25\3\2\2\2\30\26\3\2\2\2\30\27\3\2\2\2\31\6\3\2\2\2\32\33\7k\2\2\33\34"+
		"\7u\2\2\34\35\7\"\2\2\35\36\7R\2\2\36\37\7q\2\2\37 \7y\2\2 !\7g\2\2!\""+
		"\7t\2\2\"#\7h\2\2#$\7w\2\2$%\7n\2\2%\b\3\2\2\2&\'\7k\2\2\'(\7u\2\2()\7"+
		"\"\2\2)*\7Q\2\2*+\7r\2\2+,\7g\2\2,-\7p\2\2-.\7\"\2\2./\7U\2\2/\60\7q\2"+
		"\2\60\61\7w\2\2\61\62\7t\2\2\62\63\7e\2\2\63\64\7g\2\2\64\n\3\2\2\2\65"+
		"\66\7k\2\2\66\67\7u\2\2\678\7\"\2\289\7C\2\29:\7r\2\2:;\7c\2\2;<\7e\2"+
		"\2<=\7j\2\2=>\7g\2\2>?\7\"\2\2?@\7R\2\2@A\7t\2\2AB\7q\2\2BC\7l\2\2CD\7"+
		"g\2\2DE\7e\2\2EF\7v\2\2F\f\3\2\2\2GI\t\2\2\2HG\3\2\2\2IJ\3\2\2\2JH\3\2"+
		"\2\2JK\3\2\2\2KL\3\2\2\2LM\b\7\2\2M\16\3\2\2\2\5\2\30J\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}