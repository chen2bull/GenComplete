import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.*;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/6/1.
 */
public class GenConfigCodeListener extends GenConfigBaseListener {
    ParseTreeProperty<String> porpertys = new ParseTreeProperty<String>();
    Map<String, String> map = new LinkedHashMap<String, String>();
    String result;

    @Override public void enterFile(GenConfigParser.FileContext ctx) { }

    @Override public void exitFile(GenConfigParser.FileContext ctx) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ctx.getChildCount(); i++) {
            sb.append(porpertys.get(ctx.getChild(i)));
        }
        sb.append("\ncomplete -F ");
        sb.append(map.get("functionName"));
        sb.append(" ");
        sb.append(map.get("fileName"));
        result = sb.toString();
    }

    @Override public void enterCommendDec(GenConfigParser.CommendDecContext ctx) { }

    @Override public void exitCommendDec(GenConfigParser.CommendDecContext ctx) {
        StringBuilder functionName = new StringBuilder().append("__parser_");
        functionName.append(ctx.ID().toString());
        functionName.append("_hub");
        StringBuilder stringBuilder = new StringBuilder("function ");
        stringBuilder.append(functionName);
        stringBuilder.append("() {\n");
        porpertys.put(ctx, stringBuilder.toString());
        map.put("fileName", ctx.ID().toString());
        map.put("functionName", functionName.toString());
    }

    @Override public void enterOptionsDec(GenConfigParser.OptionsDecContext ctx) { }

    @Override public void exitOptionsDec(GenConfigParser.OptionsDecContext ctx) { }

    @Override public void enterOptionList(GenConfigParser.OptionListContext ctx) { }

    @Override public void exitOptionList(GenConfigParser.OptionListContext ctx) { }

    @Override public void enterArrays(GenConfigParser.ArraysContext ctx) { }

    @Override public void exitArrays(GenConfigParser.ArraysContext ctx) { }

    @Override public void enterArrayDec(GenConfigParser.ArrayDecContext ctx) { }

    @Override public void exitArrayDec(GenConfigParser.ArrayDecContext ctx) { }

    @Override public void enterEveryRule(ParserRuleContext ctx) { }

    @Override public void exitEveryRule(ParserRuleContext ctx) { }

    @Override public void visitTerminal(TerminalNode node) { }

    @Override public void visitErrorNode(ErrorNode node) { }

    public static void main(String args[]) throws Exception {
        String inputFile = null;
        if ( args.length>0 ) inputFile = args[0];
        InputStream is = System.in;
        if ( inputFile!=null ) {
            is = new FileInputStream(inputFile);
        }
        ANTLRInputStream input = new ANTLRInputStream(is);
        GenConfigLexer lexer = new GenConfigLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        GenConfigParser parser = new GenConfigParser(tokens);
        parser.setBuildParseTree(true);      // tell ANTLR to build a parse tree
        ParseTree tree = parser.file(); // parse
        // show tree in text form
//        System.out.println(tree.toStringTree(parser));

        ParseTreeWalker walker = new ParseTreeWalker();
        GenConfigCodeListener listener = new GenConfigCodeListener();
        walker.walk(listener, tree);
        System.out.print(listener.result);
    }

}
