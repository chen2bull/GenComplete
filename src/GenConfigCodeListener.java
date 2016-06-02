import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.*;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/6/1.
 */
public class GenConfigCodeListener extends GenConfigBaseListener {
    ParseTreeProperty<String> porpertys = new ParseTreeProperty<String>();
    Map<String, String> map = new LinkedHashMap<String, String>();
    ArrayList<String> singleString = new ArrayList<String>();
    ArrayList<String> singleArrays = new ArrayList<String>();
    Map<String, String> coupleString = new LinkedHashMap<String, String>();

    String getArrayString(String arrayName) {
        if( arrayName.equals("$file")) {
            return "$(ls)";
        } else if(arrayName.equals("$file_only")) {
            return "$(ls -la | awk '/^-/{print $NF}')";
        } else if(arrayName.equals("$dir")) {
            return "$(ls -d */)";
        } else if(arrayName.startsWith("$file.")) {
            return "$(ls " + arrayName.replace("$file.", "*.") + ")";
        } else {
            return map.get("$array" + arrayName);
        }
    }

    String getArrayNameInFunction(String arrayName) {
        if( arrayName.equals("$file")) {
            return "file";
        } else if(arrayName.equals("$file_only")) {
            return "file_only";
        } else if(arrayName.equals("$dir")) {
            return "dir";
        } else if(arrayName.startsWith("$file.")) {
            return arrayName.replaceAll("\\$", "").replaceAll("\\.", "_");
        } else {
            return arrayName.replaceAll("\\$", "");
        }
    }
    void setArrayString(String arrayName, String str) {
        map.put("$array"+arrayName, str);
    }
    String result;

    @Override public void enterFile(GenConfigParser.FileContext ctx) { }

    @Override public void exitFile(GenConfigParser.FileContext ctx) {
        StringBuilder mainBuilder = new StringBuilder();
        mainBuilder.append("function ");
        mainBuilder.append(map.get("mainFunctionName"));
        mainBuilder.append(" {\n\tlocal curarg=${COMP_WORDS[COMP_CWORD]}\n");
        for (String arrayName : singleArrays) {
            mainBuilder.append("\tlocal ");
            mainBuilder.append(getArrayNameInFunction(arrayName));
            mainBuilder.append("=");
            mainBuilder.append(getArrayString(arrayName));
            mainBuilder.append("\n");
        }
        mainBuilder.append("\tlocal comp_all=\"");
        for (String arrayName : singleArrays) {
            mainBuilder.append("\"${");
            mainBuilder.append(getArrayNameInFunction(arrayName));
            mainBuilder.append("}\" ");
        }
        for (String option:singleString) {
            mainBuilder.append(option);
            mainBuilder.append(" ");
        }
        mainBuilder.append("\"\n\tCOMPREPLY=( $(compgen -W \"${__comp_all}\" -- \"${curarg}\") )\n}");

        StringBuilder sb = new StringBuilder();
        sb.append(porpertys.get(ctx.commendDec()));

        sb.append("\t\tlocal curarg=${COMP_WORDS[COMP_CWORD]}\n");
        sb.append("\t\tcase \"${COMP_WORDS[$COMP_CWORD - 1]}\" in\n");
        for (Map.Entry<String, String> entry : coupleString.entrySet()) {
            sb.append("\t\t\t");
            sb.append(entry.getKey());
            sb.append(")\n\t\t\t\tlocal ");
            String arrayName = entry.getValue();
            sb.append(getArrayNameInFunction(arrayName));
            sb.append("=\"");
            sb.append(getArrayString(arrayName));
            sb.append("\"\n\t\t\t\tCOMPREPLY=( $(compgen -W \"${__comp_all}\" -- \"${curarg}\") )\n\t\t\t;;\n");
        }

        sb.append("\t\t\t*)\n");
        sb.append("\t\t\teval ");
        sb.append(map.get("mainFunctionName"));
        sb.append("\n");
        sb.append("\t\t\t;;\n\t\tesac\n\tesac\n}\n");

        sb.append(mainBuilder);
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
        map.put("functionName", functionName.toString());
        map.put("mainFunctionName", functionName+"_main");
        StringBuilder stringBuilder = new StringBuilder("function ");
        stringBuilder.append(functionName);
        stringBuilder.append("() {\n\tcase $COMP_CWORD in\n");
        stringBuilder.append("\t\t0)\n\t\t;;\n\t\t1)\n\t\teval ");
        stringBuilder.append(map.get("mainFunctionName"));
        stringBuilder.append("\n");
        stringBuilder.append("\t\t;;\n");
        porpertys.put(ctx, stringBuilder.toString());
        map.put("fileName", ctx.ID().toString());
    }

    @Override public void enterOptionsDec(GenConfigParser.OptionsDecContext ctx) { }

    @Override public void exitOptionsDec(GenConfigParser.OptionsDecContext ctx) { }

    @Override public void enterOptionStringAndArray(GenConfigParser.OptionStringAndArrayContext ctx) {
        coupleString.put(ctx.String().getText(), ctx.arrayName().getText());
    }

    @Override public void exitOptionStringAndArray(GenConfigParser.OptionStringAndArrayContext ctx) { }

    @Override public void enterOptionSingle(GenConfigParser.OptionSingleContext ctx) {
        singleString.add(ctx.getText());
    }

    @Override public void exitOptionSingle(GenConfigParser.OptionSingleContext ctx) { }

    @Override public void enterOptionArray(GenConfigParser.OptionArrayContext ctx) {
        singleArrays.add(ctx.getText());
    }

    @Override public void exitOptionArray(GenConfigParser.OptionArrayContext ctx) { }

    @Override public void enterArrays(GenConfigParser.ArraysContext ctx) { }

    @Override public void exitArrays(GenConfigParser.ArraysContext ctx) { }

    @Override public void enterArrayDec(GenConfigParser.ArrayDecContext ctx) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree cctx = ctx.getChild(i);
            if( ((TerminalNode) cctx).getSymbol().getType() == GenConfigLexer.String) {
                sb.append(cctx.getText());
                sb.append(" ");
            }
        }
        setArrayString(ctx.Arrayname().getText(), sb.toString());
    }

    @Override public void exitArrayDec(GenConfigParser.ArrayDecContext ctx) {
    }

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
